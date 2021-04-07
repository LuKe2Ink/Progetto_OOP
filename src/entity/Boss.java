package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import utilities.*;
import game.*;
import mapandtiles.*;

public class Boss extends Entity{
	
	int column=0;
	Player player_parameter;
	AABB box1;
	boolean collide;
	Clip clip;
	AudioInputStream audio;
	long timer;
	long lastime;
	
	public Boss(int x, int y, ID id, int level, int hp, int attack, int magic_attack, int defence,AbsFloor floor, Player player) throws IOException, LineUnavailableException, UnsupportedAudioFileException{
		super(x, y, id, level, hp, attack, magic_attack, defence, floor);
		// TODO Auto-generated constructor stub
		sprite = new SpriteSheet(ImageIO.read(new File("data/boss.png")));
		this.player_parameter = player;
		this.img_matrix = new BufferedImage[4][3];
		for(int row=0; row<4; row++)
		{
			for(int column=0; column<3; column++)
			{
				img_matrix[row][column] = sprite.grabImage(column+1, row+1, 192, 128); 
			}
		}
		clip = AudioSystem.getClip();
		audio= AudioSystem.getAudioInputStream(new File("data/bonk.wav"));
		clip.open(audio);
		
		this.setDirection(Direction.Down);
		
		img = img_matrix[0][1];

		lastime = System.currentTimeMillis();
		timer = 0;
	}

	@Override
	public void tick() {
		timer += System.currentTimeMillis() - lastime;
		// TODO Auto-generated method stub
		if(timer>=1500)
		{
			switch(this.getDirection())
			{
				case Left:
					img = img_matrix [3][this.column];
					break;
					
				case Down:
					img = img_matrix [2][this.column];
					break;
					
				case Right:
					img = img_matrix [1][this.column];
					break;
					
				case Up:
					img = img_matrix [0][this.column];
					break;
			}
			
			if(this.column==2)
				this.column=0;
			else
				this.column++;
			
			lastime = System.currentTimeMillis();
			timer = 0;
		}
	}

	@Override
	public void move() {
		// TODO Auto-generated method stub
		
		x+=velX;
		y+=velY;
		box.setpos(new Point(x,y));
		velX=0;
		velY=0;
	}

	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		g.setColor(Color.green);
		
		//with a proportion the render function set the hp of the monster
		
		if(this.getHp() > 0) {
			if(this.getHp()==this.getHp()) {
				g.fillRect((x-getFloor().getOffsetX())*32, 
						(y-getFloor().getOffsetY())*32, 
						30, 10);
			}
			else if ( this.getHp()/this.getHp() <= 2)
			{
				g.setColor(Color.orange);
				g.fillRect((x-getFloor().getOffsetX())*32,
						(y-getFloor().getOffsetY())*32-1, 
						(this.getHp()*30)/this.getHp(), 10);
			}
			else if (this.getHp()/this.getHp() <= 3)
			{
				g.setColor(Color.red);
				g.fillRect((x-getFloor().getOffsetX())*32, 
						(y-getFloor().getOffsetY())*32-1, 
						(this.getHp()*30)/this.getHp(), 10);
			}
		}
		g.drawImage(img,(x-getFloor().getOffsetX()-5)*32,
				(y-getFloor().getOffsetY()-3)*32,null);
	}

	@Override
	public void input(KeyEvent key, List<AABB> collisions) {
		// TODO Auto-generated method stub
		box1 = new AABB(new Point(this.getBox().getX(), getBox().getY()), 1, 2);
		collisions.remove(box);
		collide = false;
		
		//the enemy find the position of the player like it is in a cartesian system
		
		if(this.getY()<player_parameter.getY())
		{
			if(!(this.getFloor().getMap().get(new Point((this.x),(this.y+1))).gettype()==tiletype.OFF))
			{
				this.setDirection(Direction.Down);
				box1.sumY(1);	
				this.setvelY(1);
			}
		}
		
		if(this.getY()>player_parameter.getY())
		{
			if(!(this.getFloor().getMap().get(new Point((this.x),(this.y-1))).gettype()==tiletype.OFF))
			{
				this.setDirection(Direction.Up);
				box1.sumY(-1);	
				this.setvelY(-1);
			}
		}
		
		if(this.getX()<player_parameter.getX())
		{
			if(!(this.getFloor().getMap().get(new Point((this.x+1),(this.y+velY))).gettype()==tiletype.OFF))
			{
				this.setDirection(Direction.Right);
				box1.sumX(1);	
				this.setvelX(1);
			}
		}
		
		if(this.getX()>player_parameter.getX())
		{
			if(!(this.getFloor().getMap().get(new Point((this.x-1),(this.y+velY))).gettype()==tiletype.OFF))
			{	
				this.setDirection(Direction.Left);
				box1.sumX(-1);	
				this.setvelX(-1);
			}
		}
		
		//try if there are any other entity in the position where i'm going to go, if not the enemy move
		collisions.forEach(x -> {if(box1.collides(x)) {collide=true;}});
		if(!collide)
			this.move();
		
		else {
				if(box1.collides(player_parameter.getBox()))
				{
					clip.loop(1);
				}
				      
		}
		
		collisions.add(box);
	}

}