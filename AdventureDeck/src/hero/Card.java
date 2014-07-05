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
		BASIC, SPELL, ITEM, UI, EQUIPMENT;
	}

	public CardType type;

	String name = "";
	String description = "";
	BufferedImage image;

	// Info for drawing the card. Will be replaced with new card design soon
	private final int WIDTH = 90;
	private final int HEIGHT = 80;
	private final int SPACING = 20;

	private int cost;
	private boolean canChain; // Currently unused and likely to be removed
	private int[][] range;
	private LinkedList<String> effects;

	/**
	 * Fetches the image and .card file based on the name passed in. Files are
	 * located in /Cards/. If an image is not found, a default image is loaded.
	 * 
	 * @param n
	 *            name of card, should be same as file names
	 */
	public Card(String n) {
		name = n;
		type = CardType.BASIC;

		effects = new LinkedList<String>();

		File f = new File("src/hero/Cards/" + n + ".card"); // Not relative,
															// will probably
															// cause issues
															// during export to
															// .jar
		if (f.exists())
			try {
				loadInfo(f);
			} catch (FileNotFoundException e2) {
				GameEngine.log(e2.getMessage());
				e2.printStackTrace();
			}

		try {
			// Fetch the image
			image = ImageIO.read(getClass().getResourceAsStream(
					"Cards/" + n + ".png"));
		} catch (Exception e) {
			GameEngine.log("Could not read " + n);
			e.printStackTrace();
			try {
				// Load default image if image not found
				image = ImageIO.read(getClass()
						.getResource("Cards/default.png"));
			} catch (Exception e1) {
				GameEngine
						.log("Fatal fault: Could not load backup image default.png!");
			}
		}
	}

	/**
	 * Reads in the data from the .card file. Should ignore unrecognized
	 * commands.
	 * 
	 * @param f
	 *            File to read from
	 * @throws FileNotFoundException
	 *             if the File is not found (should never be thrown in the
	 *             context of use)
	 */
	private void loadInfo(File f) throws FileNotFoundException {
		// GameEngine.log("Loading info of " + f.getName());

		Scanner scan = new Scanner(f);
		scan.useDelimiter("\t"); // Tab delimination

		String section = "";

		while (scan.hasNext()) {
			String scanned = scan.next();
			if (scanned.contains("}"))
				scanned = scanned.replace("}", "");

			scanned = scanned.trim();

			// GameEngine.log(section + " : " + scanned);

			if (scanned.contains("INFO {"))
				section = "INFO";
			else if (scanned.contains("EFFECT {"))
				section = "EFFECT";

			else {
				// Feed basic info into object
				if (!scanned.equals("}")) {
					if (section == "INFO") {
						if (scanned.equals("name"))
							name = scan.next();
						else if (scanned.equals("type"))
							setCardType(scan.next());
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
					} else
						effects.add(scanned); // Effects can be as long as they
												// want (up to max size) thus
												// the need for a List

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
	 * Range is represented as an array of int arrays, in the order of range [y]
	 * [x]. Range is left to right (row) and top to bottom (array of rows). This
	 * is done to give an easy in-text visualization of the range output.
	 * 
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
	 * Converts the .card format of range (ex: [0, 0, 1, 0, 0] [1, 1, 0, 1, 1])
	 * into an array of array of ints.
	 * 
	 * @param next
	 * @return
	 */
	private int[][] readRange(String rangeText) {
		List<List<String>> rowsArray = new LinkedList<List<String>>();

		// Read in the range row by row (ex: row 1 would be [0, 0, 1, 0, 0])
		Scanner row = new Scanner(rangeText);
		row.useDelimiter("]");

		while (row.hasNext()) {
			String line = row.next().replace("[", "").trim(); // Remove any
																// unnecessary
																// characters

			if (line.equals(""))
				break;

			// GameEngine.log("Line found: " + line);

			// Read in the range elements (ex: first element would be 0, second
			// 0, third 1. . . and last 0)
			Scanner element = new Scanner(line);
			element.useDelimiter(",");

			List<String> singleRowArray = new LinkedList<String>();

			while (element.hasNext()) {
				String el = element.next();

				// An empty element means we are done with the row
				if (el.equals(""))
					break;

				singleRowArray.add(el);
				// GameEngine.log("Element found: " + el);
			}
			rowsArray.add(singleRowArray);
			element.close();
		}

		row.close();

		// Make sure the range array is exactly the size it needs to be, and
		// then copy the elements into it
		int[][] foundRange = new int[rowsArray.size()][rowsArray.get(0).size()];

		for (int y = 0; y < rowsArray.size(); y++) {
			for (int x = 0; x < rowsArray.get(y).size(); x++) {
				foundRange[y][x] = Integer.valueOf(rowsArray.get(y).get(x)
						.trim());
			}
		}

		return foundRange;
	}

	/*
	 * Draws the card, based on a start point for the row of cards, and the
	 * index of where the card is in that row. In the future, the index could be
	 * removed with an updated start Point in the outer array.
	 */
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

	/**
	 * Checks to see if the card has been clicked. Uses same Point and index
	 * mechanic as draw().
	 * 
	 * @param start
	 * @param engine
	 * @param index
	 * @param point
	 * @return
	 */
	public boolean checkClick(Point start, GameEngine engine, int index,
			Point point) {
		Rectangle bounds = new Rectangle(start.x + (index * WIDTH)
				+ (index * SPACING), start.y, WIDTH, HEIGHT);

		return bounds.contains(point);

	}

	public String getName() {
		return name;
	}

	public CardType getCardType() {
		return type;
	}

	/**
	 * This may be able to be simplified with some Enum values
	 * 
	 * @param basic
	 */
	public void setCardType(String s) {
		if (s.equals("ITEM"))
			type = CardType.ITEM;
		else if (s.equals("SPELL"))
			type = CardType.SPELL;
		else if (s.equals("EQUIPMENT"))
			type = CardType.EQUIPMENT;
		else if (s.equals("SHOP"))
			type = CardType.UI;
		else
			type = CardType.BASIC;
	}

	public void setCardType(CardType t) {
		type = t;
	}

	public boolean canChain() {
		return canChain;
	}

	public String toString() {
		return getName() + " : " + getCardType();
	}
}
