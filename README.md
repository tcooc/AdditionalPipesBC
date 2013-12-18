## Update of Additional Pipes for BC4.2.1+ and Minecraft 1.6.4

### Cloning and Compiling ###

Windows

    REM (optional) SET AP_VERSION=... and BC_VERSION=... to set versions
    git clone https://github.com/arasium/AdditionalPipesBC.git    
    cd AdditionalPipesBC
    build.bat

Linux

    # (optional) export AP_VERSION=... and export BC_VERSION=... to set versions
    git clone https://github.com/arasium/AdditionalPipesBC.git    
    cd AdditionalPipesBC
    ./build.sh

Compiled binaries can be found in `BuildCraft/bin/ap`.

### Download: [latest releases](https://github.com/arasium/AdditionalPipesBC/releases), ([BuildCraft 4.2.1](http://minecraft.curseforge.com/mc-mods/buildcraft/files/10-build-craft-4-2-1/)) ####
To install, place in the standard minecraft/mods folder.
REQUIRE Forge 953+, BC 4.2.1, See also Logistics Pipes for 1.5.1
### Credits to Zeldo, DaStormBringer, Kyprus, tcooc, gejzer for their work on the mod.

1.2.5 Thread: http://www.minecraftforum.net/topic/856360-125bc2214-bc315rev213rev310-additional-pipes-for-buildcraft-teleport-pipes/

Pipes still generally do the same thing. Some GUIs and recipes have changed (check NEI for recipes).

#### Experimental Pipes & Gate Features ####

##### Power Switch Pipe #####

Just a nifty little pipe with nice texture effects. Complete credits to ABO for the idea (the code is original though).

##### Closed Pipe #####

This pipe has 3 functions:

1. Acts like a normal pipe when transporting items.

2. Acts like a void pipe in that it never drops items.

3. Stores the items it "drops" inside an buffer. This buffer will destroy the oldest stack to make space for new stack when full.

Closed pipes also have a "closed" gate trigger which activates when the buffer has item(s) inside.

##### Water Pump Pipe #####

Just a pipe that fills with water if a source block is under it.
