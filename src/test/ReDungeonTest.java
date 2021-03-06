package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import entity.Boss;
import entity.Direction;
import entity.Enemy;
import entity.Player;
import game.CombatSystem;
import game.Game;
import game.Handler;
import game.Id;
import java.awt.AWTException;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import mapandtiles.BossFloor;
import mapandtiles.Floor;
import menu.Difficulty;
import menu.Menu;
import utilities.AaBb;

/**
 * Class test.
 *
 * @author Luigi Incarnato
 *
 */

public class ReDungeonTest {
  public Boss boss;
  public BossFloor bfloor;
  public Floor floor;
  public CombatSystem combat;
  public Player player;
  public AaBb box;
  public Menu menu;
  public Game game;
  public Enemy enemy;
  public KeyEvent key;
  public Handler handler;
  public List<AaBb> collisions;

  public static int level = 1;
  public static int mapWidth = 2000;
  public static int mapHeight = 2000;
  public static int screenWidth = 960;
  public static int screenHeight = 720;
  
  /**
   * initial all.
   *
   * @throws IOException If a function that handler
   *                     call doesn't read a file
   *                  
   * @throws LineUnavailableException   If a function that handler
   *                                    call doens't open a line beacuse
   *                                    it's unavailable
   *                                    
   * @throws UnsupportedAudioFileException  If an audio file isn't supported
   *
   * @throws AWTException Signals that an Abstract Window Toolkit exception has occurred
   */
  @org.junit.Before
  public void initAll() throws IOException, LineUnavailableException, 
      UnsupportedAudioFileException, AWTException {
    box = new AaBb(new Point(), mapWidth, mapHeight);
    //floor = new Floor(level, mapWidth, mapHeight, screenWidth, screenHeight);
    collisions = new ArrayList<>();
    bfloor = new BossFloor(level, mapWidth, mapHeight, screenWidth, screenHeight);
    combat = new CombatSystem(0);
    player = new Player(0, 0, Id.PLAYER, combat, 1, 100, 10, 10, 10, bfloor);

    collisions.add(player.getBox());

    boss = new Boss(32, 32, Id.BOSS, combat, level, bfloor, player);

    enemy = new Enemy(32, 32, Id.ENEMY, combat, level, bfloor, player);

    bfloor.placeEntity(player);

    bfloor.placeEntity(enemy);

    bfloor.placeEntity(boss);

    combat.addBoss(boss);
    combat.addEnemy(enemy);
    combat.addPlayer(player);

    game = new Game(screenWidth, screenHeight, mapWidth, mapHeight, Difficulty.EASY, 0.0, 0.0);
    key = new KeyEvent(game, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 
        0, KeyEvent.VK_1, '0');
  }

  @org.junit.Test
  public void bossTest() throws IOException, LineUnavailableException, 
      UnsupportedAudioFileException {
    /* right init */
    assertEquals(boss.getMaxHp(), boss.getHp());
    /* the boss exp increase ten by ten */
    assertEquals(70, boss.getExpGuaranteed());
    boss.setLevel(2); 
    final Boss boss1 = new Boss(32, 32, Id.BOSS, combat, 
        2, bfloor, player); // by costructor it always have 30 + (leve*10)
    assertEquals(80, boss1.getExpGuaranteed());

    enemy.setLevel(1);
    /*
     * boss by costructor has specif stats depending on floor and player level at
     * the start of the bossfloor it has the same defence as the attack of the
     * player
     */
    assertEquals(510, boss.getMaxHp());
    assertEquals(player.getAttack(), boss.getDefence());
    boss.setMaxHp(500);
    boss.setLevel(2);
    boss.augmStat();
    assertEquals(520, boss.getMaxHp());
    assertEquals(player.getAttack(), boss.getDefence());
  }

