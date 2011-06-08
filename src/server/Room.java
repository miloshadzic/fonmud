package server;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;

public class Room {

    Room north;
    Room south;
    Room west;
    Room east;
    Room up;
    Room down;
    String id;
    String name;
    String description;
    String roomString;

    private final Object itemLock = new Object();
    private final Object characterLock = new Object();
    private final Object mobLock = new Object();

    LinkedList<Item> items = new LinkedList<Item>();
    LinkedList<PlayerCharacter> characters = new LinkedList<PlayerCharacter>();
    LinkedList<Character> mobs = new LinkedList<Character>();

    public Room(Room north, Room south, Room west, Room east, Room up, Room down, String id, String name, String description) {
        this.north = north;
        this.south = south;
        this.west = west;
        this.east = east;
        this.up = up;
        this.down = down;
        this.id = id;
        this.name = name;
        this.description = description;
        this.roomString = ("\n\r" + name + "\n\r\n\r" + description + "\n\r");
    }

    public Room(String id, String name, String description) {
        this.north = null;
        this.south = null;
        this.west = null;
        this.east = null;
        this.up = null;
        this.down = null;
        this.id = id;
        this.name = name;
        this.description = description;
        this.roomString = ("\n\r" + name + "\n\r\n\r" + description);
    }

    public void removeCharacterAndNotify(PlayerCharacter character) throws IOException {
        synchronized (characterLock) {
            this.characters.remove(character);
        }
        outToRoom(character.getName() + " has left the building.\n\r");
    }

    public void removeCharacter(Character character) {
        synchronized (characterLock) {
            characters.remove(character);
        }
        synchronized (mobLock) {
            mobs.remove(character);
        }
    }

    public void addPlayer(PlayerCharacter player) {
        synchronized (characterLock) {
            characters.add(player);
        }
    }

    public void outToRoom(String message) throws IOException {
        for (PlayerCharacter character : characters) {
            character.getOut().writeBytes(message + "\n\r");
        }
    }

    public boolean isInRoom(Character character) {
        if (characters.contains(character)) {
            return true;
        }
        return false;
    }

    public Character getCharacter(String id) {
        for (Character mob : mobs) {
            if (mob.id.equalsIgnoreCase(id)) {
                return mob;
            }
        }
        for (Character player : characters) {
            if (player.id.equalsIgnoreCase(id)) {
                return player;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        if (characters.isEmpty()) {
            return true;
        }
        return false;
    }

    public String getExits() {
        StringBuilder exits = new StringBuilder("Obvious exits : ");
        if (north == null && south == null && west == null && east == null) {
            exits.append("None.");
        } else {
            if (north != null) {
                exits.append("north ");
            }
            if (south != null) {
                exits.append("south ");
            }
            if (west != null) {
                exits.append("west ");
            }
            if (east != null) {
                exits.append("east ");
            }
            if (up != null) {
                exits.append("up ");
            }
            if (down != null) {
                exits.append("down ");
            }
        }
        exits.append("\n\r");
        return exits.toString();
    }

    public String getRoomDetails() {
        StringBuilder roomItems = new StringBuilder();
        StringBuilder roomCharacters = new StringBuilder();
        for (Item item : items) {
            roomItems.append(item.getName() + "\n\r");
        }
        for (Character mob : mobs) {
            roomItems.append(mob.getName() + " is here.\n\r");
        }
        for (PlayerCharacter character : characters) {
            roomCharacters.append(character.getName() + " is here.\n\r");
        }
        return (roomString + getExits() + roomItems + roomCharacters + "\n\r");
    }

    public String getRoomString() {
        return roomString;
    }

    // Getters and Setters (TODO: Check which are nescessary)
    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addMob(Character character) {
        character.currentRoom = this;
        synchronized(mobLock) {
            mobs.add(character);
        }
        Thread thread = new Thread(character);
        thread.start();
    }

    // Sinhronizovane metode za iteme u sobi

    public void addItem(Item item) {
        synchronized (itemLock) {
            items.add(item);
        }
    }

    public void addItemCollection(Collection<Item> items) {
        synchronized (itemLock) {
            this.items.addAll(items);
        }
    }

    public void removeItem(Item item) {
        synchronized (itemLock) {
            this.items.remove(item);
        }
    }
}
