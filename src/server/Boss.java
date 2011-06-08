/*
 * Boss klasa
 * Boss je gospodar sobe. 
 * Napada automatski na prvi pogled.
 * Mada moze da se porazi bez dodatne opreme, pozeljno je oruzje koje je slaba tacka napadnutom bossu. Ono se moze naci negde na mapi...
 * Regenerise se po stopi 5hp/sec, ako nije u borbi
 * Priziva zombija u pomoc kad vidi da ne gubi (max 3)
 */
package server;

import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author solmax
 */
public class Boss extends Character {

    public LinkedList<String> bossBrains = new LinkedList<String>();

    public Boss(String id, String name, int damage, int hitPoints) {
        super.id = id;
        super.name = name;
        super.currentHitPoints = super.maximumHitPoints = hitPoints;
        super.damage = damage;
        super.numKills = 0;
        bossBrains.add("Now.. YOU DIE!");
        bossBrains.add("Noweone can fire me!! I'm indispensable!");
        bossBrains.add("AAAGHH!! Don't...Sit...on..the..RADIATORS!!");
    }

    @Override
    public void run() {
        /*
        Random radnomGenerator = new Random();
        while(true){
        try{
        if(!inBattle){
        if(currentRoom.characters.size() !=0){
        currentRoom.outToRoom("[Boss]: "+bossBrains.get(radnomGenerator.nextInt(bossBrains.size()))+"\n\r");
        Thread.currentThread().sleep(2000);
        PlayerCharacter defender = currentRoom.characters.get(0);
        Battle battle = new Battle(this, defender, currentRoom);
        currentRoom.battle = battle;
        this.inBattle = true;
        defender.inBattle = true;
        Thread battleThread = new Thread(battle);
        battleThread.setName(this.id + "VS" + defender.getName());
        battleThread.start();
        }else{
        if(currentHitPoints > maximumHitPoints){
        currentHitPoints = maximumHitPoints;
        }else{
        currentHitPoints += 5;
        Thread.currentThread().sleep(1000);
        }
        }
        }else{
        //if dead
        if(currentHitPoints <= 0){
        currentRoom.outToRoom("[Boss]: NOOOO!!!\n\r");
        Thread.currentThread().sleep(30000);
        }
        //summon zombie to assist
        if(currentHitPoints < 50 && currentRoom.mobs.size()< 3){
        currentRoom.addZombie(new Zombie(null));
        currentRoom.outToRoom("[Boss]: My faitfull zombie will assist me! \n\r");
        }
        }
        }catch(Exception e){e.printStackTrace();}
        }
         */
    }

    @Override
    public boolean isAPlayer() {
        return false;
    }
}
