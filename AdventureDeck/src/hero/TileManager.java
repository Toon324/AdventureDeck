package hero;

import hero.Tile.Types;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author Cody
 * 
 */
public class TileManager {

	Tile[][] board;

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
	 * 
	 */
	private void generateBoard() {
		Types lastType = Types.DIRT;
		for (int x = 0; x < board.length; x += 2) {
			for (int y = 0; y < board[x].length; y += 2) {
				Random gen = new Random();

				int choice = gen.nextInt(15);

				if (choice < 5) {
					board[x][y] = new Tile(lastType);
					board[x][y + 1] = new Tile(lastType);
					board[x + 1][y] = new Tile(lastType);
					board[x + 1][y+1] = new Tile(lastType);

				} else if (choice < 10) {
					board[x][y] = new Tile(Tile.Types.DIRT);
					board[x][y + 1] = new Tile(Tile.Types.DIRT);
					board[x + 1][y] = new Tile(Tile.Types.DIRT);
					board[x + 1][y+1] = new Tile(Tile.Types.DIRT);

					lastType = Types.DIRT;

				} else if (choice < 14) {
					board[x][y] = new Tile(Tile.Types.GRASS);
					board[x][y + 1] = new Tile(Tile.Types.GRASS);
					board[x + 1][y] = new Tile(Tile.Types.GRASS);
					board[x + 1][y+1] = new Tile(Tile.Types.GRASS);

					lastType = Types.GRASS;

				} else {
					board[x][y] = new Tile(Tile.Types.WATER);
					board[x][y + 1] = new Tile(Tile.Types.WATER);
					board[x + 1][y] = new Tile(Tile.Types.WATER);
					board[x + 1][y+1] = new Tile(Tile.Types.WATER);

					lastType = Types.WATER;

				}
			}
		}

		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
					board[x][y].detectType(board, x, y);
			}
		}
		
		generateBackgroundImage();
		System.out.println("Board done!");
		MainMenu.reportDone();
	}

	/**
	 * 
	 */
	private void generateBackgroundImage() {
		background = new BufferedImage(board.length * 25, board[0].length * 25, board[0][0].getImage().getType());
		
		Graphics g = background.getGraphics();
		
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				board[x][y].paint(x, y, g);
			}
		}
		
		
	}

	public void paint(Graphics g) {
		g.drawImage(background, 0, 0, board.length * 25, board[0].length * 25, null);
	}
}
