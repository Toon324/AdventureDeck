package hero;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

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

	Types type;
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

		// Board corners
		if (x == 0 && y == 0) {
			piece = Piece.CORNER;
			position = Position.TOPLEFT;
			setImage();
			return;
		} else if (x == board.length - 1 && y == board[x].length - 1) {
			piece = Piece.CORNER;
			position = Position.BOTRIGHT;
			setImage();
			return;
		} else if (x == 0 && y == board[x].length - 1) {
			piece = Piece.CORNER;
			position = Position.BOTLEFT;
			setImage();
			return;
		} else if (y == 0 && x == board.length - 1) {
			piece = Piece.CORNER;
			position = Position.TOPRIGHT;
			setImage();
			return;
		}

		// Board Sides
		else if (x == 0) {
			Tile up = board[x][y - 1];
			Tile down = board[x][y + 1];

			if (up.type != type) {
				piece = Piece.CORNER;
				position = Position.TOPLEFT;
				setImage();
				return;
			} else if (down.type != type) {
				piece = Piece.CORNER;
				position = Position.BOTLEFT;
				setImage();
				return;
			}

			piece = Piece.SIDE;
			position = Position.LEFT;
			setImage();
			return;
		} else if (y == 0) {
			Tile left = board[x - 1][y];
			Tile right = board[x + 1][y];

			if (left.type != type) {
				piece = Piece.CORNER;
				position = Position.TOPLEFT;
				setImage();
				return;
			} else if (right.type != type) {
				piece = Piece.CORNER;
				position = Position.TOPRIGHT;
				setImage();
				return;
			}

			piece = Piece.SIDE;
			position = Position.TOP;
			setImage();
			return;
		} else if (x == board.length - 1) {
			Tile up = board[x][y - 1];
			Tile down = board[x][y + 1];

			if (up.type != type) {
				piece = Piece.CORNER;
				position = Position.TOPRIGHT;
				setImage();
				return;
			} else if (down.type != type) {
				piece = Piece.CORNER;
				position = Position.BOTRIGHT;
				setImage();
				return;
			}

			piece = Piece.SIDE;
			position = Position.RIGHT;
			setImage();
			return;
		} else if (y == board[x].length - 1) {
			Tile left = board[x - 1][y];
			Tile right = board[x + 1][y];

			if (left.type != type) {
				piece = Piece.CORNER;
				position = Position.BOTLEFT;
				setImage();
				return;
			} else if (right.type != type) {
				piece = Piece.CORNER;
				position = Position.BOTRIGHT;
				setImage();
				return;
			}

			piece = Piece.SIDE;
			position = Position.BOTTOM;
			setImage();
			return;
		}

		Tile left = board[x - 1][y];
		Tile up = board[x][y - 1];
		Tile right = board[x + 1][y];
		Tile down = board[x][y + 1];

		// Detect corners
		if (left.type != type && up.type != type) {
			piece = Piece.CORNER;
			position = Position.TOPLEFT;
		} else if (right.type != type && up.type != type) {
			piece = Piece.CORNER;
			position = Position.TOPRIGHT;
		} else if (right.type != type && down.type != type) {
			piece = Piece.CORNER;
			position = Position.BOTRIGHT;
		} else if (left.type != type && down.type != type) {
			piece = Piece.CORNER;
			position = Position.BOTLEFT;
		}

		// Detect sides
		else if (left.type != type) {
			piece = Piece.SIDE;
			position = Position.LEFT;
		} else if (up.type != type) {
			piece = Piece.SIDE;
			position = Position.TOP;
		} else if (right.type != type) {
			piece = Piece.SIDE;
			position = Position.RIGHT;
		} else if (down.type != type) {
			piece = Piece.SIDE;
			position = Position.BOTTOM;
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
				image = TileManager.dirt4;
			} else if (piece == Piece.CORNER) {
				if (position == Position.TOPRIGHT)
					image = TileManager.dirttr;
				else if (position == Position.BOTRIGHT)
					image = TileManager.dirtbr;
				else if (position == Position.BOTLEFT)
					image = TileManager.dirtbl;
				else
					image = TileManager.dirt0;

			} else {
				if (position == Position.RIGHT)
					image = TileManager.dirtright;
				else if (position == Position.BOTTOM)
					image = TileManager.dirtbottom;
				else if (position == Position.LEFT)
					image = TileManager.dirtleft;
				else
					image = TileManager.dirt1;
			}
		} else if (type == Types.GRASS) {
			image = TileManager.grass4;
		} else {
			if (piece == Piece.CENTER) {
				image = TileManager.water4;
			} else if (piece == Piece.CORNER) {
				if (position == Position.TOPRIGHT)
					image = TileManager.watertr;
				else if (position == Position.BOTRIGHT)
					image = TileManager.waterbr;
				else if (position == Position.BOTLEFT)
					image = TileManager.waterbl;
				else
					image = TileManager.water0;

			} else {
				if (position == Position.RIGHT)
					image = TileManager.waterright;
				else if (position == Position.BOTTOM)
					image = TileManager.waterbottom;
				else if (position == Position.LEFT)
					image = TileManager.waterleft;
				else
					image = TileManager.water1;
			}
		}
	}

	public void paint(int x, int y, Graphics g) {
		if (image != null)
			g.drawImage(image, x * SIZE, y * SIZE, x * SIZE + SIZE, y * SIZE
					+ SIZE, 0, 0, image.getWidth(), image.getHeight(), null);
	}

	public BufferedImage getImage() {
		return image;
	}
}
