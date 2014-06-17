package hero;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * @author Cody
 * 
 */
public class Tile {

	public enum Types {
		DIRT, GRASS, WATER;
	}

	public enum Piece {
		CORNER, SIDE, CENTER;
	}

	public enum Position {
		TOPLEFT, TOP, TOPRIGHT, LEFT, CENTER, RIGHT, BOTLEFT, BOTTOM, BOTRIGHT;
	}

	private final int SIZE = 25;

	boolean canTraverse;

	private Types type;
	private Piece piece;
	private Position position;
	private BufferedImage image;

	public Tile() {
		canTraverse = true;
		type = Types.DIRT;
		piece = Piece.CENTER;
		position = Position.CENTER;

		try {
			image = ImageIO.read(getClass().getResourceAsStream(
					"Tiles/dirt0.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Tile(Types t) {
		super();
		type = t;

		if (t == Types.WATER)
			canTraverse = false;
	}

	public void detectType(Tile[][] board, int x, int y) {
		if ((x == 0 && y == 0)
				|| (x == board.length - 1 && y == board[x].length - 1)
				|| (x == 0 && y == board[x].length - 1)
				|| (y == 0 && x == board.length - 1))
			piece = Piece.CORNER;
		else if (x == 0 || y == 0 || x == board.length - 1
				|| y == board[x].length - 1)
			piece = Piece.SIDE;
		else if (board[x-1][y].type != type || board[x+1][y].type != type || board[x][y-1].type != type || board[x][y+1].type != type) {
			piece = Piece.SIDE;
		}
		else
			piece = Piece.CENTER;

		setImage();
	}

	/**
	 * 
	 */
	private void setImage() {
		if (type == Types.DIRT) {
			if (piece == Piece.CENTER) {
				image = loadImage("dirt4.png");
			} else if (piece == Piece.CORNER) {
				image = loadImage("dirt0.png");
			} else {
				image = loadImage("dirt1.png");
			}
		} else if (type == Types.GRASS) {
			if (piece == Piece.CENTER) {
				image = loadImage("grass4.png");
			} else if (piece == Piece.CORNER) {
				image = loadImage("grass0.png");
			} else {
				image = loadImage("grass1.png");
			}
		} else {
			if (piece == Piece.CENTER) {
				image = loadImage("water4.png");
			} else if (piece == Piece.CORNER) {
				image = loadImage("water0.png");
			} else {
				image = loadImage("water1.png");
			}
		}

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
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Could not load image " + name);
			return null;
		}
	}

	public void paint(int x, int y, Graphics g) {
		if (image != null)
			g.drawImage(image, x * SIZE, y * SIZE, x * SIZE + SIZE, y * SIZE
					+ SIZE, 0, 0, image.getWidth(), image.getHeight(), null);
	}

}
