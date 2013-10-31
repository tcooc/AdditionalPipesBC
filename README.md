## Update of Additional Pipes for BC3+ and Minecraft 1.5+

### Cloning and Compiling ###

Windows

    REM (optional) SET AP_VERSION=... and BC_VERSION=... to set versions
    git clone https://github.com/PontiacFirebird/AdditionalPipesBC.git    
    cd AdditionalPipesBC
    build.bat

Linux

    # (optional) export AP_VERSION=... and export BC_VERSION=... to set versions
    git clone https://github.com/PontiacFirebird/AdditionalPipesBC.git    
    cd AdditionalPipesBC
    ./build.sh

Compiled binaries can be found in `BuildCraft/bin/ap`.

### Download: [latest releases](https://github.com/PontiacFirebird/AdditionalPipesBC/releases), ([BuildCraft 4.0.2 and earlier](https://www.dropbox.com/sh/0hc1l4bn4dvjlni/t82-3--LmE)) ####
To install, place in the standard minecraft/mods folder.
REQUIRE Forge 650+, BC 3.5.0, See also Logistics Pipes for 1.5.1
### Credits to Zeldo, DaStormBringer, Kyprus, tcooc, gejzer, PontiacFirebird for their work on the mod.

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
