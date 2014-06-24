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
		BASIC, SPELL, TRAP;
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

	public Card(String n) {
		name = n;
		type = CardType.BASIC;
		
		File f = new File("bin/hero/Cards/" + n + ".card");
		System.out.println("Card file should be " + f.getName());
		if (f.exists())
			try {
				loadInfo(f);
			} catch (FileNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		try {
			image = ImageIO.read(getClass().getResourceAsStream("Cards/" + n + ".png"));
		} catch (Exception e) {
			System.out.println("Could not read " + n);
			e.printStackTrace();
			try {
				image = ImageIO.read(getClass().getResource("Cards/default.png"));
			}
			catch (Exception e1) {
				System.out.println("Fatal fault: Could not load backup image default.png!");
			}
		}
	}

	/**
	 * @param f
	 * @throws FileNotFoundException 
	 */
	private void loadInfo(File f) throws FileNotFoundException {
		//System.out.println("Loading info of " + f.getName());
		Scanner scan = new Scanner(f);
		scan.useDelimiter("\t");
		
		String section = "";
		
		while (scan.hasNext()) {
			String s = scan.next();
			System.out.println(section + " : " + s);
			
			if (s.contains("INFO {"))
				section = "INFO";
			else if (s.contains("EFFECT {"))
				section = "EFFECT";
			
			if (!s.equals("}")) {
				if (section == "INFO") {
					if (s.equals("name"))
						name = scan.next();
					else if (s.equals("type"))
						setType(scan.next());
					else if (s.equals("cost"))
						cost = Integer.parseInt(scan.next().trim());
					else if (s.equals("range"))
						range = readRange(scan.next());
					else if (s.equals("canChain")) {
						String chain = scan.next();
						int i = Integer.valueOf(chain.substring(0, chain.indexOf("}")).trim());
						if (i == 0)
							canChain = false;
						else
							canChain = true;
					}
				}
				else {
					
				}
			}
				
		}
		
		scan.close();
	}

	/**
	 * @param next
	 * @return
	 */
	private int[][] readRange(String rangeText) {
		List<String> temp = new LinkedList<String>();
		
		Scanner layer1 = new Scanner(rangeText);
		layer1.useDelimiter("]");
		
		while (layer1.hasNext()) {
			String line = layer1.next();
			Scanner layer2 = new Scanner(line);
			layer2.useDelimiter(",");
			
			System.out.println("Line found: " + line); 
			System.out.println("Element found: " + layer2.next());
		}
		
		return null;
	}

	public void draw(GameEngine engine, Graphics g, int index) {
		
		Color org = g.getColor();
		
		//Set color based on card type
		if (type == CardType.BASIC)
			g.setColor(Color.gray);
		else if (type == CardType.SPELL)
			g.setColor(Color.cyan);
		else if (type == CardType.TRAP)
			g.setColor(Color.orange);
		
		//Determine position
		int x = 10 + (index * WIDTH) + (index * SPACING);
		int y = engine.getEnvironmentSize().y - 90;

		//Draw back
		g.fillRect(x, y, WIDTH, HEIGHT);

		//Draw icon
		g.drawImage(image, x + 10, y + 10, x + 70, y + 70, 0, 0,
				image.getWidth(), image.getHeight(), null);

		//Draw text
		g.setColor(Color.BLACK);
		
		
		//Reset to original color
		g.setColor(org);

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
			type = CardType.TRAP;
		
	}
	
	public void setType(CardType t) {
		type = t;
	}
	
	public String toString() {
		return getName() + " : " + getType();
	}
}
