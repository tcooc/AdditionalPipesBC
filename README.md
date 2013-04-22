## Update of Additional Pipes for BC3 and Minecraft 1.4+

### Testing builds: https://www.dropbox.com/sh/tjzx65jqfrwvd73/jHuONV6I5t/AdditionalPipes2.1.4u52-BC3.5.0-MC1.5.1.jar ####
To install, place in the standard minecraft/mods folder.
REQUIRE Forge 650+, BC 3.5.0, See also Logistics Pipes for 1.5.1
### Credits to Zeldo, DaStormBringer, Kyprus, tcooc, gejzer for their work on the mod.

1.2.5 Thread: http://www.minecraftforum.net/topic/856360-125bc2214-bc315rev213rev310-additional-pipes-for-buildcraft-teleport-pipes/

Original Wiki: https://bitbucket.org/Kyprus/additionalpipes/wiki/Crafting

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

##### Phased Signal Trigger (For Gates) #####

This trigger only applies to phased pipes, and activates when any phased pipe with the same frequency *outputs* a pipe wire signal.

Note #1: The other phased pipe has to *output* a pipe signal, not just receive one.

Note #2: Phased signals do not take into account the owner of the pipe, whether the pipe is public/private, or whether the pipe is send/receive.

Note #3: A phased pipe can send a phased signal to itself. Be careful.
