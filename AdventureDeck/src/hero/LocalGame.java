/**
 * 
 */
package hero;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
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

	static final int CARD_TRAY_SIZE = 100;
	final static int TILE_SIZE = 25;

	final int ENEMY_DAMAGE = 12;

	private GameImage playerImage = null, aiImage = null, enemyCamp = null;
	public static GameImage slashImage = null, fireImage = null, enemyImage = null;

	BufferedImage crosshairs;

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
					.getResourceAsStream("slime.png"));
			enemyImage = new GameImage(loadEnemy);

			BufferedImage loadAI = ImageIO.read(getClass().getResourceAsStream(
					"enemy.png"));
			aiImage = new AnimatedImage(loadAI, 4, 4);

			crosshairs = ImageIO.read(getClass().getResourceAsStream(
					"crosshair2.png"));

			BufferedImage loadSlash = ImageIO.read(getClass()
					.getResourceAsStream("slash.png"));
			slashImage = new AnimatedImage(loadSlash, 5, 1);

			BufferedImage loadFire = ImageIO.read(getClass()
					.getResourceAsStream("fire.png"));
			fireImage = new AnimatedImage(loadFire, 5, 1);
			
			enemyCamp = new GameImage(ImageIO.read(getClass().getResourceAsStream("ruin.png")));

		} catch (Exception e) {
			e.printStackTrace();
			GameEngine.log("LocalGame exception: " + e.getMessage());
		}

		player = new Player(eng, playerImage);

		player.getCardHandler().giveLocalGame(this);

		EnemyCamp c1 = new EnemyCamp(eng, enemyCamp);

		EnemyCamp c2 = new EnemyCamp(eng, enemyCamp);


		ai = new AI(eng, aiImage);
		ai.getCardHandler().giveLocalGame(this);

		ai.setCenter(500, 150);

		engine.getActors().add(player);
		engine.getActors().add(c1);
		engine.getActors().add(c2);
		engine.getActors().add(ai);

		System.out.println("Begin generation: "
				+ (System.currentTimeMillis() - startTime));

		Executors.newCachedThreadPool().execute(new Runnable() {

			@Override
			public void run() {
				board = new TileManager(
						(engine.getEnvironmentSize().x - CARD_TRAY_SIZE) / 25,
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

		g.drawImage(board.getMapImage(), CARD_TRAY_SIZE, 0, board.getWidth(),
				board.getHeight(), null);

		if (showRange) {

			for (int outterY = 0; outterY < range.length; outterY++) {
				for (int innerX = 0; innerX < range[outterY].length; innerX++) {
					if (range[outterY][innerX] == 1) {
						int yTile = outterY - range.length / 2;
						int xTile = innerX - range[outterY].length / 2;

						g.drawImage(crosshairs, (int) player.getCenter().x
								+ xTile * TileManager.TILE_SIZE,
								(int) player.getCenter().y
										+ TileManager.TILE_SIZE + yTile
										* TileManager.TILE_SIZE,
								TileManager.TILE_SIZE, TileManager.TILE_SIZE,
								null);
					}
				}
			}
		}

		g.setColor(Color.LIGHT_GRAY);

		// Card tray
		g.fillRect(0, engine.getEnvironmentSize().y - CARD_TRAY_SIZE,
				engine.getEnvironmentSize().x, CARD_TRAY_SIZE);

		// Info tray
		g.fillRect(0, 0, CARD_TRAY_SIZE, engine.getEnvironmentSize().y);

		g.setColor(Color.black);

		g.setFont(g.getFont().deriveFont(25.0F));
		g.drawString("Turn: " + turnCnt, 5, 90);

		engine.getActors().drawActors(g);

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


		// Add more items to the shop if it's not already full

		((ShopMenu) engine.getGameMode("ShopMenu")).stockOptions();

		// Handle any statuses they may have (burn, poison, etc)
		for (Actor a : engine.getActors().getArrayList()) {
			if (a instanceof NPC)
				((NPC) a).endTurn();
		}
	}

	/**
	 * @param e
	 */
	private void checkChoice(MouseEvent e) {

		for (int x = 0; x < range.length; x++) {
			for (int y = 0; y < range[x].length; y++) {
				if (range[y][x] == 1) {

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
						player.confirmRemoveCard();
						return;
					}
				}
			}
		}
		// No valid choice made, cancel card
		showRange = false;
		player.cancelRemoveCard();
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
			// System.out.println("Actor: " + a + " " + actorPoint);

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
