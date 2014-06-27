/**
 * 
 */
package hero;

import hero.Card.CardType;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import petri.api.Actor;
import petri.api.AnimatedImage;
import petri.api.GameEngine;
import petri.api.GameImage;
import petri.api.GameMode;

/**
 * @author Cody
 * 
 */
public class LocalGame extends GameMode {

	final int SMALL_POTION_AMT = 4;
	final int LARGE_POTION_AMT = 9;
	final int TILE_SIZE = 25;
	final int SWORD_DAMAGE = 8;
	final int BOW_DAMAGE = 5;
	final int ENEMY_DAMAGE = 12;

	ArrayList<Card> basicDeck = new ArrayList<Card>();
	ArrayList<Card> spellDeck = new ArrayList<Card>();
	ArrayList<Card> trapDeck = new ArrayList<Card>();

	Card[] basicHand = new Card[5];
	Card[] spellHand = new Card[2];
	Card[] trapHand = new Card[2];

	private GameImage playerImage = null, enemyImage = null;
	private CardHandler cardHandler = new CardHandler(this);

	public Player player;
	public boolean showRange;

	private TileManager board;
	private int[][] range;
	private Card choiceCard;
	private CardType toDraw;
	public Actor currentTarget;

	static long startTime;

	public LocalGame(GameEngine eng) {
		super(eng);

		System.out.println("Start: " + System.currentTimeMillis());
		startTime = System.currentTimeMillis();

		try {
			BufferedImage loadPlayer = ImageIO.read(getClass()
					.getResourceAsStream("player.png"));
			playerImage = new AnimatedImage(loadPlayer, 4, 4);

			BufferedImage loadEnemy = ImageIO.read(getClass()
					.getResourceAsStream("enemy.png"));
			enemyImage = new AnimatedImage(loadEnemy, 4, 4);

		} catch (Exception e) {
			e.printStackTrace();
			GameEngine.log("LocalGame exception: " + e.getMessage());
		}

		player = new Player(eng, playerImage);

		Enemy e1 = new Enemy(eng, enemyImage);
		e1.setCenter(300, 300);

		Enemy e2 = new Enemy(eng, enemyImage);
		e2.setCenter(600, 200);

		Enemy e3 = new Enemy(eng, enemyImage);
		e3.setCenter(200, 400);

		engine.getActors().add(player);
		engine.getActors().add(e1);
		engine.getActors().add(e2);
		engine.getActors().add(e3);

		basicDeck.add(new Card("walk"));
		basicDeck.add(new Card("walk"));
		basicDeck.add(new Card("walk"));
		basicDeck.add(new Card("walk"));
		basicDeck.add(new Card("walk"));
		basicDeck.add(new Card("run"));

//		basicDeck.add(new Card("sword"));
//		basicDeck.add(new Card("sword"));
//		basicDeck.add(new Card("sword"));
//		basicDeck.add(new Card("bow"));
//		basicDeck.add(new Card("bow"));

		// deck.add(new Card("shop"));
		// deck.add(new Card("shop"));

		basicDeck.add(new Card("smallPotion"));
		basicDeck.add(new Card("smallPotion"));
		//basicDeck.add(new Card("largePotion"));

		// Spell deck

		spellDeck.add(new Card("lightning"));
		spellDeck.add(new Card("fireball"));

		// Trap deck

		trapDeck.add(new Card("pitfall"));
		trapDeck.add(new Card("shadow"));

		// Tell the cards what type they are
		for (Card c : basicDeck)
			c.setType(CardType.BASIC);

		for (Card c : spellDeck)
			c.setType(CardType.SPELL);

		for (Card c : trapDeck)
			c.setType(CardType.TRAP);

		initiateHand();
		System.out.println("Begin generation: "
				+ (System.currentTimeMillis() - startTime));

		Executors.newCachedThreadPool().execute(new Runnable() {

			@Override
			public void run() {
				board = new TileManager(engine.getEnvironmentSize().x / 25,
						engine.getEnvironmentSize().y / 25);
			}

		});
	}

