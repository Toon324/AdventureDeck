/**
 * 
 */
package hero;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import petri.api.AnimatedImageActor;
import petri.api.GameEngine;
import petri.api.GameImage;

/**
 * @author Cody
 * 
 */
public class Enemy extends AnimatedImageActor {

	public Enemy(GameEngine e, GameImage i) {
		super(e, i);
		xTiles = 4;
		yTiles = 4;
		xTile = 0;
		yTile = 0;
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
		// g.drawImage((Image)image.getImage(),(int) center.x, (int) center.y,
		// null);
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

	public boolean checkClick(MouseEvent e) {
		System.out.println("Checking click: " + e.getPoint() + " against " + center);
		return ((e.getPoint().x <= center.x + size.x && e.getPoint().x >= center.x) || (e
				.getPoint().y <= center.y + size.y && e.getPoint().y >= center.y));
	}
}
