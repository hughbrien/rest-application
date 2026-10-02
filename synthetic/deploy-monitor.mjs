#!/usr/bin/env node
// Turns every GET request in an IntelliJ .http file into one Dynatrace Browser
// (clickpath) monitor, one navigate step per request. Dynatrace runs the monitor
// in a real Chromium browser from a private Synthetic location, and it appears in
// the Synthetic app (/ui/apps/dynatrace.synthetic/monitors).
//
// Re-running the script updates the existing monitor (matched by name) instead of
// creating a duplicate.
//
// Usage:
//   node deploy-monitor.mjs [--file <path.http>] [--name <monitor name>] [--frequency <minutes>]
//                           [--disable] [--dry-run]
//
// Environment:
//   DT_ENV_URL            Platform URL, e.g. https://uim8926h.sprint.apps.dynatracelabs.com
//   DT_PLATFORM_TOKEN     Platform token (dt0s16.…) with scopes
//                         environment-api:synthetic-monitors:read and environment-api:synthetic-monitors:write
//   DT_LOCATION_IDS       Comma-separated private location IDs, e.g. SYNTHETIC_LOCATION-1234ABCD5678EF90
//   BASE_URL              Rewrites http://localhost:8083 to a host the private location can reach

import { readFileSync } from 'node:fs';
import { dirname, resolve, basename } from 'node:path';
import { fileURLToPath } from 'node:url';

const here = dirname(fileURLToPath(import.meta.url));

// ---------- CLI ----------
const args = process.argv.slice(2);
const opt = (name, fallback) => {
  const i = args.indexOf(name);
  return i >= 0 && args[i + 1] ? args[i + 1] : fallback;
};
const flag = (name) => args.includes(name);

const httpFile = resolve(opt('--file', resolve(here, '../http_requests/test-services.http')));
const monitorName = opt('--name', `rest-application: ${basename(httpFile, '.http')}`);
const frequencyMin = Number(opt('--frequency', 5));
const enabled = !flag('--disable');
const dryRun = flag('--dry-run');

// Platform tokens only work on the platform ("apps") host, so accept the classic
// host too and rewrite it: abc.live.dynatrace.com / abc.sprint.dynatracelabs.com -> ...apps...
const dtEnvUrl = (process.env.DT_ENV_URL || '').replace(/\/+$/, '')
  .replace('.live.dynatrace.com', '.apps.dynatrace.com')
  .replace(/^(https:\/\/[^.]+\.(?:sprint|dev))\.dynatracelabs\.com/, '$1.apps.dynatracelabs.com');
const dtToken = process.env.DT_PLATFORM_TOKEN || process.env.DT_API_TOKEN || '';
const locationIds = (process.env.DT_LOCATION_IDS || '').split(',').map((s) => s.trim()).filter(Boolean);
const baseUrl = (process.env.BASE_URL || '').replace(/\/+$/, '');

// Classic v1 API, proxied by the platform so it accepts platform tokens as Bearer.
const monitorsApi = `${dtEnvUrl}/platform/classic/environment-api/v1/synthetic/monitors`;

