package hero;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import petri.api.GameEngine;
import petri.api.GameImage;

/**
 * @author Cody
 *
 */
public class EnemyCamp extends NPC {
	
	private final int SPAWN_RATE = 10;
	int spawnCnt = 0;

	/* (non-Javadoc)
	 * @see petri.api.Actor#draw(java.awt.Graphics)
	 */
	/**
	 * @param e
	 * @param i
	 */
	public EnemyCamp(GameEngine e, GameImage i) {
		super(e, i);
		health = 50;
		goldValue = 100;
		setRandomLocation();
	}

	/* (non-Javadoc)
	 * @see hero.NPC#draw(java.awt.Graphics)
	 */
	@Override
	public void draw(Graphics g) {
		g.setColor(Color.black);
		for (int x =0; x < spawnCnt; x++) {
			
			g.fillRect((int)getCenter().x + (6 * x),(int) getCenter().y - 20, 3, 5);
		}
		super.draw(g);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see petri.api.Actor#setDeath(boolean)
	 */
	@Override
	public void setDeath(boolean d) {
		if (d) {
			health = maxHealth;
			setRandomLocation();
		}
	}
	
	public void endTurn() {
		spawnCnt++;
		
		if (spawnCnt >= SPAWN_RATE) {
			spawnCnt = 0;
			spawnEnemy();
		}
	}
	
	/**
	 * 
	 */
	private void spawnEnemy() {
		Enemy e = new Enemy(engine, LocalGame.enemyImage);
		e.setCenter(getCenter().x + 25, getCenter().y + 50);
		engine.getActors().add(e);
	}

	private void setRandomLocation() {
		Random gen = new Random();

		int x = gen.nextInt(engine.getEnvironmentSize().x
				- LocalGame.CARD_TRAY_SIZE - 50)
				+ LocalGame.CARD_TRAY_SIZE;
		int y = gen.nextInt(engine.getEnvironmentSize().y
				- LocalGame.CARD_TRAY_SIZE - 100);
		setCenter(x -= x % 25, y -= y % 25);
	}


}
