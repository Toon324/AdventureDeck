package hero;

import hero.Tile.Types;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 * @author Cody
 * 
 */
public class TileManager {

	Tile[][] board;

	private int size = 10;

	public TileManager() {
		board = new Tile[size][size];
		generateBoard();
	}

	public TileManager(int x, int y) {
		board = new Tile[x][y];
		generateBoard();
	}

	/**
	 * 
	 */
	private void generateBoard() {
		Types lastType = Types.DIRT;
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				Random gen = new Random();

				int choice = gen.nextInt(15);

				if (choice < 5) {
					board[x][y] = new Tile(lastType);
				} else if (choice < 10) {
					board[x][y] = new Tile(Tile.Types.DIRT);
					lastType = Types.DIRT;
				} else if (choice < 14) {
					board[x][y] = new Tile(Tile.Types.GRASS);
					lastType = Types.GRASS;
				} else {
					board[x][y] = new Tile(Tile.Types.WATER);
					lastType = Types.WATER;
				}
			}
		}

		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				board[x][y].detectType(board, x, y);
			}
		}

	}

	public void paint(Graphics g) {
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				board[x][y].paint(x, y, g);
			}
		}
	}

}
