package server;

import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StateManager implements Runnable {

    PlayerCharacter player;
    Thread thread;

    public StateManager(PlayerCharacter player) {
        this.player = player;
    }

    public void interrupt() {
        Thread.currentThread().interrupt();
    }

    public void run() {
        try {
            switch (player.state) {

                case ASLEEP:
                    while (player.getCurrentHitPoints() <= player.maximumHitPoints) {
                        Thread.sleep(2000);
                        player.increaseHitPoints(5);

                    }
                    break;
                case DEAD:
                    player.setCurrentHitPoints(player.maximumHitPoints);
                    try {
                        player.respawn();
                    } catch (IOException ex) {
                        Logger.getLogger(StateManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;

            }
        } catch (InterruptedException ex) {
            Logger.getLogger(StateManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        player.stopStateManager();
    }
}
