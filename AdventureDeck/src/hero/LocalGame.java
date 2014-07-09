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
import java.util.LinkedList;
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

	private static final int CARD_TRAY_SIZE = 100;
	final static int TILE_SIZE = 25;

	final int ENEMY_DAMAGE = 12;

	int bonusSwordDamage = 0;
	int bonusBowDamage = 0;

	private GameImage playerImage = null, enemyImage = null, aiImage = null;

	public Player player;
	private AI ai;
	public boolean showRange;

	private TileManager board;
	private int[][] range;
	private Card choiceCard;
	public NPC currentTarget;
	int turnCnt;
	GraphPanel drawTimes;
	LinkedList<Double> drawPoints;

	long drawStart;

	static long startTime;

	public LocalGame(GameEngine eng) {
		super(eng);

		drawPoints = new LinkedList<Double>();
		drawPoints.add(0.0);
		drawTimes = new GraphPanel(drawPoints);
		drawStart = System.currentTimeMillis();

		// JFrame graph = new JFrame();
		// graph.setSize(400, 400);
		//
		// graph.add(drawTimes);
		//
		// graph.setVisible(true);

		System.out.println("Start: " + System.currentTimeMillis());
		startTime = System.currentTimeMillis();

		try {
			BufferedImage loadPlayer = ImageIO.read(getClass()
					.getResourceAsStream("player.png"));
			playerImage = new AnimatedImage(loadPlayer, 4, 4);

			BufferedImage loadEnemy = ImageIO.read(getClass()
					.getResourceAsStream("enemy.png"));
			enemyImage = new AnimatedImage(loadEnemy, 4, 4);

			BufferedImage loadAI = ImageIO.read(getClass().getResourceAsStream(
					"enemy.png"));
			aiImage = new AnimatedImage(loadAI, 4, 4);

		} catch (Exception e) {
			e.printStackTrace();
			GameEngine.log("LocalGame exception: " + e.getMessage());
		}

		player = new Player(eng, playerImage);

		player.getCardHandler().giveLocalGame(this);

		NPC e1 = new NPC(eng, enemyImage);
		e1.setCenter(300, 300);

		NPC e2 = new NPC(eng, enemyImage);
		e2.setCenter(600, 200);

		NPC e3 = new NPC(eng, enemyImage);
		e3.setCenter(200, 400);

		ai = new AI(eng, aiImage);
		ai.getCardHandler().giveLocalGame(this);

		ai.setCenter(500, 150);

		engine.getActors().add(player);
		engine.getActors().add(e1);
		engine.getActors().add(e2);
		engine.getActors().add(e3);
		engine.getActors().add(ai);

		System.out.println("Begin generation: "
				+ (System.currentTimeMillis() - startTime));

		Executors.newCachedThreadPool().execute(new Runnable() {

			@Override
			public void run() {
				board = new TileManager(engine.getEnvironmentSize().x / 25,
						(engine.getEnvironmentSize().y - CARD_TRAY_SIZE) / 25);
			}

		});
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

			for (int outterY = 0; outterY < range.length; outterY++) {
				for (int innerX = 0; innerX < range[outterY].length; innerX++) {
					if (range[outterY][innerX] == 1) {
						int yTile = outterY - range.length / 2;
						int xTile = innerX - range[outterY].length / 2;

						g.fillRect((int) player.getCenter().x + xTile
								* TileManager.TILE_SIZE,
								(int) player.getCenter().y
										+ TileManager.TILE_SIZE + yTile
										* TileManager.TILE_SIZE,
								TileManager.TILE_SIZE, TileManager.TILE_SIZE);
					}
				}
			}
		}

		g.setColor(Color.orange);

		g.setFont(g.getFont().deriveFont(18.0F));
		g.drawString(player.getGold() + " Gil", 30, 50);
		g.drawString("Turn: " + turnCnt, 30, 90);
		g.drawString(
				"Action Points: " + player.getAP() + " / " + player.getAPpool(),
				30, 130);

		engine.getActors().drawActors(g);

		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, engine.getEnvironmentSize().y - CARD_TRAY_SIZE,
				engine.getEnvironmentSize().x, CARD_TRAY_SIZE);

		player.drawHands(g);

		super.paint(g);
		// drawPoints.add((double) (System.currentTimeMillis() - drawStart));

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
			Point start = new Point(10, engine.getEnvironmentSize().y - 90);
			player.checkHandsForClick(start, e);
		}

		super.mouseReleased(e);
	}

	public void endTurn() {
		turnCnt++;

		player.endTurn(); // Player gains max AP, resets to full AP, and heals
							// slightly

		ai.endTurn();

		// Handle any statuses they may have (burn, poison, etc)
		for (Actor a : engine.getActors().getArrayList())
			((NPC) a).handleStatuses();
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
						player.cardHandler.setCurrentTarget(getTarget(tileX,
								tileY));

						player.getCardHandler().handleChoice(choiceCard, tileX,
								tileY);
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
	private NPC getTarget(float x, float y) {
		Point click = new Point((int) (player.getCenter().x + x * TILE_SIZE),
				(int) (player.getCenter().y + y * TILE_SIZE));

		// System.out.println("Click: " + click);

		for (Actor a : engine.getActors().getArrayList()) {
			Point actorPoint = new Point((int) a.getCenter().x,
					(int) a.getCenter().y);
			 //System.out.println("Actor: " + a + " " +  actorPoint);

			if (actorPoint.equals(click)) {
				// System.out.println("Target set to " + a);
				return (NPC) a;
			}
		}

		return null;
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