	/**
	 * 
	 */
	private void initiateHand() {
		drawCards(5, basicHand, basicDeck);
		drawCards(2, spellHand, spellDeck);
		drawCards(2, trapHand, trapDeck);

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

	@Override
	public void run(int ms) {

		engine.getActors().handleActors(ms);

		super.run(ms);
	}

	@Override
	public void paint(Graphics g) {

		// g.drawImage(background.getImage(), 0, 0,
		// engine.getEnvironmentSize().x,
		// engine.getEnvironmentSize().y, 0, 0, background.getWidth(),
		// background.getHeight(), null);

		board.paint(g);

		if (showRange) {
			g.setColor(new Color(214, 28, 74, 200)); // Semi transparent red
			for (int x = 0; x < range.length; x++) {
				for (int y = 0; y < range[x].length; y++) {
					if (range[x][y] == 1) {
						int tileX = x - range.length / 2;
						int tileY = y - range[x].length / 2;

						g.fillRect((int) player.getCenter().x + tileX
								* TileManager.TILE_SIZE,
								(int) player.getCenter().y
										+ TileManager.TILE_SIZE + tileY
										* TileManager.TILE_SIZE,
								TileManager.TILE_SIZE, TileManager.TILE_SIZE);
					}
				}
			}
		}

		g.setColor(Color.orange);

		g.setFont(g.getFont().deriveFont(18.0F));
		g.drawString(player.getGold() + " Gil", 30, 50);

		engine.getActors().drawActors(g);

		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, engine.getEnvironmentSize().y - 100,
				engine.getEnvironmentSize().x, 100);

		for (int x = 0; x < basicHand.length; x++) {
			if (basicHand[x] != null)
				basicHand[x].draw(engine, g, x);
		}

		// Next two hands are drawn with offsets.

		for (int x = 0; x < spellHand.length; x++) {
			if (spellHand[x] != null)
				spellHand[x].draw(engine, g, x + basicHand.length);

		}

		for (int x = 0; x < trapHand.length; x++)
			if (trapHand[x] != null)
				trapHand[x].draw(engine, g, x + basicHand.length
						+ spellHand.length);

		super.paint(g);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petri.api.GameMode#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (showRange)
			checkChoice(e);
		else {

			checkClick(e, basicHand, 0);
			checkClick(e, spellHand, basicHand.length);
			checkClick(e, trapHand, basicHand.length + spellHand.length);
		}

		super.mouseReleased(e);
	}

	public void endTurn() {
		System.out.println("ToDraw: " + toDraw);
		drawCard(toDraw);
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
			drawCard(trapHand, trapDeck);

	}

	/**
	 * @param e
	 */
	private void checkChoice(MouseEvent e) {

		for (int x = 0; x < range.length; x++) {
			for (int y = 0; y < range[x].length; y++) {
				if (range[x][y] == 1) {

					float tileX = x - range.length / 2;
					float tileY = y - range[x].length / 2;

					Rectangle r = new Rectangle(
							(int) (player.getCenter().x + tileX
									* TileManager.TILE_SIZE),
							(int) (player.getCenter().y + TileManager.TILE_SIZE + tileY
									* TileManager.TILE_SIZE),
							TileManager.TILE_SIZE, TileManager.TILE_SIZE);

					if (r.contains(e.getPoint())) {
						// System.out.println("Handling " + x + " " + y);
						currentTarget = getTarget(tileX, tileY);
						cardHandler.handleChoice(choiceCard, tileX, tileY);
						showRange = false;
						return;
					}
				}
			}
		}
	}

	/**
	 * @param tileX
	 * @param tileY
	 * @return
	 */
	private Actor getTarget(float x, float y) {
		Point click = new Point((int) (player.getCenter().x + x
				* TILE_SIZE), (int) (player.getCenter().y + y
				* TILE_SIZE));
		
		Rectangle clickBox = new Rectangle(click.x % 25, click.y % 25, TILE_SIZE, TILE_SIZE);
		
		for (Actor a : engine.getActors().getArrayList()) {
			if (clickBox.contains(a.getCenter())) {
				return a;
			}
		}
		
		return null;
	}

	/**
	 * @param e
	 * @param basicHand2
	 */
	private void checkClick(MouseEvent e, Card[] hand, int offset) {
		for (int x = 0; x < hand.length; x++)
			if (hand[x].checkClick(engine, x + offset, e.getPoint())) {

				Card toRemove = hand[x];
				hand[x] = null;

				System.out.println("Handling " + toRemove);

				toDraw = toRemove.type;

				if (toRemove.type == CardType.BASIC)
					basicDeck.add(toRemove);
				else if (toRemove.type == CardType.SPELL)
					spellDeck.add(toRemove);
				else
					trapDeck.add(toRemove);

				cardHandler.handleCard(toRemove);
			}
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

	/**
	 * @param range
	 */
	public void giveOption(int[][] r, Card c) {
		range = r;
		choiceCard = c;
		showRange = true;

	}

}
