/**
 * 
 */
package hero;

import hero.Card.CardType;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import petri.api.Actor;
import petri.api.GameEngine;
import petri.api.GameImage;

/**
 * @author Cody
 * 
 */
public class Player extends NPC {

	private final int MAX_AP = 8, MAX_SWORD_DAMAGE = 15, MAX_BOW_DAMAGE = 10;

	int bonusSwordDamage = 0;
	int bonusBowDamage = 0;

	private int gold, actionPoints, apPool;

	public final static int BOWRANGE = 75;

	BufferedImage[] apGuage = new BufferedImage[9];
	BufferedImage coin;

	ArrayList<Card> basicDeck = new ArrayList<Card>();
	ArrayList<Card> spellDeck = new ArrayList<Card>();
	ArrayList<Card> itemDeck = new ArrayList<Card>();

	Card[] basicHand = new Card[5];
	Card[] spellHand = new Card[2];
	Card[] itemHand = new Card[2];
	Card[] ui = new Card[2];

	protected CardHandler cardHandler;

	Player(GameEngine e, GameImage i) {
		super(e, i);
		actionPoints = 1;
		apPool = 1;
		maxHealth = 50;
		health = 50;
		gold = 500;
		xTiles = 4;
		yTiles = 4;
		size = new Point2D.Float(30, 50);
		center = new Point2D.Float(400, 400);
		setDir(2);

		cardHandler = new CardHandler(this);

		createCards();

		loadApImages();
	}

