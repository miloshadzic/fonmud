package server;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class GameMaster implements Runnable {

    LinkedBlockingQueue<Message> commandQueue = new LinkedBlockingQueue<Message>();
    ConcurrentHashMap<String, PlayerCharacter> connectedMap = new ConcurrentHashMap<String, PlayerCharacter>();
    private PlayerCharacter sender;

    @Override
    public void run() {
        //Server.game.createMap();
        while (true) {
            try {
                try {
                    Message message = commandQueue.take();
                    String firstToken = message.getCommands()[0];
                    setSender(message.sender);
                    if (sender.state == State.INBATTLE) {
                        sender.getOut().writeBytes("No way you're fighting for your life!\n\r");
                    } // Quit
                    else if (sender.state == State.ASLEEP) {
                        if (firstToken.equalsIgnoreCase("wake")) {
                            sender.awake();
                            sender.getOut().writeBytes("You stop sleeping.\n\r");
                        } else {
                            sender.getOut().writeBytes("What, in your dreams?\n\r");
                        }
                    }
                    else if (firstToken.equalsIgnoreCase("quit")) {
                        sender = message.sender;
                        // QUIT Handling
                        sender.gm.disconnect(sender.id);
                        sender.conn.socket.close();
                        // MOVEMENT
                    } else if (firstToken.equalsIgnoreCase("look") || firstToken.equalsIgnoreCase("l")) {
                        sender.getOut().writeBytes(sender.currentRoom.getRoomDetails());
                    } else if (firstToken.equalsIgnoreCase("inventory") || firstToken.equalsIgnoreCase("i")) {
                        sender.getOut().writeBytes("\n\rInventory:\n\r-------\n\r");
                        sender.getOut().writeBytes(sender.getInventory());
                        sender.getOut().writeBytes("-------\n\r\n\r");
                    } else if (firstToken.equalsIgnoreCase("north") || firstToken.equalsIgnoreCase("n")) {
                        if (sender.setCurrentRoom(sender.currentRoom.north)) {
                            sender.currentRoom.south.outToRoom(sender.getName() + " leaves north.\n\r");
                        }
                    } else if (firstToken.equalsIgnoreCase("south") || firstToken.equalsIgnoreCase("s")) {
                        if (sender.setCurrentRoom(sender.currentRoom.south)) {
                            sender.currentRoom.north.outToRoom(sender.getName() + " leaves south.\n\r");
                        }
                    } else if (firstToken.equalsIgnoreCase("west") || firstToken.equalsIgnoreCase("w")) {
                        if (sender.setCurrentRoom(sender.currentRoom.west)) {
                            sender.currentRoom.east.outToRoom(sender.getName() + " leaves west.\n\r");
                        }
                    } else if (firstToken.equalsIgnoreCase("east") || firstToken.equalsIgnoreCase("e")) {
                        if (sender.setCurrentRoom(sender.currentRoom.east)) {
                            sender.currentRoom.west.outToRoom(sender.getName() + " leaves east.\n\r");
                        }
                    } else if (firstToken.equalsIgnoreCase("up") || firstToken.equalsIgnoreCase("u")) {
                        if (sender.setCurrentRoom(sender.currentRoom.up)) {
                            sender.currentRoom.down.outToRoom(sender.getName() + " leaves up.\n\r");
                        }
                    } else if (firstToken.equalsIgnoreCase("down") || firstToken.equalsIgnoreCase("d")) {
                        if (sender.setCurrentRoom(sender.currentRoom.down)) {
                            sender.currentRoom.up.outToRoom(sender.getName() + " leaves down.\n\r");
                        }
                    }
                    else if (firstToken.equalsIgnoreCase("say")) {
                        message.setCommand(0, sender.getName() + " says");
                        //Collection<PlayerCharacter> c = connectedMap.values();
                        Iterator<PlayerCharacter> i = sender.currentRoom.characters.iterator();
                        // Build say string
                        StringBuilder sentence = new StringBuilder("\n\r");
                        for (int s = 0; s < message.getCommands().length; s++) {
                            if (s == 1) {
                                sentence.append('\"');
                            }
                            if (s == message.getCommands().length - 1) {
                                sentence.append(message.getCommands()[s]);
                            } else {
                                sentence.append(message.getCommands()[s] + " ");
                            }
                        }
                        sentence.append("\"\n\r\n\r");

                        while (i.hasNext()) {
                            i.next().getOut().writeBytes(sentence.toString());
                        }
                    } // Tell <person> <text>
                    else if (firstToken.equalsIgnoreCase("tell") && message.getCommands().length > 2) {
                        String recieverId = message.getCommands()[1].toLowerCase();
                        if (isConnected(recieverId)) {
                            StringBuilder sentence = new StringBuilder("\n\r" + sender.getName() + " tells you ");
                            StringBuilder senderSentence = new StringBuilder("\n\rYou tell " + connectedMap.get(recieverId).getName() + " ");
                            for (int i = 2; i < message.getCommands().length; i++) {
                                sentence.append(message.getCommands()[i] + " ");
                                senderSentence.append(message.getCommands()[i] + " ");
                            }
                            connectedMap.get(recieverId).getOut().writeBytes(sentence + "\n\r\n\r");
                            connectedMap.get(sender.id).getOut().writeBytes(senderSentence + "\n\r\n\r");
                        } else {
                            connectedMap.get(sender.id).getOut().writeBytes("No such person!" + "\n\r\n\r");
                        }
                    } // Kill
                    else if (firstToken.equals("kill")) {
                        try {
                            String recieverId = message.getCommands()[1];
                            Character defender = sender.currentRoom.getCharacter(recieverId);
                            if (defender != null && defender != sender) {
                                //wake him up
                                //defender.sleep = false; (deprecated)
                                if (defender.state == State.ASLEEP) {
                                    defender.awake();
                                }
                                Battle battle = new Battle(sender, defender, sender.currentRoom);
                                sender.setState(State.INBATTLE);
                                defender.setState(State.INBATTLE);
                                Thread battleThread = new Thread(battle);
                                battleThread.setName(sender.id + "VS" + recieverId);
                                battleThread.start();
                            } else if (defender == sender) {
                                sender.getOut().writeBytes("Why would you like to kill yourself?\n\r\n\r");
                            } else {
                                sender.getOut().writeBytes("Can't see him.\n\r\n\r");
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            sender.getOut().writeBytes("Whom do you wish to kill?");
                        }
                    } // Who is online
                    else if (firstToken.equalsIgnoreCase("who")) {
                        sender.getOut().writeBytes("\n\rOnline:\n\r-------\n\r");
                        Collection<PlayerCharacter> c = connectedMap.values();
                        Iterator<PlayerCharacter> i = c.iterator();
                        while (i.hasNext()) {
                            sender.getOut().writeBytes(i.next().getName());
                            sender.getOut().writeBytes("\n\r");
                        }
                        sender.getOut().writeBytes("-------\n\r\n\r");
                    } // Dancing
                    else if (firstToken.equalsIgnoreCase("dance")) {
                        sender.getOut().writeBytes("\n\rYou go jiggy.\n\r\n\r");
                        Iterator<PlayerCharacter> i = sender.currentRoom.characters.iterator();
                        while (i.hasNext()) {
                            PlayerCharacter pc = i.next();
                            if (pc != sender) {
                                pc.getOut().writeBytes(sender.getName() + " starts dancing like an idiot.\n\r\n\r");
                            }
                        }
                    } // Nod
                    else if (firstToken.equalsIgnoreCase("nod")) {
                        sender.getOut().writeBytes("\n\rYou nod.\n\r\n\r");
                        Iterator<PlayerCharacter> i = sender.currentRoom.characters.iterator();
                        while (i.hasNext()) {
                            PlayerCharacter pc = i.next();
                            if (pc != sender) {
                                pc.getOut().writeBytes(sender.getName() + " nods.\n\r\n\r");
                            }
                        }
                    } // Smile
                    else if (firstToken.equalsIgnoreCase("smile")) {
                        sender.getOut().writeBytes("\n\rYou smile.\n\r\n\r");
                        Iterator<PlayerCharacter> i = sender.currentRoom.characters.iterator();
                        while (i.hasNext()) {
                            PlayerCharacter pc = i.next();
                            if (pc != sender) {
                                pc.getOut().writeBytes(sender.getName() + " smiles.\n\r\n\r");
                            }
                        }
                    } // Wave
                    else if (firstToken.equalsIgnoreCase("wave")) {
                        sender.getOut().writeBytes("\n\rYou wave.\n\r\n\r");
                        Iterator<PlayerCharacter> i = sender.currentRoom.characters.iterator();
                        while (i.hasNext()) {
                            PlayerCharacter pc = i.next();
                            if (pc != sender) {
                                pc.getOut().writeBytes(sender.getName() + " waves.\n\r\n\r");
                            }
                        }
                    } //slap <character>
                    else if (firstToken.equalsIgnoreCase("slap")) {
                        try {
                            String recieverId = message.getCommands()[1].toLowerCase();
                            if (isConnected(recieverId)) {
                                String sentence = "\n\r" + sender.getName() + " slaps you! ";
                                String senderSentence = "\n\rYou slap " + connectedMap.get(recieverId).getName() + "!";
                                connectedMap.get(recieverId).getOut().writeBytes(sentence + "\n\r\n\r");
                                connectedMap.get(sender.id).getOut().writeBytes(senderSentence + "\n\r\n\r");
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            sender.getOut().writeBytes("\n\rYou slap yourself!\n\r\n\r");
                            Iterator<PlayerCharacter> i = sender.currentRoom.characters.iterator();
                            while (i.hasNext()) {
                                PlayerCharacter pc = i.next();
                                if (pc != sender) {
                                    pc.getOut().writeBytes(sender.getName() + " slaps himself! It looks like he might be retarded..\n\r\n\r");
                                }
                            }
                        }
                    }//help && help <command>
                    else if (firstToken.equalsIgnoreCase("help")) {
                        try {
                            //help <command>
                            String command = message.getCommands()[1];
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            //help
                        }
                    } // Get <Item>
                    else if (firstToken.equalsIgnoreCase("get")) {
                        try {
                            //get item
                            String itemId = message.getCommands()[1];
                            int b = 0;
                            for (int i = 0; i < sender.currentRoom.items.size(); i++) {
                                Item item = sender.currentRoom.items.get(i);
                                if (item.getId().equals(itemId)) {
                                    sender.items.add(item);
                                    //damage++
                                    sender.damage += item.getExtraDamage();
                                    b++;
                                    Iterator<PlayerCharacter> it = sender.currentRoom.characters.iterator();
                                    while (it.hasNext()) {
                                        PlayerCharacter pc = it.next();
                                        if (pc != sender) {
                                            pc.getOut().writeBytes(sender.getName() + " picked up " + item.getName() + ".\n\r\n\r");
                                        }
                                    }
                                    sender.getOut().writeBytes("\n\rYou picked up " + item.getName() + ".\n\r\n\r");
                                    sender.currentRoom.items.remove(i);
                                }
                            }
                            if (b == 0) {
                                sender.getOut().writeBytes("\n\rNo such item!\n\r\n\r");
                            }

                        } catch (ArrayIndexOutOfBoundsException ex) {
                            //no such item
                            sender.getOut().writeBytes("\n\rNo such item!\n\r\n\r");
                        }
                    } //drop <Item>
                    else if (firstToken.equalsIgnoreCase("drop")) {
                        try {
                            //drop item
                            String itemId = message.getCommands()[1];
                            int b = 0;
                            for (int i = 0; i < sender.items.size(); i++) {
                                Item item = sender.items.get(i);
                                if (item.getId().equals(itemId)) {
                                    sender.currentRoom.items.add(item);
                                    //damage--
                                    sender.damage -= item.getExtraDamage();
                                    b++;
                                    Iterator<PlayerCharacter> it = sender.currentRoom.characters.iterator();
                                    while (it.hasNext()) {
                                        PlayerCharacter pc = it.next();
                                        if (pc != sender) {
                                            pc.getOut().writeBytes(sender.getName() + " droped item " + item.getName() + ".\n\r\n\r");
                                        }
                                    }
                                    sender.getOut().writeBytes("\n\rYou droped " + item.getName() + ".\n\r\n\r");
                                    sender.items.remove(i);
                                }
                            }
                            if (b == 0) {
                                sender.getOut().writeBytes("\n\rNo such item!\n\r\n\r");
                            }

                        } catch (ArrayIndexOutOfBoundsException ex) {
                            //no such item
                            sender.getOut().writeBytes("\n\rNo such item!\n\r\n\r");
                        }
                    }//sleep
                    else if (firstToken.equalsIgnoreCase("sleep")) {
                        if (sender.currentHitPoints == sender.maximumHitPoints) {
                            sender.getOut().writeBytes("You don't need to sleep. \n\r");
                        } else {
                            // sender.sleep = true;
                            sender.setState(State.ASLEEP);
                            sender.createStateManager();
                            sender.getOut().writeBytes("You go to sleep. \n\r");
                        }
                    }//score
                    else if (firstToken.equalsIgnoreCase("score")) {
                        sender.getOut().writeBytes("\n\rScore:\n\r-------\n\r");
                        sender.getOut().writeBytes("Hitpoints: " + sender.getCurrentHitPoints() + "\n\r");
                        sender.getOut().writeBytes("Damage: " + sender.getDamage() + "\n\r");
                        sender.getOut().writeBytes("Kills: " + sender.getNumKills() + "\n\r");
                        sender.getOut().writeBytes("-------\n\r\n\r");
                    } //help
                    else if (firstToken.equalsIgnoreCase("help") || firstToken.equalsIgnoreCase("h")) {
                        sender.getOut().writeBytes("\n\rLista komandi:\n\r---------------------------------\n\r");
                        sender.getOut().writeBytes("help (ili samo h) - otvara help meni\n\r");
                        sender.getOut().writeBytes("who - spisak online karaktera\n\r");
                        sender.getOut().writeBytes("look (ili samo l) - daje opis sobe, zajedno sa itemima i prisutnim karakterima\n\r");
                        sender.getOut().writeBytes("say <text> - ispisuje text prisutnim karakterima u sobi\n\r");
                        sender.getOut().writeBytes("tell <person> <text> - ispisuje text karakteru sa imenom <person> ako je u sobi\n\r");
                        sender.getOut().writeBytes("smile - karakter se smesi\n\r");
                        sender.getOut().writeBytes("dance - karakter igra\n\r");
                        sender.getOut().writeBytes("wave - karakter mase\n\r");
                        sender.getOut().writeBytes("nod - karakter klima glavom\n\r");
                        sender.getOut().writeBytes("get <item> - stavlja item u inventory\n\r");
                        sender.getOut().writeBytes("drop <item> - baca item iz inventory-ja\n\r");
                        sender.getOut().writeBytes("kill <person> - ulazi u borbu sa osobom\n\r");
                        sender.getOut().writeBytes("con <person> - vraca osnovne informacije o karakteru\n\r");
                        sender.getOut().writeBytes("score - osnovne informacije o karakteru (hitpoints, kills, ...)\n\r");
                        sender.getOut().writeBytes("---------------------------------\n\r\n\r");
                    }//con <character>
                    else if (firstToken.equalsIgnoreCase("con")) {
                        try {
                            String characterName = message.getCommands()[1];
                            Iterator<PlayerCharacter> it = sender.currentRoom.characters.iterator();
                            int b = 0;
                            while (it.hasNext()) {
                                PlayerCharacter pc = it.next();
                                if (pc != sender && pc.getName().equals(characterName)) {
                                    pc.getOut().writeBytes(sender.getName() + " is checking you out.\n\r\n\r");
                                    StringBuilder info = new StringBuilder("-----" + pc.getName() + "-----\n\r");
                                    info.append("Strength: " + pc.getDamage() + "\n\r");
                                    info.append("Hitpoints: " + pc.getCurrentHitPoints() + "\n\r");
                                    for (int i = 0; i < pc.items.size(); i++) {
                                        info.append(pc.items.get(i).getName() + "\n\r");
                                    }
                                    sender.getOut().writeBytes(info + "--------------\n\r\n\r");
                                    b++;
                                }
                            }
                            if (b == 0) {
                                sender.getOut().writeBytes("No such person in this room. \n\r");
                            }

                        } catch (IndexOutOfBoundsException e) {
                            sender.getOut().writeBytes("You must pick someone!\n\r");
                        }
                    }
                    else {
                        sender.getOut().writeBytes("\n\rWTF?!?\n\r\n\r");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int connect(PlayerCharacter character) {
        if (connectedMap.containsKey(character.id)) {
            return -1;
        } else {
            connectedMap.put(character.id, character);
            character.setConnected(true);
        }
        return 1;
    }

    public int disconnect(String id) throws IOException {
        if (connectedMap.containsKey(id)) {
            PlayerCharacter character = connectedMap.get(id);
            character.currentRoom.removeCharacterAndNotify(character);
            character.setConnected(false);
            connectedMap.remove(id);
            return 1;
        } else {
            return -1;
        }
    }

    public boolean isConnected(String id) {
        if (connectedMap.containsKey(id)) {
            System.out.println(id + " connected");
            return true;
        } else {
            System.out.println(id + " not connected");
            return false;
        }
    }

    public void setSender(PlayerCharacter sender) {
        this.sender = sender;
    }
}
