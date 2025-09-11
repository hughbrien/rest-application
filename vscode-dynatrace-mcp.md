# Github Project with Dynatrace and Github MCP servers  

A couple of notes : 

- [Dynatrace MCP Server ](https://github.com/dynatrace-oss/dynatrace-mcp)

The Dynatrace MCP server will typtically running locally using a command that looks like 

```
bash$  npx -y @dynatrace-oss/dynatrace-mcp-server@0.5.0

[dotenv@17.2.2] injecting env (3) from .env -- tip: ⚙️  write to custom object with { processEnv: myObject }
Initializing Dynatrace MCP Server v0.5.0...

Testing connection to Dynatrace environment: https://id.domain.apps.dynatracelabs.com... (Attempt 1 of 5)

Trying to authenticate API Calls to https://<ID>.<domain>.apps.dynatracelabs.com via OAuthClientId dxyzid.ABCDEFG with the following scopes: app-engine:apps:run, app-engine:functions:run

Using SSO auth URL: https://sso-sprint.dynatracelabs.com/sso/oauth2/token
Successfully retrieved token from SSO!

Successfully connected to the Dynatrace environment at https://id.domain.apps.dynatracelabs.com.

Starting Dynatrace MCP Server v0.5.0...

Connecting server to transport...

Dynatrace MCP Server running on stdio

```


## Configuration and Setup 

We are going to configure the following 
- CoPilot 
- VSCode 
- Dynatrace MCP 
- Github MCP  

### Two local projeccts
https://github.com/hughbrien/frontend
https://github.com/hughbrien/rest-application

### Here are the steps 

- git clone https://github.com/hughbrien/rest-application
- create the .vscode/mcp.json
- create .env file with your tokens
- Run the folloing command "load dynatrace mcp"

### Create the MCP file 
in the .vscode / mcp.json file 
```
{
  "servers": {
    "npx-dynatrace-mcp-server": {
      "command": "npx",
      "cwd": "${workspaceFolder}",
      "args": ["-y", "@dynatrace-oss/dynatrace-mcp-server@latest"],
      "envFile": "${workspaceFolder}/.env"
    }
  }
}

```

### Create the .env file in the project root directory.  
Add this .env file to the .gitignore folder

```
OAUTH_CLIENT_ID=ZZZZZZZ.XXXXXXXXXX
OAUTH_CLIENT_SECRET=ZZZZZZZ.XXXXXXXXXX.TO2C22BV%%%RT%TRFGGGGGGGGGGFFFFDDDDFDDFFFFFFK
DT_ENVIRONMENT=https://<id>.<>.apps.dtlab.com
```

### Need to the Rules from the Dynatrace MCP 
Copy the following files into the .github directory  in hour project : 
https://github.com/dynatrace-oss/dynatrace-mcp

- ./.gihub 
- ./dynatrace-agent-rules

