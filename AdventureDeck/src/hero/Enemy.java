package hero;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.Random;

import petri.api.GameEngine;
import petri.api.GameImage;

public class Enemy extends NPC {

	public Enemy(GameEngine e, GameImage i) {
		super(e, i);
		// TODO Auto-generated constructor stub

	}

	public void endTurn() {
		Random gen = new Random();
		dir = gen.nextInt(8);
		moveSpace(1);
		ensureOnBoard();
	}

	/**
	 * 
	 */
	private void ensureOnBoard() {
		if (getCenter().x <= LocalGame.CARD_TRAY_SIZE)
			setCenter(LocalGame.CARD_TRAY_SIZE + 50, getCenter().y);
		else if (getCenter().x > engine.getEnvironmentSize().x - 100)
			setCenter(engine.getEnvironmentSize().x - 150, getCenter().y);
		
		if (getCenter().y <= 0)
			setCenter(getCenter().x, 50);
		else if (getCenter().y >= engine.getEnvironmentSize().y - LocalGame.CARD_TRAY_SIZE - 100)
			setCenter(getCenter().x, engine.getEnvironmentSize().y - LocalGame.CARD_TRAY_SIZE - 150);
	}

	public void moveSpace(int spaces) {
		Point2D.Float c = getCenter();

		// System.out.println("Center: " + getCenter());

		xTile++;
		if (xTile >= xTiles)
			xTile = 0;

		switch (dir) {
		case 0:
			setCenter(c.x, c.y - (spaces * LocalGame.TILE_SIZE));
			return;
		case 1:
			setCenter(c.x + (spaces * LocalGame.TILE_SIZE), c.y
					- (spaces * LocalGame.TILE_SIZE));
			return;
		case 2:
			setCenter(c.x + (spaces * LocalGame.TILE_SIZE), c.y);
			return;
		case 3:
			setCenter(c.x + (spaces * LocalGame.TILE_SIZE), c.y
					+ (spaces * LocalGame.TILE_SIZE));
			return;
		case 4:
			setCenter(c.x, c.y + (spaces * LocalGame.TILE_SIZE));
			return;
		case 5:
			setCenter(c.x - (spaces * LocalGame.TILE_SIZE), c.y
					+ (spaces * LocalGame.TILE_SIZE));
			return;
		case 6:
			setCenter(c.x - (spaces * LocalGame.TILE_SIZE), c.y);
			return;
		case 7:
			setCenter(c.x - (spaces * LocalGame.TILE_SIZE), c.y
					- (spaces * LocalGame.TILE_SIZE));
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hero.NPC#draw(java.awt.Graphics)
	 */
	@Override
	public void draw(Graphics g) {
		dir = 0;
		xTile = 0;
		yTile = 0;
		super.draw(g);
	}

}
