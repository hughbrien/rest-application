# Github Project with Dynatrace and Github MCP servers  

A couple of notes : 

The Dynatrace MCP server will typtically running locally using a command that looks like 

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

./.gihub 
./dynatrace-agent-rules

