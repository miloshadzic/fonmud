package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasa karaktera Zombie
 */
public class Zombie extends Character {

    public final LinkedList<String> brains = new LinkedList<String>();
    final static String defaultId = "zombie";
    final static String defaultName = "Zombie";
    final static int defaultHp = 100;
    final static int defaultDamage = 10;
    final static LinkedList<Item> defaultItems = new LinkedList<Item>();
    Random randomGenerator;

    public Zombie() {
        super.id = defaultId;
        super.name = defaultName;
        super.maximumHitPoints = defaultHp;
        super.currentHitPoints = defaultHp;
        super.damage = defaultDamage;
        super.items = defaultItems;
        randomGenerator = new Random();
        loadBrains();
    }

    public Zombie(int maxHitPoints, LinkedList<Item> items) {
        super.id = "zombie";
        super.name = "Zombie";
        super.maximumHitPoints = maxHitPoints;
        super.currentHitPoints = maxHitPoints;
        super.damage = 10;
        super.items = items;
        loadBrains();
    }

    private void loadBrains() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("brains" + File.separator + id + ".txt"));
            while (br.ready()) {
                brains.add(br.readLine());
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(Zombie.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isAPlayer() {
        return false;
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (String s : brains) {
                    Thread.sleep(randomGenerator.nextInt(10) * 5000);
                    currentRoom.outToRoom(getName() + " says: \"" + s + "\"");
                }
            } catch (IOException ex) {
                Logger.getLogger(Zombie.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Zombie.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
