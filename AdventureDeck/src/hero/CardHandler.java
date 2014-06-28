package hero;

import java.awt.Point;
import java.util.LinkedList;

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

		int[][] range = c.getRange();

		// Check to see if spell targets self
		if (range.length == 1) {
			// System.out.println("Targetting self");
			handleChoice(c, 0, 0);
		} else
			game.giveOption(c.getRange(), c);

		// String cmd = c.getName();
		//
		// switch (cmd) {
		// case "sword":
		// swordAttack();
		// game.endTurn();
		// return;
		// case "block":
		// return;
		// case "bow":
		// int[][] bowRange = { { 1, 0, 0, 1, 0, 0, 1 },
		// { 0, 1, 0, 1, 0, 1, 0 }, { 0, 0, 1, 1, 1, 0, 0 },
		// { 1, 1, 1, 0, 1, 1, 1 }, { 0, 0, 1, 1, 1, 0, 0 },
		// { 0, 1, 0, 1, 0, 1, 0 }, { 1, 0, 0, 1, 0, 0, 1 } };
		// game.giveOption(bowRange, c);
		// return;
		// case "smallPotion":
		// game.player.heal(game.SMALL_POTION_AMT);
		// game.endTurn();
		// return;
		// case "largePotion":
		// game.player.heal(game.LARGE_POTION_AMT);
		// game.endTurn();
		// return;
		// case "shop":
		// game.engine.setCurrentGameMode(2);
		// game.endTurn();
		// return;
		//
		// case "walk":
		// int[][] walkRange = { { 1, 1, 1 }, { 1, 0, 1 }, { 1, 1, 1 } };
		// game.giveOption(walkRange, c);
		// return;
		//
		// case "run":
		// int[][] runRange = { { 1, 0, 1, 0, 1 }, { 0, 1, 1, 1, 0 },
		// { 1, 1, 0, 1, 1 }, { 0, 1, 1, 1, 0 }, { 1, 0, 1, 0, 1 } };
		// game.giveOption(runRange, c);
		// return;
		//
		// }

	}

	/**
	 * 
	 */
	private void swordAttack() {
		NPC closest = null;
		int distance = -1;

		for (Actor a : game.engine.getActors().getArrayList())
			if (a instanceof NPC)
				if (closest == null || game.player.distanceTo(a) < distance) {
					closest = (NPC) a;
					distance = game.player.distanceTo(a);
				}

		if (distance <= game.TILE_SIZE * 2) {
			closest.dealDamage(game.bonusSwordDamage);
			game.player.dealDamage(game.ENEMY_DAMAGE);

			if (closest.getHealth() <= 0)
				game.player.addGold(closest.getGoldValue());
		}

	}

	/**
	 * @param choiceCard
	 * @param x
	 * @param y
	 */
	public void handleChoice(Card c, float x, float y) {
		LinkedList<String> effects = c.getEffects();

		for (int num = 0; num < effects.size(); num++) {

			String cmd = effects.get(num);
			// System.out.println("CMD: " + cmd);
			num++;
			String arg = effects.get(num);

			// System.out.println("ARG: " + arg);

			if (arg.contains("}"))
				arg = arg.replace("}", "");

			switch (cmd) {
			case "PLAYERHEAL": {
				int amt = Integer.valueOf(arg);

				game.player.heal(amt);
				break;
			}
			case "PLAYERMOVE": {
				int amt = Integer.valueOf(arg);

				setDirection(x, y);

				x = Math.abs(x);
				y = Math.abs(y);

				// Simple check to see which is moving farther, then moves that
				// number
				// of tiles.
				if (x > y)
					game.player.moveSpace((int) x);
				else
					game.player.moveSpace((int) y);

				break;
			}
			case "PLAYERDAMAGE": {
				int amt = Integer.valueOf(arg);

				game.player.dealDamage(amt);
				break;
			}
			case "TARGETDAMAGE": {
				int amt = Integer.valueOf(arg);

				if (game.currentTarget != null) {
					game.currentTarget.dealDamage(amt);
					if (game.currentTarget.getHealth() <= 0)
						game.player.addGold(game.currentTarget.getGoldValue());
				}
				break;
			}
			case "TARGETSTATUS": {
				switch (arg) {
				case "burn":
					num++;
					int amt = Integer.valueOf(effects.get(num));
					if (game.currentTarget != null)
						game.currentTarget.inflictStatus("burn", amt);
				}

				break;
			}
			case "SHOP": {
				game.engine.setCurrentGameMode(2);
			}
			case "BUFFHEALTH": {
				int amt = Integer.valueOf(arg);
				game.player.addHealth(amt);
				break;
			}
			case "BUFFSWORDDAMAGE": {
				int amt = Integer.valueOf(arg);
				game.bonusSwordDamage += amt;
				break;
			}
			}

		}

		game.endTurn();

		// switch (cmd) {
		// case "sword":
		// swordAttack();
		// game.endTurn();
		// return;
		// case "block":
		// game.endTurn();
		// return;
		// case "bow":
		// bowAttack(x, y);
		// game.endTurn();
		// return;
		//
		// case "walk":
		// setDirection(x, y);
		// game.player.moveSpace(1);
		// game.endTurn();
		// return;
		//
		// case "run":
		// setDirection(x, y);
		//
		// x = Math.abs(x);
		// y = Math.abs(y);
		//
		// //Simple check to see which is moving farther, then moves that number
		// of tiles.
		// if (x > y)
		// game.player.moveSpace((int)x);
		// else
		// game.player.moveSpace((int)y);
		//
		// game.endTurn();
		// return;
		//
		// }

	}

	/**
	 * @param x
	 * @param y
	 */
	private void bowAttack(float x, float y) {
		// TODO Auto-generated method stub
		for (Actor a : game.engine.getActors().getArrayList()) {
			if (a instanceof NPC) {
				NPC e = (NPC) a;

				Point click = new Point((int) (game.player.getCenter().x + x
						* game.TILE_SIZE), (int) (game.player.getCenter().y + y
						* game.TILE_SIZE));

				if (e.checkClick(click)) {
					e.dealDamage(game.bonusBowDamage);
					if (e.getHealth() <= 0)
						game.player.addGold(e.getGoldValue());
				}
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
