# MUD Server/Game for a school project

This was my project for a class on networking/socket programming. It's written in not very good Java. I've written it with two friends who were supposed to be writing the "content" part but they slacked off so there isn't an elaborate storyline or something like that. The most important thing missing is some kind of persistence layer to save state.

There are zombies which "talk" lines from a boring management book and which you can kill.

You can run it with ``ant run``.

Clients can connect using telnet to port 6789.

## In Game Commands
    
    north, east, south, west, up, down - Moving through the map. You can also use n,e,s,w,u and d.
    quit             - Exits the game
    sleep            - Go to sleep. Regenerates hitpoints.
    look             - Shows details of the room.
    inventory        - Shows what you are carrying.
    say              - Say something that everyone in the room can hear.
    tell <character> - Tell someone something. Only that character can hear the message.
    kill <character> - Attack someone. Works with both PCs and NPCs.
    who              - Show who is online at the moment.
    dance            - Dancing
    wave             - Waving
    nod              - Nodding
    smile            - Smiling
    slap <character> - Slap someone.
    help             - Should display help for commands and list commands but it isn't implemented yet.
    get <item>       - Pick up some item. Items give bonuses during combat(corpses don't, sorry).
    drop <item>      - Drop somehting on the ground.
    score            - Tells you how many have you killed and how many hitpoints you have.
    con <character>  - Tells you information you should know before attacking someone.

It's very much incomplete but PvP combat is still fun for about 15 minutes.