// ---------- .http parsing ----------
function parseHttpFile(path) {
  const requests = [];
  // Each request starts at a "###" separator; text after "###" on that line is the title.
  for (const block of readFileSync(path, 'utf8').split(/^###/m)) {
    const lines = block.split(/\r?\n/);
    const reqLine = lines.slice(1).find((l) => /^\s*(GET|POST|PUT|PATCH|DELETE|HEAD|OPTIONS)\s+\S+/i.test(l));
    if (!reqLine) continue;
    const [method, url] = reqLine.trim().split(/\s+/);
    const name = lines[0].trim() || `${method} ${new URL(url).pathname}`;
    if (method.toUpperCase() !== 'GET') {
      console.warn(`Skipping "${name}": a browser monitor can only navigate (GET), not ${method}.`);
      continue;
    }
    requests.push({ name, url: baseUrl ? url.replace(/^https?:\/\/[^/]+/, baseUrl) : url });
  }
  return requests;
}

// ---------- monitor definition ----------
function buildMonitor(requests) {
  return {
    name: monitorName,
    type: 'BROWSER',
    frequencyMin,
    enabled,
    locations: locationIds,
    manuallyAssignedApps: [],
    tags: ['rest-application'],
    script: {
      type: 'clickpath',
      version: '1.0',
      configuration: { device: { deviceName: 'Desktop', orientation: 'landscape' } },
      events: requests.map((r) => ({
        type: 'navigate',
        description: r.name,
        url: r.url,
        wait: { waitFor: 'page_complete' },
      })),
    },
  };
}

// ---------- Dynatrace API ----------
async function dt(method, url, body) {
  const res = await fetch(url, {
    method,
    headers: { 'Content-Type': 'application/json; charset=utf-8', Authorization: `Bearer ${dtToken}` },
    body: body ? JSON.stringify(body) : undefined,
  });
  const text = await res.text();
  if (!res.ok) {
    const hint = res.status === 401 || res.status === 403
      ? '\nHint: check the token is a platform token for this environment with the '
        + 'environment-api:synthetic-monitors:read/write scopes.'
      : '';
    throw new Error(`${method} ${url} -> HTTP ${res.status}: ${text}${hint}`);
  }
  return text ? JSON.parse(text) : null;
}

async function findMonitorId(name) {
  const { monitors = [] } = await dt('GET', `${monitorsApi}?type=BROWSER&tag=rest-application`);
  return monitors.find((m) => m.name === name)?.entityId;
}

// ---------- main ----------
const requests = parseHttpFile(httpFile);
if (requests.length === 0) {
  console.error(`No GET requests found in ${httpFile}`);
  process.exit(1);
}

const local = requests.filter((r) => /\/\/(localhost|127\.0\.0\.1)[:/]/.test(r.url));
if (local.length) {
  console.warn(`Note: ${local.length} step(s) target localhost. That only works if the private location runs on `
    + 'the same machine as the app; otherwise set BASE_URL to an address the location can reach.');
}

const monitor = buildMonitor(requests);
console.log(`Monitor "${monitorName}": ${requests.length} steps, every ${frequencyMin} min, `
  + `locations [${locationIds.join(', ') || 'none'}]`);
requests.forEach((r, i) => console.log(`  ${i + 1}. ${r.name.padEnd(14)} ${r.url}`));

if (dryRun) {
  console.log('--dry-run: monitor not deployed:\n' + JSON.stringify(monitor, null, 2));
  process.exit(0);
}
if (!dtEnvUrl || !dtToken || locationIds.length === 0) {
  console.error('DT_ENV_URL, DT_PLATFORM_TOKEN and DT_LOCATION_IDS must be set (or pass --dry-run).');
  process.exit(2);
}
const ALLOWED_FREQUENCIES = [5, 10, 15, 30, 60, 120, 240];
if (!ALLOWED_FREQUENCIES.includes(frequencyMin)) {
  console.error(`--frequency ${frequencyMin} is not allowed for browser monitors; use one of ${ALLOWED_FREQUENCIES.join(', ')}.`);
  process.exit(2);
}
const badLocations = locationIds.filter((id) => !/^(SYNTHETIC_LOCATION|GEOLOCATION)-[0-9A-F]{16}$/.test(id));
if (badLocations.length) {
  console.error(`Not a location entity ID: ${badLocations.join(', ')}. Use the ID, not the name, e.g. `
    + 'SYNTHETIC_LOCATION-1A2B3C4D5E6F7A8B (Settings > Synthetic > Private Synthetic locations, or the location URL).');
  process.exit(2);
}

const existingId = await findMonitorId(monitorName);
if (existingId) {
  await dt('PUT', `${monitorsApi}/${existingId}`, monitor);
  console.log(`Updated ${existingId}`);
} else {
  const { entityId } = await dt('POST', monitorsApi, monitor);
  console.log(`Created ${entityId}`);
}
console.log(`View it at ${dtEnvUrl}/ui/apps/dynatrace.synthetic/monitors`);
