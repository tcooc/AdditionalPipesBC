@ECHO OFF

set CYGWIN=nontsec

IF EXIST ..\build\forge\mcp\src\minecraft (
	echo Syncing Client
	rsync -arv --existing ../build/forge/mcp/src/minecraft/buildcraft/ buildcraft/
)

IF EXIST ..\build\forge\mcp\src\minecraft_server (
	PAUSE

	echo Syncing Server
	rsync -arv --existing ../build/forge/mcp/src/minecraft_server/buildcraft/ buildcraft/
)

PAUSE
