package server;

public class Message {

    PlayerCharacter sender;
    private String[] command;

    public Message(PlayerCharacter sender, String[] command) {
        this.sender = sender;
        this.command = command;
    }

    public void push() throws InterruptedException {
        Server.game.getGM().commandQueue.add(this);
    }

    public Character getCharacter() {
        return sender;
    }

    public String[] getCommands() {
        return command;
    }

    public void setCommand(int index, String replacement) {
        command[index] = replacement;
    }
}
