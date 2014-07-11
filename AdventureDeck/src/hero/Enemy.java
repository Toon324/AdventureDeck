package hero;

import java.util.Random;

import petri.api.GameEngine;
import petri.api.GameImage;

public class Enemy extends NPC {

	public Enemy(GameEngine e, GameImage i) {
		super(e, i);
		// TODO Auto-generated constructor stub
		
		setRandomLocation();
		
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
}
