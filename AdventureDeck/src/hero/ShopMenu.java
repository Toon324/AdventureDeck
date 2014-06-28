/**
 * 
 */
package hero;

import hero.Card.CardType;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import petri.api.GameEngine;
import petri.api.GameMode;

/**
 * @author Cody
 * 
 */
public class ShopMenu extends GameMode {

	BufferedImage background, exit;
	Card[] equipment;
	Card[] items;

	ArrayList<Card> itemDeck = new ArrayList<Card>();
	ArrayList<Card> equipmentDeck = new ArrayList<Card>();
	private CardType toDraw;
	private LocalGame game;

	public ShopMenu(GameEngine eng, LocalGame lg) {
		super(eng);

		game = lg;

		items = new Card[5];
		equipment = new Card[5];

		itemDeck.add(new Card("smallPotion"));
		itemDeck.add(new Card("largePotion"));
		itemDeck.add(new Card("smallPotion"));
		itemDeck.add(new Card("largePotion"));
		itemDeck.add(new Card("smallPotion"));
		itemDeck.add(new Card("largePotion"));

		equipmentDeck.add(new Card("vest"));
		equipmentDeck.add(new Card("steelSword"));
		equipmentDeck.add(new Card("legendaryBow"));
		equipmentDeck.add(new Card("clothPants"));
		equipmentDeck.add(new Card("vest"));

		populateShop();

		try {
			BufferedImage tempBackground = ImageIO.read(getClass()
					.getResourceAsStream("shopBackground.jpg"));
			BufferedImage keeper = ImageIO.read(getClass().getResourceAsStream(
					"shopKeeper.png"));
			BufferedImage message = ImageIO.read(getClass()
					.getResourceAsStream("shopMessage.jpg"));
			exit = ImageIO.read(getClass().getResourceAsStream("shopExit.jpg"));

			background = new BufferedImage(engine.getEnvironmentSize().x,
					engine.getEnvironmentSize().y, tempBackground.getType());

			Graphics g = background.getGraphics();

			g.drawImage(tempBackground, 0, 0, engine.getEnvironmentSize().x,
					engine.getEnvironmentSize().y, null);
			g.drawImage(keeper, engine.getEnvironmentSize().x - 250, 300, 250,
					engine.getEnvironmentSize().y - 300, null);
			g.drawImage(message, 10, engine.getEnvironmentSize().y - 125,
					engine.getEnvironmentSize().x - 440, 115, null);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petri.api.GameMode#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		g.drawImage(background, 0, 0, engine.getEnvironmentSize().x,
				engine.getEnvironmentSize().y, null);
		g.drawImage(exit, engine.getEnvironmentSize().x - 400,
				engine.getEnvironmentSize().y - 120, 75, 100, null);

		g.setColor(new Color(179, 171, 178, 230));

		g.fillRect(80, 75, 600, 300);

		g.setColor(Color.white);

		g.setFont(g.getFont().deriveFont(30.0F));

		g.drawString("Items", 85, 100);

		Point itemStart = new Point(100, 125);
		for (int x = 0; x < items.length; x++) {
			items[x].draw(itemStart, engine, g, x);
			g.drawString(items[x].getCost() * 15 + " Gil", x * 110
					+ itemStart.x, itemStart.y + 95);
		}

		g.drawString("Equipment", 85, 250);

		Point equipStart = new Point(100, 275);
		for (int x = 0; x < equipment.length; x++) {
			equipment[x].draw(equipStart, engine, g, x);
			g.drawString(equipment[x].getCost() * 15 + " Gil", x * 110
					+ equipStart.x, equipStart.y + 95);
		}

		super.paint(g);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petri.api.GameMode#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		// System.out.println(e.getPoint());
		if (e.getPoint().x >= engine.getEnvironmentSize().x - 400
				&& e.getPoint().x <= engine.getEnvironmentSize().x - 325
				&& e.getPoint().y >= engine.getEnvironmentSize().y - 120
				&& e.getPoint().y <= engine.getEnvironmentSize().y - 20) {
			CardHero.gi.getGameApplet().setCursor(
					new Cursor(Cursor.HAND_CURSOR));
		} else
			CardHero.gi.getGameApplet().setCursor(
					new Cursor(Cursor.DEFAULT_CURSOR));
		super.mouseMoved(e);
	}

	/**
	 * @param e
	 * @param basicHand2
	 */
	private void checkClick(Point start, MouseEvent e, Card[] hand) {

		for (int x = 0; x < hand.length; x++)
			if (hand[x] != null)
				if (hand[x].checkClick(start, engine, x, e.getPoint())) {

					Card toRemove = hand[x];
					hand[x] = null;

					// System.out.println("Handling " + toRemove);

					toDraw = toRemove.type;

					if (toRemove.type == CardType.ITEM) {
						itemDeck.add(toRemove);
						game.itemDeck.add(toRemove);
					} else {
						equipmentDeck.add(toRemove);
						game.cardHandler.handleCard(toRemove);
					}
				}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petri.api.GameMode#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getClickCount() == 2) {
			if (e.getPoint().x >= engine.getEnvironmentSize().x - 400
					&& e.getPoint().x <= engine.getEnvironmentSize().x - 325
					&& e.getPoint().y >= engine.getEnvironmentSize().y - 120
					&& e.getPoint().y <= engine.getEnvironmentSize().y - 20) {
				engine.setCurrentGameMode(1);
			} else {
				Point equipStart = new Point(0, 0);
				checkClick(equipStart, e, equipment);
				Point itemStart = new Point(0, 0);
				checkClick(itemStart, e, items);
			}
		}
		super.mouseReleased(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petri.api.GameMode#run(int)
	 */
	@Override
	public void run(int ms) {
		// TODO Auto-generated method stub
		super.run(ms);
	}

	private void populateShop() {
		for (int x = 0; x < items.length; x++)
			drawCard(items, itemDeck);

		for (int x = 0; x < equipment.length; x++)
			drawCard(equipment, equipmentDeck);
	}

	private void drawCard(Card[] section, ArrayList<Card> deck) {
		Random gen = new Random();

		int num = gen.nextInt(deck.size());

		for (int x = 0; x < section.length; x++)
			if (section[x] == null) {
				section[x] = deck.get(num);
				break;
			}

		deck.remove(deck.get(num));

	}

}
