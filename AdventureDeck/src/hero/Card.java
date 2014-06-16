/**
 * 
 */
package hero;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import petri.api.GameEngine;

/**
 * @author Cody
 * 
 */
public class Card {

	String name = "";
	BufferedImage image;
	private final int WIDTH = 90;
	private final int HEIGHT = 80;
	private final int SPACING = 20;

	public Card(String n) {
		name = n;
		try {
			image = ImageIO.read(getClass().getResourceAsStream(n + ".png"));
		} catch (Exception e) {
			System.out.println("Could not read " + n);
			e.printStackTrace();
		}
	}

	public void draw(GameEngine engine, Graphics g, int index) {
		g.setColor(Color.gray);

		int x = 10 + (index * WIDTH) + (index * SPACING);
		int y = engine.getEnvironmentSize().y - 90;

		g.fillRect(x, y, WIDTH, HEIGHT);

		g.drawImage(image, x + 10, y + 10, x + 70, y + 70, 0, 0,
				image.getWidth(), image.getHeight(), null);

		g.setColor(Color.BLACK);

	}

	public boolean checkClick(GameEngine engine, int index, Point point) {
		Rectangle bounds = new Rectangle(10 + (index * WIDTH)
				+ (index * SPACING), engine.getEnvironmentSize().y - 90, WIDTH,
				HEIGHT);

		return bounds.contains(point);

	}

	public String getName() {
		return name;
	}

}
