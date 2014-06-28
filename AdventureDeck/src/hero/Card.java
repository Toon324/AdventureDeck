/**
 * 
 */
package hero;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import petri.api.GameEngine;

/**
 * @author Cody
 * 
 */
public class Card {

	enum CardType {
		BASIC, SPELL, ITEM, SHOP, EQUIPMENT;
	}

	String name = "";
	String description = "";
	BufferedImage image;
	private final int WIDTH = 90;
	private final int HEIGHT = 80;
	private final int SPACING = 20;
	public CardType type;
	private int cost;
	private boolean canChain;
	private int[][] range;
	private LinkedList<String> effects;

	public Card(String n) {
		name = n;
		type = CardType.BASIC;

		effects = new LinkedList<String>();

		File f = new File("src/hero/Cards/" + n + ".card");
		if (f.exists())
			try {
				loadInfo(f);
			} catch (FileNotFoundException e2) {
				e2.printStackTrace();
			}
		try {
			image = ImageIO.read(getClass().getResourceAsStream(
					"Cards/" + n + ".png"));
		} catch (Exception e) {
			System.out.println("Could not read " + n);
			e.printStackTrace();
			try {
				image = ImageIO.read(getClass()
						.getResource("Cards/default.png"));
			} catch (Exception e1) {
				System.out
						.println("Fatal fault: Could not load backup image default.png!");
			}
		}
	}

	/**
	 * @param f
	 * @throws FileNotFoundException
	 */
	private void loadInfo(File f) throws FileNotFoundException {
		// System.out.println("Loading info of " + f.getName());
		Scanner scan = new Scanner(f);
		scan.useDelimiter("\t");

		String section = "";

		while (scan.hasNext()) {
			String scanned = scan.next();
			if (scanned.contains("}"))
				scanned = scanned.replace("}", "");
			
			scanned = scanned.trim();
			
		//	System.out.println(section + " : " + scanned);

			if (scanned.contains("INFO {"))
				section = "INFO";
			else if (scanned.contains("EFFECT {"))
				section = "EFFECT";
			else {

				if (!scanned.equals("}")) {
					if (section == "INFO") {
						if (scanned.equals("name"))
							name = scan.next();
						else if (scanned.equals("type"))
							setType(scan.next());
						else if (scanned.equals("cost"))
							cost = Integer.parseInt(scan.next().trim());
						else if (scanned.equals("range"))
							range = readRange(scan.next());
						else if (scanned.equals("canChain")) {
							String chain = scan.next();
							int i = Integer.valueOf(chain.substring(0,
									chain.indexOf("}")).trim());
							if (i == 0)
								canChain = false;
							else
								canChain = true;
						}
					} else {
						effects.add(scanned);
					}
				}
			}

		}

		scan.close();
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the cost
	 */
	public int getCost() {
		return cost;
	}

	/**
	 * @return the range
	 */
	public int[][] getRange() {
		return range;
	}

	/**
	 * @return the effects
	 */
	public LinkedList<String> getEffects() {
		return effects;
	}

	/**
	 * @param next
	 * @return
	 */
	private int[][] readRange(String rangeText) {
		List<List<String>> yArray = new LinkedList<List<String>>();

		Scanner layer1 = new Scanner(rangeText);
		layer1.useDelimiter("]");

		while (layer1.hasNext()) {
			String line = layer1.next().replace("[", "").trim();

			if (line.equals(""))
				break;

			//System.out.println("Line found: " + line);

			Scanner layer2 = new Scanner(line);
			layer2.useDelimiter(",");

			List<String> xArray = new LinkedList<String>();

			while (layer2.hasNext()) {
				String el = layer2.next();

				if (el.equals(""))
					break;

				xArray.add(el);
				//System.out.println("Element found: " + el);
			}
			yArray.add(xArray);
			layer2.close();
		}

		layer1.close();

		int[][] foundRange = new int[yArray.size()][yArray.get(0).size()];

		for (int y = 0; y < yArray.size(); y++) {
			for (int x = 0; x < yArray.get(y).size(); x++) {
				foundRange[y][x] = Integer.valueOf(yArray.get(y).get(x).trim());
			}
		}

		return foundRange;
	}

	public void draw(Point start, GameEngine engine, Graphics g, int index) {

		Color org = g.getColor();

		// Set color based on card type
		if (type == CardType.BASIC)
			g.setColor(Color.gray);
		else if (type == CardType.SPELL)
			g.setColor(Color.cyan);
		else if (type == CardType.ITEM)
			g.setColor(Color.orange);
		else
			g.setColor(Color.green);

		// Determine position
		int x = start.x + (index * WIDTH) + (index * SPACING);
		int y = start.y;

		// Draw back
		g.fillRect(x, y, WIDTH, HEIGHT);

		// Draw icon
		g.drawImage(image, x + 10, y + 10, x + 70, y + 70, 0, 0,
				image.getWidth(), image.getHeight(), null);

		// Draw text
		g.setColor(Color.BLACK);

		// Reset to original color
		g.setColor(org);

	}

	public boolean checkClick(Point start, GameEngine engine, int index, Point point) {
		Rectangle bounds = new Rectangle(start.x + (index * WIDTH)
				+ (index * SPACING), start.y, WIDTH,
				HEIGHT);

		return bounds.contains(point);

	}

	public String getName() {
		return name;
	}

	public CardType getType() {
		return type;
	}

	/**
	 * @param basic
	 */
	public void setType(String s) {
		if (s.equals("BASIC"))
			type = CardType.BASIC;
		else if (s.equals("SPELL"))
			type = CardType.SPELL;
		else
			type = CardType.ITEM;

	}

	public void setType(CardType t) {
		type = t;
	}

	public boolean canChain() {
		return canChain;
	}

	public String toString() {
		return getName() + " : " + getType();
	}
}
