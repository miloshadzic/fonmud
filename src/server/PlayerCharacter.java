package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerCharacter extends Character implements Attackable {

    public Connection conn;
    public Game game;
    public GameMaster gm;
    public boolean connected = false;
    private StateManager stateManager;

    public PlayerCharacter(String name, Connection conn, Game game) {
        super.name = name;
        super.id = name.toLowerCase();
        this.conn = conn;
        this.game = game;
        this.gm = game.getGM();
        super.maximumHitPoints = 150;
        super.setCurrentHitPoints(maximumHitPoints);
        super.damage = 15;
    }

    public boolean isAPlayer() {
        return true;
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public Connection getConnection(Connection conn) {
        return conn;
    }

    public boolean connected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean setCurrentRoom(Room room) throws IOException {
        if (room != null) {
            if (currentRoom != null) {
                currentRoom.removeCharacter(this);
            }
            currentRoom = room;
            getOut().writeBytes(currentRoom.getRoomDetails());
            currentRoom.outToRoom(getName() + " has arrived.");
            currentRoom.addPlayer(this);
            return true;
        } else {
            getOut().writeBytes("Can't go that way.\n\r");
            return false;
        }
    }

    public void removeFromRoom() throws IOException {
        currentRoom.removeCharacterAndNotify(this);
        currentRoom = null;
    }

    public void createStateManager() {
        StateManager sm = new StateManager(this);
        Thread thread = new Thread(sm);
        thread.setName(this.name + "_stateManager");
        setStateManager(sm);
        thread.start();
    }

    public void setStateManager(StateManager sm) {
        this.stateManager = sm;
    }

    public StateManager getStateManager() {
        return stateManager;
    }

    public void stopStateManager() {
        stateManager.interrupt();
        stateManager = null;
    }

    @Override
    public void awake() {
        setState(State.NORMAL);
        stopStateManager();
    }

    @Override
    public void die() {
        setState(State.DEAD);
        createStateManager();
    }

    public BufferedReader characterInput() {
        return conn.in;
    }

    public DataOutputStream getOut() {
        return conn.out;
    }

    public void spawn() {
    }

    public void respawn() throws IOException {
        getOut().writeBytes("You have been killed. You respawn at the entrance...\n\r");
        currentRoom = game.getSpawn();
        getOut().writeBytes(currentRoom.getRoomDetails());
        currentRoom.addPlayer(this);
    }

    @Override
    public void run() {
        gm.connect(this);
        try {
            setCurrentRoom(game.getSpawn());
            //
            while (connected()) {
                String sentence;
                if ((sentence = characterInput().readLine().trim()) != null) {
                    String[] commands = sentence.split(" ");
                    Message message = new Message(this, commands);
                    message.push();
                }
            }
            Thread.sleep(500);
        } catch (Exception e) {
            try {
                if (gm.isConnected(id)) {
                    gm.disconnect(id);
                }
            } catch (IOException ex) {
                Logger.getLogger(PlayerCharacter.class.getName()).log(Level.SEVERE, null, ex);
            }
            e.printStackTrace();
        }
    }
}
