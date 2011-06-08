package server;

public class Item {

    final String id;
    final String name;
    final int extraDamage;

    public Item(String id, String name, int extraDamage) {
        this.id = id;
        this.name = name;
        this.extraDamage = extraDamage;
    }

    public String getName() {
        return name;
    }

    public int getExtraDamage() {
        return extraDamage;
    }

    public String getId() {
        return id;
    }




}
