## Update of Additional Pipes for BC3+ and Minecraft 1.5+

### Downloads
Click the releases tab at the top.

### Mod Documentation
[Current CurseForge page is here](https://www.curseforge.com/minecraft/mc-mods/additional-pipes-for-buildcraft).

[an archived version of the forum thread is here](https://web.archive.org/web/20150919105906/http://mod-buildcraft.com/forums/showthread.php?tid=1467).

### Cloning and Compiling ###

Note that you will need JDK 8 as this mod (still) targets Minecraft 1.12. You can download that [here](https://www.openlogic.com/openjdk-downloads).

Also note that you will need gradle 6.x, not older or newer. Gradle 6.8 will be used automatically if you use the `gradlew` script.

    git clone https://github.com/tcooc/AdditionalPipesBC.git
    cd AdditionalPipesBC
    gradlew setupDecompWorkspace
    gradlew build
Compiled binaries can be found in `build/libs`.

    
### Setting Up Eclipse ###
1. Install Eclipse JDK.
2. Run the command `gradlew setupDecompWorkspace --refresh-dependencies eclipse`
3. In Eclipse, go to `File > Import... > General > Existing Projects into Workspace`
4. Hit Next.  Click Browse... in the top right, and select the directory you cloned Additional Pipes into.  Check the box next to AdditionalPipesBC in the Projects list.
5. Hit Finish, and the mod project should be imported.
6. Set up the run configuration.  Go to `File > Import... > Run/Debug > Launch Configurations` and hit next.
7. Click the Browse... button and select the directory you cloned Additional Pipes into.  Check the box next to `AdditionalPipesBC_Client.launch` and `AdditionalPipesBC_Server.launch` in the right pane.
8. Hit finish.
9. Click the down arrow next to the play button on the top bar, select `Run Configurations...`, and click on AP Client 1.8 in the left pane.
10. Click on `AdditionalPipesBC_Client` in the left pane and go to the arguments tab.
11. Replace `<username>` with your Minecraft username.
11.  Hit run.  If it works, you're done!
12.  If it doesn't, it's probably because your `.gradle` folder is not in your home directory, or you have unusual environment variables defined.  Either way, in the Arguments tab, replace `${env_var:userprofile}${env_var:HOME}/.gradle` with the full path to the folder containing your `.gradle` folder.

*Done!*

### Download: [latest releases](https://github.com/tcooc/AdditionalPipesBC/releases) ####
To install, place in the standard minecraft/mods folder.

### Credits to Zeldo, DaStormBringer and [Additional Pipes Contributors](https://github.com/tcooc/AdditionalPipesBC/graphs/contributors) for their work on the mod.

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