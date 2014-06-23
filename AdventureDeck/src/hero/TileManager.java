package hero;

import hero.Tile.Types;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * @author Cody
 * 
 */
public class TileManager {
	
	final static int TILE_SIZE = 25;

	Tile[][] board;

	// Cached tile images
	static BufferedImage grass4;
	static BufferedImage dirt0, dirt1, dirt4, dirtright, dirtbottom, dirtleft, dirttr,
			dirtbr, dirtbl;
	static BufferedImage water0, water1, water4, waterright, waterbottom, waterleft,
			watertr, waterbr, waterbl;

	private int size = 10;

	private BufferedImage background;

	private boolean done;

	public TileManager() {
		board = new Tile[size][size];
		generateBoard();
	}

	public TileManager(int x, int y) {
		System.out.println("Board of size " + x + " " + y + " created.");
		board = new Tile[x][y];
		generateBoard();
	}
	
	/**
	 * @param string
	 * @return
	 */
	private BufferedImage loadImage(String name) {
		try {
			return ImageIO
					.read(getClass().getResourceAsStream("Tiles/" + name));
		} catch (IOException e) {
			// TODO Auto-generated catch block\
			System.out.println("Could not load image " + name);
			e.printStackTrace();
			
			return null;
		}
	}

	/**
	 * 
	 */
	private void generateBoard() {
		//Preload in tile images
		//magic();
		grass4 = loadImage("grass4.png");
		
		dirt0 = loadImage("dirt0.png");
		dirt1 = loadImage("dirt1.png");
		dirt4 = loadImage("dirt4.png");
		dirtright = loadImage("dirtright.png");
		dirtbottom = loadImage("dirtbottom.png");
		dirtleft = loadImage("dirtleft.png");
		dirttr = loadImage("dirttr.png");
		dirtbr = loadImage("dirtbr.png");
		dirtbl = loadImage("dirtbl.png");
		
		water0 = loadImage("water0.png");
		water1 = loadImage("water1.png");
		water4 = loadImage("water4.png");
		waterright = loadImage("waterright.png");
		waterbottom = loadImage("waterbottom.png");
		waterleft = loadImage("waterleft.png");
		watertr = loadImage("watertr.png");
		waterbr = loadImage("waterbr.png");
		waterbl = loadImage("waterbl.png");
		
		Types lastType = Types.DIRT;
		for (int x = 0; x < board.length; x += 2) {
			for (int y = 0; y < board[x].length; y += 2) {
				Random gen = new Random();

				int choice = gen.nextInt(15);

				if (choice < 5) {
					if (y != 0 && board[x][y - 1].type == Types.WATER)
						genTileBlock(board, x, y, Types.WATER);
					else
						genTileBlock(board, x, y, lastType);

				} else if (choice < 10) {
					genTileBlock(board, x, y, Types.DIRT);

					lastType = Types.DIRT;

				} else if (choice < 14) {
					if (x != 0 && board[x - 1][y].type == Types.WATER)
						genTileBlock(board, x, y, Types.WATER);
					else
						genTileBlock(board, x, y, Types.GRASS);

					lastType = Types.GRASS;

				} else {
					genTileBlock(board, x, y, Types.WATER);

					lastType = Types.WATER;

				}
			}
		}
		System.out.println("Tiles created: "
				+ (System.currentTimeMillis() - LocalGame.startTime));

		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				board[x][y].detectType(board, x, y);
			}
		}
		System.out.println("Edges detected: "
				+ (System.currentTimeMillis() - LocalGame.startTime));

		generateBackgroundImage();
		System.out.println("Board done! "
				+ (System.currentTimeMillis() - LocalGame.startTime));
		MainMenu.reportDone();
	}

	private void genTileBlock(Tile[][] board, int x, int y, Types type) {
		board[x][y] = new Tile(type);
		board[x][y + 1] = new Tile(type);
		board[x + 1][y] = new Tile(type);
		board[x + 1][y + 1] = new Tile(type);

	}

	/**
	 * 
	 */
	private void generateBackgroundImage() {
		background = new BufferedImage(board.length * TILE_SIZE, board[0].length * TILE_SIZE,
				board[0][0].getImage().getType());

		Graphics g = background.getGraphics();

		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				board[x][y].paint(x, y, g);
			}
		}

	}

	public void paint(Graphics g) {
		g.drawImage(background, 0, 0, board.length * TILE_SIZE, board[0].length * TILE_SIZE,
				null);
	}
}
