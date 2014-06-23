package hero;

import java.awt.Point;

import petri.api.Actor;

/**
 * @author Cody
 * 
 */
public class CardHandler {

	LocalGame game;

	public CardHandler(LocalGame lg) {
		game = lg;
	}

	public void handleCard(Card c) {
		String cmd = c.getName();

		switch (cmd) {
		case "sword":
			game.attack();
			return;
		case "block":
			return;
		case "bow":
			int[][] bowRange = { { 1, 0, 0, 1, 0, 0, 1 },
					{ 0, 1, 0, 1, 0, 1, 0 }, { 0, 0, 1, 1, 1, 0, 0 },
					{ 1, 1, 1, 0, 1, 1, 1 }, { 0, 0, 1, 1, 1, 0, 0 },
					{ 0, 1, 0, 1, 0, 1, 0 }, { 1, 0, 0, 1, 0, 0, 1 } };
			game.giveOption(bowRange, c);
			return;
		case "smallPotion":
			game.player.heal(game.SMALL_POTION_AMT);
			return;
		case "largePotion":
			game.player.heal(game.LARGE_POTION_AMT);
			return;
		case "shop":
			game.engine.setCurrentGameMode(2);
			return;

		case "walk":
			int[][] walkRange = { { 1, 1, 1 }, { 1, 0, 1 }, { 1, 1, 1 } };
			game.giveOption(walkRange, c);
			return;

		case "run":
			int[][] runRange = { { 1, 0, 1, 0, 1 }, { 0, 1, 1, 1, 0 },
					{ 1, 1, 0, 1, 1 }, { 0, 1, 1, 1, 0 }, { 1, 0, 1, 0, 1 } };
			game.giveOption(runRange, c);
			return;

		}

	}

	/**
	 * @param choiceCard
	 * @param x
	 * @param y
	 */
	public void handleChoice(Card c, float x, float y) {
		String cmd = c.getName();

		switch (cmd) {
		case "sword":
			game.attack();
			return;
		case "block":
			return;
		case "bow":
			bowAttack(x, y);
			return;

		case "walk":
			setDirection(x, y);
			game.player.moveSpace(1);
			return;

		case "run":
			setDirection(x, y);
			game.player.moveSpace(2);
			return;

		}

	}

	/**
	 * @param x
	 * @param y
	 */
	private void bowAttack(float x, float y) {
		// TODO Auto-generated method stub
		for (Actor a : game.engine.getActors().getArrayList()) {
			if (a instanceof Enemy) {
				Enemy e = (Enemy) a;
				
				Point click = new Point((int)(game.player.getCenter().x + x * game.TILE_SIZE),(int) (game.player.getCenter().y + y * game.TILE_SIZE));
				
				if (e.checkClick(click))
					e.dealDamage(game.BOW_DAMAGE);
			}
		}
	}

	/**
	 * @param x
	 * @param y
	 */
	private void setDirection(float x, float y) {
		double angle = Math.toDegrees(Math.atan(y / x));

		if (angle == -90.0)
			game.player.setDir(0);
		else if (x > 0 && y < 0)
			game.player.setDir(1);
		else if (x > 0 && y == 0)
			game.player.setDir(2);
		else if (x > 0 && y > 0)
			game.player.setDir(3);
		else if (angle == 90.0)
			game.player.setDir(4);
		else if (x < 0 && y > 0)
			game.player.setDir(5);
		else if (x < 0 && y == 0)
			game.player.setDir(6);
		else
			game.player.setDir(7);
	}
}
