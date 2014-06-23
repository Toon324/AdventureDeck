/**
 * 
 */
package hero;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;

import petri.api.AnimatedImageActor;
import petri.api.GameEngine;
import petri.api.GameImage;

/**
 * @author Cody
 * 
 */
public class Enemy extends AnimatedImageActor {
	
	private int goldValue;

	public Enemy(GameEngine e, GameImage i) {
		super(e, i);
		xTiles = 4;
		yTiles = 4;
		xTile = 0;
		yTile = 0;
		goldValue = 30;
		maxHealth = 30;
		health = 15;
		size = new Point2D.Float(30, 50);
		center = new Point2D.Float(300, 300);
	}

	@Override
	public void move(int ms) {
	}

	@Override
	public void draw(Graphics g) {

		g.setColor(Color.BLACK);
		
		try {
			g.drawImage(image.getImage(), (int) (center.x), (int) (center.y),
					(int) (center.x + size.x), (int) (center.y)
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

	public boolean checkClick(Point click) {
		//System.out.println("Checking click: " + click + " against " + center);
		return ((click.x <= center.x + size.x && click.x >= center.x) || (click.y <= center.y
				+ size.y && click.y >= center.y));
	}

	/**
	 * @return
	 */
	public int getGoldValue() {
		return goldValue;
	}
}
