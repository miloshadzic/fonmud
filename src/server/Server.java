package server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    static Game game = new Game();

    public static void main(String[] args) throws Exception {

        ServerSocket welcomeSocket = new ServerSocket(6789);
        Thread gmThread = new Thread(game.getGM());
        gmThread.start();
        while (true) {
            Socket socket = welcomeSocket.accept();
            //Connection conn = new Connection(socket);
            Thread connectionThread = new Thread(new Connection(socket));
            connectionThread.start();
        }

    }
}
