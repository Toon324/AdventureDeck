package hero;

import java.awt.Graphics;

import petri.api.AnimatedImageActor;
import petri.api.GameEngine;
import petri.api.GameImage;

/**
 * @author Cody
 * 
 */
public class DamageActor extends AnimatedImageActor {

	long lastUpdate = 0;

	/**
	 * @param e
	 * @param i
	 */
	public DamageActor(GameEngine e, GameImage i) {
		super(e, i);
		xTile = 0;
		yTile = 0;
		xTiles = 5;
		yTiles = 1;
		setSize(100, 100);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petri.api.AnimatedImageActor#draw(java.awt.Graphics)
	 */
	@Override
	public void draw(Graphics g) {

		g.drawImage(image.getImage(), (int) (center.x), (int) (center.y),
				(int) (center.x + size.x), (int) (center.y) + (int) (size.y),
				xTile * image.getWidth(), yTile * image.getHeight(),
				(xTile + 1) * image.getWidth(),
				(yTile + 1) * image.getHeight(), null);
		super.draw(g);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petri.api.AnimatedImageActor#move(int)
	 */
	@Override
	public void move(int ms) {
		long elapsed = System.currentTimeMillis() - lastUpdate;

		if (elapsed > 100) {
			xTile++;
			lastUpdate = System.currentTimeMillis();
			if (xTile > xTiles)
				death = true;
		}
	}

}