	private void loadApImages() {
		try {
			apGuage[0] = ImageIO
					.read(getClass().getResourceAsStream("0ap.png"));
			apGuage[1] = ImageIO
					.read(getClass().getResourceAsStream("1ap.png"));
			apGuage[2] = ImageIO
					.read(getClass().getResourceAsStream("2ap.png"));
			apGuage[3] = ImageIO
					.read(getClass().getResourceAsStream("3ap.png"));
			apGuage[4] = ImageIO
					.read(getClass().getResourceAsStream("4ap.png"));
			apGuage[5] = ImageIO
					.read(getClass().getResourceAsStream("5ap.png"));
			apGuage[6] = ImageIO
					.read(getClass().getResourceAsStream("6ap.png"));
			apGuage[7] = ImageIO
					.read(getClass().getResourceAsStream("7ap.png"));
			apGuage[8] = ImageIO
					.read(getClass().getResourceAsStream("8ap.png"));
			
			coin = ImageIO.read(getClass().getResourceAsStream("coin.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CardHandler getCardHandler() {
		return cardHandler;
	}

	/**
	 * 
	 */
	private void createCards() {
		// Basic deck

		basicDeck.add(new Card("walk"));
		basicDeck.add(new Card("run"));

		basicDeck.add(new Card("sword"));
		basicDeck.add(new Card("sword"));
		basicDeck.add(new Card("sword"));
		basicDeck.add(new Card("bow"));
		basicDeck.add(new Card("bow"));

		// Spell deck

		spellDeck.add(new Card("lightning"));
		spellDeck.add(new Card("fireball"));

		// Item deck

		// itemDeck.add(new Card("pitfall"));
		// itemDeck.add(new Card("shadow"));
		itemDeck.add(new Card("smallPotion"));
		// itemDeck.add(new Card("smallPotion"));
		itemDeck.add(new Card("largePotion"));

		// UI
		ui[0] = new Card("endTurn");
		ui[1] = new Card("shop");

		// Tell the cards what type they are
		for (Card c : basicDeck)
			c.setCardType(CardType.BASIC);

		for (Card c : spellDeck)
			c.setCardType(CardType.SPELL);

		for (Card c : itemDeck)
			c.setCardType(CardType.ITEM);

		ui[0].setCardType(CardType.UI);
		ui[1].setCardType(CardType.UI);

		initiateHand();
	}

	public void moveSpace(int spaces) {
		Point2D.Float c = getCenter();

		System.out.println("Center: " + getCenter());

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

	public void endTurn() {
		if (apPool < MAX_AP)
			apPool++;

		actionPoints = apPool;

		heal(1);
		addGold(5);

		// Try to draw up to one card per hand
		drawCard(CardType.BASIC);
		drawCard(CardType.SPELL);
		drawCard(CardType.ITEM);
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

			
			if (! (this instanceof AI)) {
			g.drawImage(apGuage[getAP()], 0, 450, 100, 550, 0, 0,
					apGuage[getAP()].getWidth(), apGuage[getAP()].getHeight(),
					null);
			
			g.drawString("AP", 40, 505);
			
			g.drawImage(coin, 25, 375, 75, 425, 0, 0, coin.getWidth(), coin.getHeight(), null);
			
			g.drawString(getGold() + "", 40, 365);
			
			g.drawString("HP: " + getHealth(), 5, 200);
			g.drawRect(10, 210, 80, 10);
			g.fillRect(10, 210, (int) (80 * ((double)getHealth() / maxHealth)), 10);
			
			g.setColor(Color.black);
			
			g.drawString("ATK: " + bonusSwordDamage, 5, 250);
			g.drawRect(10, 260, 80, 10);
			g.fillRect(10, 260, (int) (80 * ((double)bonusSwordDamage / MAX_SWORD_DAMAGE)) , 10);
			
			g.setColor(Color.blue);
			g.drawString("WIS: " + bonusBowDamage, 5, 300);
			g.drawRect(10, 310, 80, 10);
			g.fillRect(10, 310, (int) (80 * ((double)bonusBowDamage / MAX_BOW_DAMAGE)), 10);
			
			
			}
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
		g.drawRect((int) (center.x - BOWRANGE), (int) (center.y - BOWRANGE),
				(int) (BOWRANGE * 2 + size.x), (int) (BOWRANGE * 2 + size.y));

	}

	/**
	 * @param amt
	 */
	public void addHealth(int amt) {
		health += amt;
		maxHealth += amt;

	}

	public int getAPpool() {
		return apPool;
	}

	public int getAP() {
		return actionPoints;
	}

	public void useAP(int cost) {
		actionPoints -= cost;
	}

	/**
	 * 
	 */
	private void initiateHand() {
		drawCards(5, basicHand, basicDeck);
		drawCards(2, spellHand, spellDeck);
		drawCards(2, itemHand, itemDeck);
	}

	/**
	 * @param i
	 * @param basicHand2
	 * @param basicDeck2
	 */
	private void drawCards(int num, Card[] section, ArrayList<Card> deck) {
		for (int x = 0; x < num; x++) {
			drawCard(section, deck);
		}

	}

	private void drawCard(Card[] section, ArrayList<Card> deck) {
		Random gen = new Random();

		if (deck.size() == 0) {
			System.out.println(section[0] + " deck is size zero");
			return;
		}

		int num = gen.nextInt(deck.size());

		for (int x = 0; x < section.length; x++)
			if (section[x] == null) {
				section[x] = deck.get(num);
				break;
			}

		deck.remove(deck.get(num));

	}

	/**
	 * @param toDraw2
	 */
	private void drawCard(CardType type) {
		if (type == CardType.BASIC)
			drawCard(basicHand, basicDeck);
		else if (type == CardType.SPELL)
			drawCard(spellHand, spellDeck);
		else
			drawCard(itemHand, itemDeck);

	}

	/**
	 * @param e
	 * @param basicHand2
	 */
	public void checkHandForClick(Point start, MouseEvent e, Card[] hand,
			int offset) {

		for (int x = 0; x < hand.length; x++)
			if (hand[x] != null)
				if (hand[x].checkClick(start, engine, x + offset, e.getPoint())) {

					Card toRemove = hand[x];

					if (getAP() < toRemove.getCost()) {
						GameEngine.log("Not enough AP.");
						return;
					} else
						useAP(toRemove.getCost());

					if (hand[x].getCardType() != CardType.UI)
						hand[x] = null;

					// System.out.println("Handling " + toRemove);

					if (toRemove.type == CardType.BASIC)
						basicDeck.add(toRemove);
					else if (toRemove.type == CardType.SPELL)
						spellDeck.add(toRemove);

					cardHandler.handleCard(toRemove);
				}
	}

	/**
	 * @param start
	 * @param e
	 */
	public void checkHandsForClick(Point start, MouseEvent e) {
		checkHandForClick(start, e, basicHand, 0);
		checkHandForClick(start, e, spellHand, basicHand.length);
		checkHandForClick(start, e, itemHand, basicHand.length
				+ spellHand.length);
		checkHandForClick(start, e, ui, basicHand.length + spellHand.length
				+ itemHand.length);

	}

	/**
	 * @param g
	 */
	public void drawHands(Graphics g) {
		for (int x = 0; x < basicHand.length; x++) {
			if (basicHand[x] != null)
				basicHand[x].draw(new Point(10,
						engine.getEnvironmentSize().y - 90), engine, g, x);
		}

		// Next three hands are drawn with offsets.

		for (int x = 0; x < spellHand.length; x++) {
			if (spellHand[x] != null)
				spellHand[x].draw(new Point(10,
						engine.getEnvironmentSize().y - 90), engine, g, x
						+ basicHand.length);

		}

		for (int x = 0; x < itemHand.length; x++)
			if (itemHand[x] != null)
				itemHand[x].draw(new Point(10,
						engine.getEnvironmentSize().y - 90), engine, g, x
						+ basicHand.length + spellHand.length);

		// Shop is at the end of the cards
		g.setColor(Color.green);

		for (int x = 0; x < ui.length; x++)
			if (ui[x] != null)
				ui[x].draw(new Point(10, engine.getEnvironmentSize().y - 90),
						engine, g, basicHand.length + spellHand.length
								+ itemHand.length + x);

	}

	public void buffSword(int amt) {
		bonusSwordDamage += amt;

		if (bonusSwordDamage > MAX_SWORD_DAMAGE)
			bonusSwordDamage = MAX_SWORD_DAMAGE;
	}

	public void buffBow(int amt) {
		bonusBowDamage += amt;

		if (bonusBowDamage > MAX_BOW_DAMAGE)
			bonusBowDamage = MAX_BOW_DAMAGE;
	}
}
