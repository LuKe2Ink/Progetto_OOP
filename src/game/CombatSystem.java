package game;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import entity.*;
import mapandtiles.tiletype;
import utilities.*;

// this is how raphael works 

public class CombatSystem {
	
	private Player player;
	private Enemy enemy;
	private List<Enemy> enemies;
	private Boss boss;
	private AABB direction_box;
	private boolean collide;
	private BufferedImage punch_img;
	private BufferedImage flame_img;
	private AABB punch_box;
	private int dungeon_level;
	AABB magic_boxes[];

	private Clip bone_sound;
	private AudioInputStream bone_audio;
	
	private Clip punch_sound;
	private AudioInputStream punch_audio;
	
	public CombatSystem() throws IOException, LineUnavailableException, UnsupportedAudioFileException
	{
		this.dungeon_level = 1; 
		enemies = new ArrayList<Enemy>();

		SpriteSheet sprite = new SpriteSheet(ImageIO.read(new File("data/flame.png")));
		flame_img = sprite.grabImage(1, 1, 32, 32); 
		
		punch_img=ImageIO.read(new File("data/punch.png"));

		bone_sound = AudioSystem.getClip();
		bone_audio= AudioSystem.getAudioInputStream(new File("data/bonk.wav"));
		bone_sound.open(bone_audio);
		
		punch_sound = AudioSystem.getClip();
		punch_audio= AudioSystem.getAudioInputStream(new File("data/punch.wav"));
		punch_sound.open(punch_audio);
	}

	public Clip getBonk()
	{
		return bone_sound;
	}
	public Clip getPunch()
	{
		return punch_sound;
	}
	
	public void addPlayer(Player player)
	{
		this.player = player;
	}
	
	public void addEnemy(Enemy enemy)
	{
		this.enemies.add(enemy);
	}
	public void removeEnemy(Enemy enemy)
	{
		this.enemies.remove(enemy);
	}
	
	public void addBoss(Boss boss)
	{
		this.boss = boss;
	}
	
	public void setDungeonLevel()
	{
		this.dungeon_level++;
	}
	
	
	public void render(Graphics2D g)
	{
		try {
			
			if(this.player.isAttacking()) {
				g.drawImage(punch_img, (this.punch_box.getX()-player.getFloor().getOffsetX())*32, (this.punch_box.getY()-player.getFloor().getOffsetY())*32, null);
			   }
			if(this.player.isMagicAttacking())
			{
					for(AABB box : this.magic_boxes)
					{
						g.drawImage(flame_img, (box.getX()-player.getFloor().getOffsetX())*32, (box.getY()-player.getFloor().getOffsetY())*32, null);
					}
			}
		}
		catch(Exception e)
		{}
	}
	
	public void playerAttack()
	{
		collide = false;
		
		switch(this.player.getDirection())
		{
			case Down:
				direction_box = new AABB(new Point(player.getX(), player.getY()+1), 1, 2);
				
				if(!this.enemies.isEmpty())
				{
					enemies.forEach(x -> {if(direction_box.collides(x.getBox())) {collide = true; enemy = x;} });
					this.damagePlayer("enemy",collide);
				}
				else if(this.dungeon_level%5 == 0 && boss!=null)
				{
					if(direction_box.collides(this.boss.getBox()))
					{
						this.collide = true;
						this.damagePlayer("boss",collide);
					}
				}
				
				punch_box = new AABB(new Point(player.getX(), player.getY()+1), 1, 2);
				
				break;
				
			case Left:
				direction_box = new AABB(new Point(player.getX()-1, player.getY()), 1, 2);
				
				if(!this.enemies.isEmpty())
				{
					enemies.forEach(x -> {if(direction_box.collides(x.getBox())) {collide = true; enemy = x;} });
					this.damagePlayer("enemy",collide);
				}
				else if(this.dungeon_level%5 == 0 && boss!=null)
				{
					if(direction_box.collides(this.boss.getBox()))
					{
						this.collide = true;
						this.damagePlayer("boss",collide);
					}
				}
				
				punch_box = new AABB(new Point(player.getX()-1, player.getY()-1), 1, 2);
				
				break;
				
			case Right:
				direction_box = new AABB(new Point(player.getX()+1, player.getY()), 1, 2);
				
				if(!this.enemies.isEmpty())
				{
					enemies.forEach(x -> {if(direction_box.collides(x.getBox())) {collide = true; enemy = x;} });
					this.damagePlayer("enemy",collide);
				}
				else if(this.dungeon_level%5 == 0 && boss!=null)
				{
					if(direction_box.collides(this.boss.getBox()))
					{
						this.collide = true;
						this.damagePlayer("boss",collide);
					}
				}
				
				punch_box = new AABB(new Point(player.getX()+1, player.getY()-1), 1, 2);
				
				break;
				
			case Up:
				direction_box = new AABB(new Point(player.getX(), player.getY()-1), 1, 2);
				
				if(!this.enemies.isEmpty())
				{
					enemies.forEach(x -> {if(direction_box.collides(x.getBox())) {collide = true; enemy = x;} });
					this.damagePlayer("enemy",collide);
				}
				else if(this.dungeon_level%5 == 0 && boss!=null)
				{
					if(direction_box.collides(this.boss.getBox()))
					{
						this.collide = true;
						this.damagePlayer("boss",collide);
					}
				}
				
				punch_box = new AABB(new Point(player.getX(), player.getY()-2), 1, 2);
				
				break;
		}
	}
	
