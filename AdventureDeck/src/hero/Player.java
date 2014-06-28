/**
 * 
 */
package hero;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import petri.api.Actor;
import petri.api.AnimatedImageActor;
import petri.api.GameEngine;
import petri.api.GameImage;

/**
 * @author Cody
 * 
 */
public class Player extends NPC {

	private final int SPACE_SIZE = 25;

	private int gold;

	public final static int BOWRANGE = 75;

	Player(GameEngine e, GameImage i) {
		super(e, i);
		System.out.println("Image size: " + i.getImage().getWidth() + ","
				+ i.getImage().getHeight());
		maxHealth = 50;
		health = 50;
		gold = 500;
		xTiles = 4;
		yTiles = 4;
		size = new Point2D.Float(30, 50);
		center = new Point2D.Float(400, 400);
		setDir(2);
	}

	public void moveSpace(int spaces) {
		Point2D.Float c = getCenter();

		xTile++;
		if (xTile >= xTiles)
			xTile = 0;

		switch (dir) {
		case 0:
			setCenter(c.x, c.y - (spaces * SPACE_SIZE));
			return;
		case 1:
			setCenter(c.x + (spaces * SPACE_SIZE) / 2, c.y
					- (spaces * SPACE_SIZE) / 2);
			return;
		case 2:
			setCenter(c.x + (spaces * SPACE_SIZE), c.y);
			return;
		case 3:
			setCenter(c.x + (spaces * SPACE_SIZE), c.y
					+ (spaces * SPACE_SIZE) );
			return;
		case 4:
			setCenter(c.x, c.y + (spaces * SPACE_SIZE));
			return;
		case 5:
			setCenter(c.x - (spaces * SPACE_SIZE) , c.y
					+ (spaces * SPACE_SIZE) );
			return;
		case 6:
			setCenter(c.x - (spaces * SPACE_SIZE), c.y);
			return;
		case 7:
			setCenter(c.x - (spaces * SPACE_SIZE), c.y
					- (spaces * SPACE_SIZE) );
			return;
		}
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int x) {
		dir = x;
		switch (dir) {
		case 0:
			yTile = 3;
			return;
		case 1:
			yTile = 3;
			return;
		case 2:
			yTile = 2;
			return;
		case 3:
			yTile = 2;
			return;
		case 4:
			yTile = 0;
			return;
		case 5:
			yTile = 0;
			return;
		case 6:
			yTile = 1;
			return;
		case 7:
			yTile = 1;
			return;
		}
	}

	@Override
	public void draw(Graphics g) {
		try {
			g.drawImage(image.getImage(), (int) (center.x), (int) (center.y),
					(int) (center.x) + (int) (size.x), (int) (center.y)
							+ (int) (size.y), xTile * image.getWidth(), yTile
							* image.getHeight(),
					(xTile + 1) * image.getWidth(),
					(yTile + 1) * image.getHeight(), null);

			g.setColor(Color.red);
			g.fillRect((int) center.x - 5, (int) center.y - 10,
					(int) (health * 1.2), 3);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not draw animatedImage " + toString()
					+ " due to a null image.");
		}
	}

	@Override
	public void move(int ms) {
	}

	/**
	 * @return the gold
	 */
	public int getGold() {
		return gold;
	}

	/**
	 * @param gold
	 *            the gold to set
	 */
	public void addGold(int gold) {
		this.gold += gold;
	}

	public void subtractGold(int amt) {
		gold -= amt;
	}

	public int getYTile() {
		return yTile;
	}

	public int getXTile() {
		return xTile;
	}

	public BufferedImage getImage() {
		return image.getImage();
	}

	public int distanceTo(Actor a) {
		int xDif = (int) ((int) center.x - a.getCenter().x);
		int yDif = (int) ((int) center.y - a.getCenter().y);
		return (int) Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2));
	}

	public void drawBowRange(Graphics g) {
		g.drawRect((int) (center.x - BOWRANGE),
				(int) (center.y - BOWRANGE), (int) (BOWRANGE*2 + size.x),
				(int) (BOWRANGE*2 + size.y));

	}

	/**
	 * @param amt
	 */
	public void addHealth(int amt) {
		health += amt;
		maxHealth += amt;
		
	}

}
