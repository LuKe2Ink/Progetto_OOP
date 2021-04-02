package mapandtiles;

import java.awt.Color;


import java.awt.Graphics2D;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import javax.imageio.ImageIO;
import entity.*;
import game.*;
import utilities.SpriteSheet;
import utilities.AABB;

public class Floor extends GameObject {
	
	private int level;
	private int screenw,screenh;
	private int width;
	private int height;
	private HashMap<Point,Tile> tilestate= new HashMap<>();
	private int tilesize=32;
	private Vector<Leaf> leaves= new Vector<Leaf>();
	private Vector<Vector<Point>> rooms = new Vector<Vector<Point>>();
	private final int MAX_LEAF_SIZE=20;
	private Random r= new Random();
	SpriteSheet sprite;
	private int offsetX=0;
	private int offsetY=0;
	//generate a Floor with level, width ,height,screen width and screen height
   public Floor(int l,int w, int h,int screenw,int screenh) {
	   super(w, h, ID.Floor);
	   this.level=l;
	   this.height=h;
	   this.width=w;
	   this.screenw=screenw;
	   this.screenh=screenh;
	   try 
		{
			sprite = new SpriteSheet(ImageIO.read(new File("data/tiles.png")));
			
		}	catch(IOException e) 
			{
			
			}
	   this.floorGenner(this.width,this.height);
   }

   //generate the pesudorandom floor calling roomscreate
   void floorGenner(int w,int h) {
	   roomsCreate();
	   exitCreate();
	   for(int i=0;i<w/tilesize;i++) {
		   for(int j=0;j<h/tilesize;j++) {
			   if(!(tilestate.containsKey(new Point(i,j))))
		   this.tilestate.put(new Point(i, j), new Tile(new Point(i, j),tiletype.OFF,sprite));
	   }
	   }
	   
	   
   }
   //use a BSP algorithm to create the rooms and halls through a vector of Leaf
   void roomsCreate() {
		Leaf root = new Leaf(0,0,width/tilesize,height/tilesize);
		leaves.add(root);
		boolean didsplit= true;
		while (didsplit)
		{
			didsplit = false;
			for(int i=0; i<leaves.size();i++)
			{ Leaf l= leaves.get(i);
				if (l.leftChild == null && l.rightChild == null) // if this Leaf is not already split...
				{
					// if this Leaf is too big, or 75% chance...
					if (l.w > MAX_LEAF_SIZE || l.h > MAX_LEAF_SIZE ||  r.nextFloat() > 0.25)
					{
						if (l.split()) // split the Leaf!
						{
							// if we did split, push the child leafs to the Vector so we can loop into them next
							leaves.add(l.leftChild);
							leaves.add(l.rightChild);
							didsplit = true;
						}
					}
				}
			}
		}
		root.createRooms(this.tilestate,rooms);
		
		
	}
   
   public void exitCreate() {
		Random r= new Random();
		int a=r.nextInt(rooms.size());
		Point rpos = rooms.get(a).get(r.nextInt(rooms.get(a).size()));
		this.tilestate.replace(rpos, new Tile(rpos,tiletype.Exit,sprite));
		
	}
   
   public void placeEntity(Entity e) {
		Random r= new Random();
		int a=r.nextInt(rooms.size());
		Point rpos = rooms.get(a).get(r.nextInt(rooms.get(a).size()));
		e.setX(rpos.x);
		e.setY(rpos.y);
		if(e.getID()==ID.Player) {
			this.setCamera(e);
		}
			
	}
   public void setCamera(Entity e) {
		if(e.getX()-screenw/64 >0 && e.getX()+screenw/64<width/32) {
			this.offsetX=e.getX()-screenw/64;
		}
			else if (e.getX()-screenw/64 <=0) {this.offsetX=0;}
			else if (e.getX()+screenw/64>=width/32) {this.offsetX=width/32 -screenw/32;}
			if(e.getY()-screenh/64 >0 && e.getY()+screenh/64 < height/32) {
				this.offsetY=e.getY()-screenh/64;
			}
			else if(e.getY()-screenh/64 <=0) {this.offsetY=0;}
			else if(e.getY()+screenh/64 >=height/32) {this.offsetY= height/32-screenh/32;}
			System.out.println("x:"+offsetX+"y:"+offsetY);
		
	}

   @Override
   public void render(Graphics2D g) {
	   g.setColor(Color.gray);
	   for(int i =0;i<screenw/tilesize;i++) {
		   for(int j=0;j <screenh/tilesize;j++) {
				   g.drawImage(tilestate.get(new Point(i+offsetX,j+offsetY)).getImg(),i*tilesize, j*tilesize,null);
			   
		   }
	   }
	   
   }
   
   public int getOffsetX() {return this.offsetX;}
   public int getOffsetY() {return this.offsetY;}
   public void setOffsetX(int x) {this.offsetX=x;}
   public void setOffsetY(int y) {this.offsetY=y;}
   public HashMap <Point,Tile> getMap(){return this.tilestate;}
	
	@Override
	public void tick() {
		
		//cambio degli offset y e x per individuare nella matrice
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void move() {
		if(offsetX+velX<0 || offsetX+velX>width/32-screenw/32) {velX=0;}
		if(offsetY+velY<0 || offsetY+velY>height/32-screenh/32) {velY=0;}
		this.setOffsetX(offsetX+=velX);
		this.setOffsetY(offsetY+=velY);
		velX=0;
		velY=0;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void input(KeyEvent key, List<AABB> collisions) {
		int e = key.getKeyCode();
		
		
		switch (e)
		{
			case KeyEvent.VK_W:
				
				{
					this.setvelY(-1);
					//this.setvelX(0);
					this.move();
				}
				break;
				
			case KeyEvent.VK_A:
				
				{
					this.setvelX(-1);
					//this.setvelY(0);
					this.move();
				}
				break;
				
			case KeyEvent.VK_S:
				
				{
					this.setvelY(1);
					//this.setvelX(0);
					this.move();
				}
				break;
				
			case KeyEvent.VK_D:
			{
					this.setvelX(1);
					//this.setvelY(0);
					this.move();
				}
				break;
				
		}
		// TODO Auto-generated method stub
		
	}





   
}
