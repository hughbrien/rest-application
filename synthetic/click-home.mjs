#!/usr/bin/env node
// Opens Google Chrome on the rest-application home page (static/index.html) and
// clicks every "Call /..." button, waiting for each response before the next click.
// The page carries the Dynatrace RUM tag, so each click is captured as a user action.
//
// Usage:
//   node click-home.mjs [--url <home page>] [--loops <n>] [--delay <ms>] [--headless]
//
//   --url       Home page to open (default http://localhost:8083/)
//   --loops     Times to click through all buttons; 0 = until Ctrl+C (default 1)
//   --delay     Pause between clicks in ms, so actions are readable in RUM (default 1000)
//   --headless  Run Chrome without a window

import { chromium } from 'playwright';

const args = process.argv.slice(2);
const opt = (name, fallback) => {
  const i = args.indexOf(name);
  return i >= 0 && args[i + 1] ? args[i + 1] : fallback;
};

const url = opt('--url', 'http://localhost:8083/');
const loops = Number(opt('--loops', 1));
const delayMs = Number(opt('--delay', 1000));
const headless = args.includes('--headless');
const RESPONSE_TIMEOUT_MS = 60_000; // /application and /greeting have intentional delays of 10s+

// Uses the installed Google Chrome rather than Playwright's bundled Chromium.
const browser = await chromium.launch({ channel: 'chrome', headless });
const page = await browser.newPage();
process.on('SIGINT', async () => {
  await browser.close();
  process.exit(0);
});

await page.goto(url, { waitUntil: 'load' });
const buttons = page.locator('#buttons button');
const count = await buttons.count();
console.log(`Opened ${url}: ${count} buttons`);

for (let loop = 1; loops === 0 || loop <= loops; loop++) {
  console.log(`\nLoop ${loop}${loops ? `/${loops}` : ''}`);
  for (let i = 0; i < count; i++) {
    const button = buttons.nth(i);
    const label = await button.textContent();
    const endpoint = label.replace(/^Call /, '');
    // Response boxes are matched by id, which contains a "/", so use an attribute selector.
    const responseBox = page.locator(`[id="response-${endpoint}"]`);

    const start = Date.now();
    await button.click();
    try {
      await page.waitForFunction(
        (el) => !el.textContent.startsWith('Loading'),
        await responseBox.elementHandle(),
        { timeout: RESPONSE_TIMEOUT_MS },
      );
      const status = (await responseBox.textContent()).match(/^Status: (\d+)/)?.[1] ?? 'error';
      console.log(`  ${status.padEnd(5)} ${String(Date.now() - start).padStart(6)} ms  ${endpoint}`);
    } catch {
      console.log(`  timeout ${RESPONSE_TIMEOUT_MS} ms  ${endpoint}`);
    }
    await page.waitForTimeout(delayMs);
  }
}

await browser.close();
