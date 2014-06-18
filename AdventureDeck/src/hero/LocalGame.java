/**
 * 
 */
package hero;

import hero.Card.CardType;

import java.awt.Color;
import java.awt.Graphics;
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
	
	private final int SMALL_POTION_AMT = 4;
	private final int LARGE_POTION_AMT = 9;
	private final int SPACE_SIZE = 25;
	private final int SWORD_DAMAGE = 8;
	private final int BOW_DAMAGE = 5;
	private final int ENEMY_DAMAGE = 12;
	

	ArrayList<Card> basicDeck = new ArrayList<Card>();
	ArrayList<Card> spellDeck = new ArrayList<Card>();
	ArrayList<Card> trapDeck = new ArrayList<Card>();
	
	Card[] basicHand = new Card[5];
	Card[] spellHand = new Card[2];
	Card[] trapHand = new Card[2];
	
	private GameImage background = null, playerImage = null, enemyImage = null;
	Player player;
	private boolean showBowRange;
	private TileManager board;
	private static boolean done;
	
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

			background = new GameImage(ImageIO.read(getClass().getResource(
					"background.png")));
			
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

		basicDeck.add(new Card("walkUp"));
		basicDeck.add(new Card("walkDown"));
		basicDeck.add(new Card("walkLeft"));
		basicDeck.add(new Card("walkRight"));
		basicDeck.add(new Card("walkUpRight"));
		basicDeck.add(new Card("walkDownRight"));
		basicDeck.add(new Card("walkUpLeft"));
		basicDeck.add(new Card("walkDownLeft"));
		basicDeck.add(new Card("walkUp"));
		basicDeck.add(new Card("walkDown"));
		basicDeck.add(new Card("walkLeft"));
		basicDeck.add(new Card("walkRight"));

		basicDeck.add(new Card("runUp"));
		basicDeck.add(new Card("runDown"));
		basicDeck.add(new Card("runLeft"));
		basicDeck.add(new Card("runRight"));
		basicDeck.add(new Card("runUpRight"));
		basicDeck.add(new Card("runDownRight"));
		basicDeck.add(new Card("runUpLeft"));
		basicDeck.add(new Card("runDownLeft"));
		basicDeck.add(new Card("runUp"));
		basicDeck.add(new Card("runDown"));
		basicDeck.add(new Card("runLeft"));
		basicDeck.add(new Card("runRight"));

		basicDeck.add(new Card("sword"));
		basicDeck.add(new Card("sword"));
		basicDeck.add(new Card("sword"));
		basicDeck.add(new Card("bow"));
		basicDeck.add(new Card("bow"));

		// deck.add(new Card("shop"));
		// deck.add(new Card("shop"));

		basicDeck.add(new Card("smallPotion"));
		basicDeck.add(new Card("smallPotion"));
		basicDeck.add(new Card("largePotion"));
		
		//Spell deck
		
		spellDeck.add(new Card("lightning"));
		spellDeck.add(new Card("fireball"));
		
		//Trap deck
		
		trapDeck.add(new Card("pitfall"));
		trapDeck.add(new Card("shadow"));
		
		
		//Tell the cards what type they are
		for (Card c : basicDeck)
			c.setType(CardType.BASIC);
		
		for (Card c : spellDeck)
			c.setType(CardType.SPELL);
		
		for (Card c : trapDeck)
			c.setType(CardType.TRAP);
		
		initiateHand();
		System.out.println("Begin generation: " + (System.currentTimeMillis() - startTime));
		
		
		Executors.newCachedThreadPool().execute(new Runnable() {

			@Override
			public void run() {
				board = new TileManager(engine.getEnvironmentSize().x / 25, engine.getEnvironmentSize().y / 25);
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
		for (int x=0; x < num; x++) {
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

//		g.drawImage(background.getImage(), 0, 0, engine.getEnvironmentSize().x,
//				engine.getEnvironmentSize().y, 0, 0, background.getWidth(),
//				background.getHeight(), null);
		
		board.paint(g);

		engine.getActors().drawActors(g);

		if (showBowRange) {
			g.setColor(Color.red);
			player.drawBowRange(g);
		}

		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, engine.getEnvironmentSize().y - 100,
				engine.getEnvironmentSize().x, 100);

		
		for (int x = 0; x < basicHand.length; x++)
			basicHand[x].draw(engine, g, x);
		
		//Next two hands are drawn with offsets.
		
		for (int x = 0; x < spellHand.length; x++)
			spellHand[x].draw(engine, g, x + basicHand.length);
		
		for (int x = 0; x < trapHand.length; x++)
			trapHand[x].draw(engine, g, x + basicHand.length + spellHand.length);

		super.paint(g);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petri.api.GameMode#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (showBowRange) {
			bowAttack(e);
		}

		ArrayList<Card> toRemove = new ArrayList<Card>();
		checkClick(e, basicHand, toRemove);

		for (Card c : toRemove) {
			basicDeck.add(c);
			drawCard(basicHand, basicDeck);
		}
		
		toRemove = new ArrayList<Card>();
		checkClick(e, spellHand, toRemove);

		for (Card c : toRemove) {
			spellDeck.add(c);
			drawCard(spellHand, spellDeck);
		}
		
		toRemove = new ArrayList<Card>();
		checkClick(e, trapHand, toRemove);

		for (Card c : toRemove) {
			trapDeck.add(c);
			drawCard(trapHand, trapDeck);
		}
		super.mouseReleased(e);
	}

	/**
	 * @param e
	 * @param basicHand2
	 */
	private void checkClick(MouseEvent e, Card[] hand, ArrayList<Card> toRemove) {
		for (int x = 0; x < hand.length; x++)
			if (hand[x].checkClick(engine, x, e.getPoint())) {
				handleCard(hand[x]);
				toRemove.add(hand[x]);
				hand[x] = null;
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

	private void handleCard(Card c) {
		String cmd = c.getName();

		switch (cmd) {
		case "sword":
			attack();
			return;
		case "block":
			return;
		case "bow":
			showBowRange = true;
			return;
		case "smallPotion":
			player.heal(SMALL_POTION_AMT);
			return;
		case "largePotion":
			player.heal(LARGE_POTION_AMT);
			return;
		case "shop":
			engine.setCurrentGameMode(2);
			return;

		case "walkUp":
			player.setDir(0);
			player.moveSpace(1);
			return;
		case "walkUpRight":
			player.setDir(1);
			player.moveSpace(1);
			return;
		case "walkRight":
			player.setDir(2);
			player.moveSpace(1);
			return;
		case "walkDownRight":
			player.setDir(3);
			player.moveSpace(1);
			return;
		case "walkDown":
			player.setDir(4);
			player.moveSpace(1);
			return;
		case "walkDownLeft":
			player.setDir(5);
			player.moveSpace(1);
			return;
		case "walkLeft":
			player.setDir(6);
			player.moveSpace(1);
			return;
		case "walkUpLeft":
			player.setDir(7);
			player.moveSpace(1);
			return;

		case "runUp":
			player.setDir(0);
			player.moveSpace(2);
			return;
		case "runUpRight":
			player.setDir(1);
			player.moveSpace(2);
			return;
		case "runRight":
			player.setDir(2);
			player.moveSpace(2);
			return;
		case "runDownRight":
			player.setDir(3);
			player.moveSpace(2);
			return;
		case "runDown":
			player.setDir(4);
			player.moveSpace(2);
			return;
		case "runDownLeft":
			player.setDir(5);
			player.moveSpace(2);
			return;
		case "runLeft":
			player.setDir(6);
			player.moveSpace(2);
			return;
		case "runUpLeft":
			player.setDir(7);
			player.moveSpace(2);
			return;

		}

	}

	private void attack() {
		Enemy closest = null;
		int distance = -1;

		for (Actor a : engine.getActors().getArrayList())
			if (a instanceof Enemy)
				if (closest == null || player.distanceTo(a) < distance) {
					closest = (Enemy) a;
					distance = player.distanceTo(a);
				}

		if (distance <= SPACE_SIZE * 2) {
			closest.dealDamage(SWORD_DAMAGE);
			player.dealDamage(ENEMY_DAMAGE);
		}
	}

	private void bowAttack(MouseEvent e) {
		Actor target = null;

		for (Actor a : engine.getActors().getArrayList())
			if (a instanceof Enemy) {
				System.out.println("Checkclick? " + ((Enemy)a).checkClick(e));
				System.out.println("Distance: " + a.getCenter().distance(player.getCenter()));
				if (((Enemy) a).checkClick(e)
						&& a.getCenter().distance(player.getCenter()) <= Player.BOWRANGE + 10) {
					target = a;
					break;
				}
			}

		if (target != null) {
			target.dealDamage(BOW_DAMAGE);
			showBowRange = false;
		}
	}

}
