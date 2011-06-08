/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

public interface Attackable {

    public int getCurrentHitPoints();

    public void decreaseHitPoints(int hp);

    public boolean isAttackable();

    public boolean isInBattle();

    public int getDamage();
}