  @org.junit.Test
  public void enemyTest() throws IOException, LineUnavailableException, 
      UnsupportedAudioFileException {
    /* right init */
    assertEquals(enemy.getMaxHp(), enemy.getHp());
    /* the enemy exp increase ten by ten getting to next levels */
    assertEquals(40, enemy.getExpGuaranteed());
    final Enemy enemy1 = new Enemy(32, 32, Id.ENEMY, 
        combat, 2, floor, player); // by costructor it always have 30 + (leve*10)
    assertEquals(50, enemy1.getExpGuaranteed());

    enemy.setLevel(1);
    /* enemy by costructor has specif stats depending on floor and player level */
    assertEquals(110, enemy.getMaxHp());
    assertEquals(player.getDefence() / 2 + enemy.getLevel(), enemy.getDefence());
    enemy.setMaxHp(100);
    enemy.setLevel(2);
    enemy.augmStat();
    assertEquals(120, enemy.getMaxHp());
    assertEquals(player.getDefence() / 2 + enemy.getLevel(), enemy.getDefence());

    enemy.setHp(0);
    assertTrue(enemy.isDead());
  }

  @org.junit.Test
  public void playerTest() {
    
    /* right initin */
    assertEquals(player.getMaxHp(), player.getHp());

    assertFalse(player.isDead());

    player.addExp(60); // level up beacuse the player need 50 exp
    assertEquals(2, player.getLevel());
    player.addExp(50); // the player's experience need 75 for the next level
    /* now the player has 60 to 75 exp */
    assertNotEquals(3, player.getLevel());

    /* the player level up and the stats increase */
    assertTrue(player.getMaxHp() > 100);
    assertTrue(player.getAttack() > 10);
    assertTrue(player.getMagicAttack() > 10);
    assertTrue(player.getDefence() > 10);

    /*
     * try to simulate the keys pressed on the keyboard
     */

    /* movement */
    key.setKeyCode(KeyEvent.VK_W);
    player.input(key, collisions);
    assertEquals(Direction.UP, player.getDirection());
    key.setKeyCode(KeyEvent.VK_A);
    player.input(key, collisions);
    assertEquals(Direction.LEFT, player.getDirection());
    key.setKeyCode(KeyEvent.VK_S);
    player.input(key, collisions);
    assertEquals(Direction.DOWN, player.getDirection());
    key.setKeyCode(KeyEvent.VK_D);
    player.input(key, collisions);
    assertEquals(Direction.RIGHT, player.getDirection());
    /* now the player done 4 moves */

    /* attack */
    key.setKeyCode(KeyEvent.VK_J);
    player.input(key, collisions);
    assertTrue(player.isAttacking());
    key.setKeyCode(KeyEvent.VK_K);
    player.input(key, collisions);
    assertTrue(player.isMagicAttacking());
    /*
     * the player only have one magic attack at the start of the game, used one it
     * remains 0 magic attack
     */
    assertEquals(0, player.getSpells());

    player.setHp(0);
    assertTrue(player.isDead());
  }

  @org.junit.Test
  public void combatSystemTest() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
    player.getCombat().playerAttack();
    player.getCombat().damagePlayer("boss", true);
    /*the enemy try to attack the boss, but it has
     * the defence equal to the player attack
     */
    assertEquals(510, boss.getHp());
    player.getInventory().increasePowerStone();
    player.getCombat().lowerBossStats();
    /*after getting a powerStone the boss' defence is
     * halved and the player success to damage the boss
     */
    player.getCombat().damagePlayer("boss", true);
    assertEquals(505, boss.getHp());
    
    /* the flames decrease the 20% of the maxHp */ 
    boss.getCombat().flamesAttack();
    assertEquals(player.getMaxHp() - player.getMaxHp() / 100 * 20, player.getHp());
    
    /* spawn an enemy near the player, and the player damage it */
    
    final Enemy nearEnemy = new Enemy(player.getX() - 1, player.getY(), 
        Id.ENEMY, combat, level, floor, player);
    combat.addEnemy(nearEnemy);
    player.getCombat().playerAttack();
    
    assertEquals(nearEnemy.getMaxHp() - (player.getAttack() - nearEnemy.getDefence()),
        nearEnemy.getHp());
    
    /* the enemy attack the player */
    
    nearEnemy.getCombat().enemyAttack(nearEnemy);
    
    assertEquals(80 - (nearEnemy.getAttack() - player.getDefence()), player.getHp());
  }

}
