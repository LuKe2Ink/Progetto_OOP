package game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import mapandtiles.*;
import entity.*;
import gachagame.main.Floor;

//nabi

public class Game extends Canvas implements Runnable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8647713295702872480L;
	
	public static final int WIDTH =1600, HEIGHT=WIDTH/12*9;
	public static final int MAPW=1600,MAPH=HEIGHT;
	
	private Thread thread; 
	
	private boolean running= false;
	private Floor f;
	
	private Handler handler;

	public Game() throws IOException {
		handler=new Handler();
		this.addKeyListener(new KeyInput(handler));
		new Window(WIDTH,HEIGHT,"Re:Dungeon",this);
		this.f= new Floor(1,MAPW,MAPH,WIDTH,HEIGHT);
		handler.addObject(f);
		handler.addObject(new Player(32, 32, ID.Player, 1, 100, 32, 28, 5));
		handler.addObject(new Enemy(128, 128, ID.Enemy, 1, 100, 32, 28, 5, (Player) handler.object.get(0)));
	}
	
	public synchronized void start() {
		thread= new Thread(this);
		thread.start();
		running = true;
		
	}
	
	public synchronized void stop() {
		try {
			thread.join();
			running=false;
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		long lastTime= System.nanoTime();
		double numberofticks=60.0;
		double nanoseconds= 1000000000/numberofticks;
		double delta=0;
		long timer= System.currentTimeMillis();
		int frames=0;
		while(running) {
			long now= System.nanoTime();
			delta+=(now-lastTime)/nanoseconds;
			lastTime=now;
			while(delta>=1) {
				tick();
				delta=0;
			}
			if(running) {
				render();
			}
			frames++;
			
			if(System.currentTimeMillis()-timer>1000) {
				timer+=1000;
				frames=0;
			}
		}
		stop();
		
	}
	
	private void tick() {
		handler.tick();
	}
	
	private void render() {
		BufferStrategy bs= this.getBufferStrategy();
		if(bs==null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics2D g= (Graphics2D) bs.getDrawGraphics();
		
		g.setColor(Color.black);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		handler.render(g);
		
		g.dispose();
		bs.show();
	}
	public static void main(String[] args) throws IOException {
		new Game();
	}

}