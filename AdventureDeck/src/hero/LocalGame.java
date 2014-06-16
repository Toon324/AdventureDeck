/**
 * 
 */
package hero;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

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

	ArrayList<Card> deck = new ArrayList<Card>();
	Card[] hand = new Card[5];
	private GameImage background = null, playerImage = null, enemyImage = null;
	Player player;
	private boolean showBowRange;

	public LocalGame(GameEngine eng) {
		super(eng);

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

		deck.add(new Card("walkUp"));
		deck.add(new Card("walkDown"));
		deck.add(new Card("walkLeft"));
		deck.add(new Card("walkRight"));
		deck.add(new Card("walkUpRight"));
		deck.add(new Card("walkDownRight"));
		deck.add(new Card("walkUpLeft"));
		deck.add(new Card("walkDownLeft"));
		deck.add(new Card("walkUp"));
		deck.add(new Card("walkDown"));
		deck.add(new Card("walkLeft"));
		deck.add(new Card("walkRight"));

		deck.add(new Card("runUp"));
		deck.add(new Card("runDown"));
		deck.add(new Card("runLeft"));
		deck.add(new Card("runRight"));
		deck.add(new Card("runUpRight"));
		deck.add(new Card("runDownRight"));
		deck.add(new Card("runUpLeft"));
		deck.add(new Card("runDownLeft"));
		deck.add(new Card("runUp"));
		deck.add(new Card("runDown"));
		deck.add(new Card("runLeft"));
		deck.add(new Card("runRight"));

		deck.add(new Card("sword"));
		deck.add(new Card("sword"));
		deck.add(new Card("sword"));
		deck.add(new Card("bow"));
		deck.add(new Card("bow"));

		// deck.add(new Card("shop"));
		// deck.add(new Card("shop"));

		deck.add(new Card("smallPotion"));
		deck.add(new Card("smallPotion"));
		deck.add(new Card("largePotion"));

		drawCard();
		drawCard();
		drawCard();
		drawCard();
		drawCard();
	}

	@Override
	public void run(int ms) {

		engine.getActors().handleActors(ms);

		super.run(ms);
	}

	@Override
	public void paint(Graphics g) {

		g.drawImage(background.getImage(), 0, 0, engine.getEnvironmentSize().x,
				engine.getEnvironmentSize().y, 0, 0, background.getWidth(),
				background.getHeight(), null);

		engine.getActors().drawActors(g);

		if (showBowRange) {
			g.setColor(Color.red);
			player.drawBowRange(g);
		}

		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, engine.getEnvironmentSize().y - 100,
				engine.getEnvironmentSize().x, 100);

		for (int x = 0; x < hand.length; x++)
			hand[x].draw(engine, g, x);

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
		for (int x = 0; x < hand.length; x++)
			if (hand[x].checkClick(engine, x, e.getPoint())) {
				handleCard(hand[x]);
				toRemove.add(hand[x]);
				hand[x] = null;
			}

		for (Card c : toRemove) {
			deck.add(c);
			drawCard();
		}
		super.mouseReleased(e);
	}

	private void drawCard() {
		Random gen = new Random();

		int num = gen.nextInt(deck.size());

		for (int x = 0; x < hand.length; x++)
			if (hand[x] == null) {
				hand[x] = deck.get(num);
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
