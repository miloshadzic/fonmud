package server;

import java.util.LinkedList;

public abstract class Character implements Runnable, Attackable {

    String id;
    String name;
    int damage;
    int currentHitPoints;
    int maximumHitPoints;
    int numKills;
    final Object lock = new Object();
    State state = State.NORMAL;
    Room currentRoom;
    LinkedList<Item> items = new LinkedList<Item>();

    public String getName() {
        return name;
    }

    public boolean isInBattle() {
        return state == State.INBATTLE;
    }

    public void setState(State state) {
        this.state = state;
    }

    public abstract boolean isAPlayer();

    public int getDamage() {
        return damage;
    }

    public int getCurrentHitPoints() {
        return currentHitPoints;
    }

    public void setCurrentHitPoints(int hitPoints) {
        this.currentHitPoints = hitPoints;
    }

    public void increaseHitPoints(int hp) {
        currentHitPoints = currentHitPoints + hp;
    }

    public void decreaseHitPoints(int hp) {
        synchronized (lock) {
            currentHitPoints = currentHitPoints - hp;
        }
    }

    public boolean isAttackable() {
        return true;
    }

    public boolean isNotDead() {
        return (getCurrentHitPoints() > 0);
    }

    public boolean isDead() {
        return (getCurrentHitPoints() < 0);
    }

    public String getInventory() {
        String inventory = "";
        for (int i = 0; i < items.size(); i++) {
            inventory += items.get(i).getName() + "\n\r";
        }
        return inventory;
    }

    public int getNumKills() {
        return numKills;
    }

    public void setNumKills(int numKills) {
        this.numKills = numKills;
    }

    public void awake() {
        state = State.NORMAL;
    };

    public void die() {
        state = State.DEAD;
    }

    abstract public void run();
}
