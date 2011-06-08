package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection implements Runnable {

    Socket socket;
    BufferedReader in;
    DataOutputStream out;

    public Connection(Socket socket) {
        this.socket = socket;
    }

    private boolean isUnique(String name) {
        String id = name.toLowerCase();
        if (Server.game.getGM().isConnected(name.toLowerCase())) {
            return false;
        } else {
            return true;
        }

    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
            String name = "";
            out.writeBytes("Ime karaktera: ");
            out.writeBytes("\r\n\r\n");
            boolean unique = false;
            while (unique == false) {
                name = in.readLine();
                if (isUnique(name)) {
                    unique = true;
                } else {
                    out.writeBytes("Someone by that name already connected. Try some other name.\n\r");
                }
            }
            PlayerCharacter character = new PlayerCharacter(name, this, Server.game);
            Thread thread = new Thread(character);
            thread.setName(name);
            thread.start();
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException np) {
            System.out.println("Character did not connect.");
        }
    }
}
