package server;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Battle implements Runnable {

    final Room currentRoom;
    LinkedList<Character> attackers = new LinkedList<Character>();
    LinkedList<Character> defenders = new LinkedList<Character>();
    Random randomGenerator = new Random();

    public Battle(Character attacker, Character defender, Room currentRoom) {
        attackers.add(attacker);
        defenders.add(defender);
        this.currentRoom = currentRoom;
    }

    public Character getAttacker() {
        return attackers.getFirst();
    }

    public Character getDefender() {
        return defenders.getFirst();
    }

    public synchronized void addAtacker(Character character) {
        attackers.addLast(character);
    }

    public synchronized void addDefender(Character character) {
        defenders.addLast(character);
    }

    public void removeAttackingHead() {
        getAttacker().die();
        currentRoom.removeCharacter(getAttacker());
        currentRoom.addItem(new Item("corpse", getAttacker().getName() + "'s corpse", 0));
        currentRoom.addItemCollection(getAttacker().items);
        getAttacker().items.removeAll(getAttacker().items);
        attackers.removeFirst();
    }

    public void removeDefendingHead() {
        getDefender().die();
        currentRoom.removeCharacter(getDefender());
        getDefender().currentRoom = null;
        currentRoom.addItem(new Item("corpse", getDefender().getName() + "'s corpse", 0));
        currentRoom.addItemCollection(getDefender().items);
        getDefender().items.removeAll(getDefender().items);
        defenders.removeFirst();
    }

    public void removeAttackers() {
        for (Character attacker : attackers) {
            attacker.setState(State.NORMAL);
        }
        attackers.removeAll(attackers);
    }

    public void removeDefenders() {
        for (Character defender : defenders) {
            defender.setState(State.NORMAL);
        }
        defenders.removeAll(defenders);
    }

    @Override
    public void run() {
        try {
            while (!attackers.isEmpty() && !defenders.isEmpty()) {
                for (Character attacker : attackers) {
                    //random hit
                    if (randomGenerator.nextInt(10) > randomGenerator.nextInt(10)) {
                        int damage = attacker.getDamage();
                        if (damage == 15) {
                            damage -= randomGenerator.nextInt(5);
                        }
                        if (damage > 20) {
                            damage -= randomGenerator.nextInt(7);
                        }
                        if (damage > 30) {
                            damage -= randomGenerator.nextInt(9);
                        }
                        if (damage > 40) {
                            damage -= randomGenerator.nextInt(12);
                        }
                        if (damage > 60) {
                            damage -= randomGenerator.nextInt(20);
                        }
                        if (damage > 100) {
                            damage -= randomGenerator.nextInt(35);
                        }
                        getDefender().decreaseHitPoints(damage);
                        currentRoom.outToRoom(attacker.getName() + " hits " + getDefender().getName() + " for " + damage + " hitpoints.\n\r");
                    } else {
                        currentRoom.outToRoom(attacker.getName() + " mises " + getDefender().getName() + "!\n\r");
                    }
                }
                if (defenders.getFirst().getCurrentHitPoints() <= 0) {
                    currentRoom.outToRoom(getDefender().getName() + " is dead. RIP.\n\r");
                    // Treba za sve
                    getAttacker().numKills++;
                    removeDefendingHead();
                }
                for (Character defender : defenders) {
                    //random hit
                    if (randomGenerator.nextInt(10) >= randomGenerator.nextInt(10)) {
                        int damage = defender.getDamage();
                        if (damage == 15) {
                            damage -= randomGenerator.nextInt(5);
                        }
                        if (damage > 20) {
                            damage -= randomGenerator.nextInt(7);
                        }
                        if (damage > 30) {
                            damage -= randomGenerator.nextInt(9);
                        }
                        if (damage > 40) {
                            damage -= randomGenerator.nextInt(12);
                        }
                        if (damage > 60) {
                            damage -= randomGenerator.nextInt(20);
                        }
                        if (damage > 100) {
                            damage -= randomGenerator.nextInt(35);
                        }
                        getAttacker().decreaseHitPoints(damage);
                        currentRoom.outToRoom(defender.getName() + " hits " + getAttacker().getName() + " for " + damage + " hitpoints.\n\r");
                    } else {
                        currentRoom.outToRoom(defender.getName() + " mises " + getAttacker().getName() + "!\n\r");
                    }
                }
                // Checks if leaders are dead
                if (attackers.getFirst().getCurrentHitPoints() <= 0) {
                    currentRoom.outToRoom(getAttacker().getName() + " is dead. RIP.\n\r");
                    getDefender().numKills++;
                    removeAttackingHead();
                }
                currentRoom.outToRoom("\n\r");
                Thread.sleep(1500);
            }
            removeAttackers();
            removeDefenders();
        } catch (InterruptedException ex) {
            Logger.getLogger(Battle.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