	private void damagePlayer(String type,boolean collide)
	{
		if(!(this.player.getFloor().getMap().get(direction_box.getpos()).gettype()==tiletype.OFF))
		{
			if(type=="boss")
			{
				if(collide)
				{
					boss.setHp(boss.getHp()-(player.getAttack()-boss.getDefence()));
					
					if(!punch_sound.isRunning())
					{
						punch_sound.loop(1);
					}
					
					if(boss.isDead())
					{
						this.player.addExp(this.boss.getExpGuaranteed());
						boss.getBossFloor().exitCreate(boss.getBox().getpos());
						boss=null;
					}
				}
			}
			else if(type=="enemy")
			{
				if(collide)
				{
					enemy.setHp(enemy.getHp()-(player.getAttack()-enemy.getDefence()));
					
					if(!punch_sound.isRunning())
					{
						punch_sound.loop(1);
					}
					
					if(enemy.isDead())
					{
						this.player.addExp(this.enemy.getExpGuaranteed());
						this.removeEnemy(enemy);
					}
				}
			}
		}
	}
	
	public void playerMagicAttack()
	{
		this.player.setSpells();
		AABB magic_boxes[] = 
			{
					new AABB(new Point(this.player.getX()-1,this.player.getY()-1),1,1),
					new AABB(new Point(this.player.getX(),this.player.getY()-1),1,1),
					new AABB(new Point(this.player.getX()+1,this.player.getY()-1),1,1),
					new AABB(new Point(this.player.getX()-1,this.player.getY()),1,1),
					new AABB(new Point(this.player.getX()+1,this.player.getY()),1,1),
					new AABB(new Point(this.player.getX()-1,this.player.getY()+1),1,1),
					new AABB(new Point(this.player.getX(),this.player.getY()+1),1,1),
					new AABB(new Point(this.player.getX()+1,this.player.getY()+1),1,1)	
			};
		
		this.magic_boxes = magic_boxes;
		
		if(this.dungeon_level%5 != 0 && !(this.enemies.isEmpty()))
		{
			for(AABB box : magic_boxes)
			{
				this.enemies.forEach(x -> {

					if(box.collides(x.getBox())) 
					{
						collide = true; 
						this.enemy = x;
					} 
					
					});
				if(collide)
					this.magicDamage();
			}
			
		}
		else if(this.dungeon_level%5 == 0)
			this.magicDamageBoss();
		
	}
	
	public void magicDamage()
	{
		enemy.setHp(enemy.getHp()-this.player.getMagic_attack());
		
		if(enemy.isDead())
		{
			this.player.addExp(enemy.getExpGuaranteed());
			this.removeEnemy(enemy);
		}
	}
	
	public void magicDamageBoss()
	{
		switch(this.player.getInventory().getPowerStone())
		{
			case 1:
				boss.setHp((boss.getHp()-this.player.getMagic_attack())/2);
				break;
				
			case 2:
				boss.setHp((int) ((boss.getHp()-this.player.getMagic_attack())/1.5));
				break;
				
			case 3:
				boss.setHp(boss.getHp()-this.player.getMagic_attack());
				break;
				
		}
		
		if(boss.isDead())
		{
			this.player.addExp(this.boss.getExpGuaranteed());
		}
	}
	
	public void enemyAttack(Enemy enemy)
	{
		if(enemy.getAttack()-player.getDefence()>0)
			player.setHp(player.getHp()-(enemy.getAttack()-player.getDefence()));

		bone_sound.loop(1);
	}
	
	public void bossAttack(Boss boss)
	{
		if(boss.getAttack()-player.getDefence()>0)
			player.setHp(player.getHp()-(boss.getAttack()-player.getDefence()));

		bone_sound.loop(1);
	}
	
	public void flamesAttack()
	{
		player.setHp(player.getHp()-((int) (player.getMax_hp()*20/100)));
	}
	
	public void lowerBossStats()
	{
		switch(player.getInventory().getPowerStone())
		{
			case 0:
				break;
				
			case 1:
				this.boss.setAttack(this.boss.getAttack()/2);
				this.boss.setDefence(this.boss.getDefence()/2);
				break;
				
			case 2:
				this.boss.setAttack(this.boss.getAttack()/2);
				this.boss.setDefence(this.boss.getDefence()/2);
				break;
				
			case 3:
				this.boss.setAttack(this.boss.getAttack()/2);
				this.boss.setDefence(0);
				break;
		}
	}
}
