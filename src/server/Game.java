/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 *
 * @author Milos
 */
public class Game {

    GameMaster gm = new GameMaster();
    Room spawn = new Room("construct", "Dvoriste Fakulteta", "[01:24]\n\rDesno se nalazi parkic sa fontanom u centru, okruzenom klupama.\n\rDva Porshea i jedan Bentli na parkingu.\n\rPravo je ulaz fakulteta.\n\r");
    //ground floor
    Room entrance = new Room("entrance", "Ulaz", "Zatvoreni salteri studentske sluzbe.\n\rNeki lik hrce, zaspao pred tv-om.\n\r");
    Room administrativeOffice = new Room("ao", "Studentska sluzba", "Gomila papira i par kompova od pre Hrista\n\rStolice popucale pod pritiskom.\n\r");
    Room wc = new Room("wc", "WC", "Sve okolo je belo, tetkica odradila svoj posao.\n\r");
    Room firstHallGround = new Room("hall1g", "Prizemlje - Prvi hodnik", "Pored wc-a se nalaze 2 ugasena racunara.\n\rPrazan pult za novine.\n\rOglasna tabla sa raznim listama..\n\r");
    Room secondHallGround = new Room("hall2g", "Prizemlje - Drugi hodnik", "Ugasen komp.\n\rOglasna tabla sa rezultatima. \n\rOrmar sa knjigama.\n\r");
    Room room101 = new Room("101", "Sala 101", "Par redova stolica.\n\rOciscena tabla.\n\r");
    Room room102 = new Room("102", "Sala 102", "Par redova stolica.\n\rOciscena tabla.\n\r");
    Room room103 = new Room("103", "Sala 103", "Par redova stolica.\n\rOciscena tabla.\n\r");
    //dungeon
    Room firstHallDungeon = new Room("hall1d", "Podrum - Prvi hodnik", "Prazan hodnik.\n\r");
    Room secondHallDungeon = new Room("hall2d", "Podrum - Drugi hodnik", "Everything around you is white.");
    Room room015 = new Room("015", "Amfiteatar 015", "Veliki amfiteatar sa drvenim stolicama.\n\rU centru se nalazi govornica sa natpisom \"FON\".\n\rPlafon prokisnjava.\n\r ");
    Room consultationRoom = new Room("cr", "Soba za konsultacije", "Everything around you is white.");
    //first floor
    Room firstHallFloor = new Room("hall1f", "Prvi sprat - Prvi hodnik", "Everything around you is white.");
    Room secondHallFloor = new Room("hall2f", "Prvi sprat - Drugi hodnik", "Everything around you is white.");
    Room mathRoom = new Room("mr", "Laboratorija za matematiku", "Everything around you is white.");
    Room room201 = new Room("201", "Sala 201", "Everything around you is white.");

    public Game() {
        createMap();
        entrance.addMob(new Zombie());
        entrance.addMob(new Zombie());
        entrance.addMob(new Zombie());
        firstHallGround.addMob(new Zombie());
        firstHallGround.addMob(new Zombie());
        firstHallGround.addMob(new Zombie());
        firstHallGround.addMob(new Zombie());
        firstHallGround.addMob(new Zombie());
        firstHallGround.addMob(new Zombie());
        firstHallGround.addMob(new Zombie());
        firstHallGround.addMob(new Zombie());
        //entrance.addItem();
        //firstHallGround.addZombie(new Zombie(/*new Item("cd", "Disk +5 (cd)", 5)*/));
        firstHallGround.addItem(new Item("sword", "Sword de LaPlace +20 (sword)", 20));
        room101.addItem(new Item("marker", "Svemoguci marker +5 (marker)", 5));
        room101.addItem(new Item("marker", "Svemoguci marker +5 (marker)", 5));
        room101.addItem(new Item("stolica", "Stolica +10", 10));
        room102.addItem(new Item("marker", "Svemoguci marker +5 (marker)", 5));
        room102.addItem(new Item("stolica", "Stolica +10", 10));
        room102.addItem(new Item("marker", "Svemoguci marker +5 (marker)", 5));
        room103.addItem(new Item("stolica", "Stolica +10", 10));
        room103.addItem(new Item("marker", "Svemoguci marker +5 (marker)", 5));
        //room103.addZombie(new Zombie());
        //room103.addBoss(new Boss("rajko", "Master Rajko Almighty", 20, 150));
        room201.addItem(new Item("marker", "Svemoguci marker (marker)", 5));
    }

    /**
     * Metoda koja kreira celu mapu
     */
    public void createMap() {
        //ground floor
        connectRooms(spawn, "north", entrance);
        connectRooms(entrance, "east", administrativeOffice);
        connectRooms(entrance, "north", firstHallGround);
        connectRooms(firstHallGround, "east", wc);
        connectRooms(firstHallGround, "west", room102);
        connectRooms(firstHallGround, "north", secondHallGround);
        connectRooms(secondHallGround, "west", room101);
        connectRooms(secondHallGround, "east", room103);

        //stairs
        connectRooms(secondHallGround, "down", secondHallDungeon);
        connectRooms(secondHallGround, "up", secondHallFloor);
        connectRooms(entrance, "down", firstHallDungeon);
        connectRooms(entrance, "up", firstHallFloor);


        //dungeon
        connectRooms(firstHallDungeon, "north", secondHallDungeon);
        connectRooms(firstHallDungeon, "west", consultationRoom);
        connectRooms(secondHallDungeon, "west", room015);

        //first floor
        connectRooms(firstHallFloor, "south", mathRoom);
        connectRooms(firstHallFloor, "north", secondHallFloor);
        connectRooms(secondHallFloor, "west", room201);

    }

    /**
     * Metoda koja povezuje 2 sobe
     * @param from
     * @param direction
     * @param to
     */
    public static void connectRooms(Room from, String direction, Room to) {
        if (direction.equalsIgnoreCase("north")) {
            from.north = to;
            to.south = from;
        } else if (direction.equalsIgnoreCase("south")) {
            from.south = to;
            to.north = from;
        } else if (direction.equalsIgnoreCase("west")) {
            from.west = to;
            to.east = from;
        } else if (direction.equalsIgnoreCase("east")) {
            from.east = to;
            to.west = from;
        } else if (direction.equalsIgnoreCase("up")) {
            from.up = to;
            to.down = from;
        } else if (direction.equalsIgnoreCase("down")) {
            from.down = to;
            to.up = from;
        }
    }

    public Room getSpawn() {
        return spawn;
    }

    public GameMaster getGM() {
        return gm;
    }
}
