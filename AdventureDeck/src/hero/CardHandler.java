package hero;

import java.util.LinkedList;

import petri.api.GameEngine;

/**
 * @author Cody
 * 
 */
public class CardHandler {

	LocalGame game;
	Player player;
	NPC currentTarget;

	public CardHandler(Player p) {
		player = p;
	}

	public void giveLocalGame(LocalGame lg) {
		game = lg;
	}

	public void handleCard(Card c) {

		int[][] range = c.getRange();

		if (range == null) {
			GameEngine.log(c + " doesn't have a range!");
			return;
		}

		// Check to see if spell targets self
		if (range.length == 1) {
			// System.out.println("Targetting self");
			handleChoice(c, 0, 0);
		} else
			game.giveOption(c.getRange(), c);

	}

	/**
	 * @param choiceCard
	 * @param x
	 * @param y
	 */
	public void handleChoice(Card c, float x, float y) {
		LinkedList<String> effects = c.getEffects();

		if (effects == null) {
			GameEngine.log(c + " does not have any effects.");
			return;
		} else if (effects.size() == 1) {
			GameEngine.log(c + " does not have any arguments.");
			return;
		}

		for (int num = 0; num < effects.size(); num++) {

			String cmd = effects.get(num);
			// System.out.println("CMD: " + cmd);
			num++;
			String arg = "";
			if (num < effects.size())
				arg = effects.get(num);

			// System.out.println("ARG: " + arg);

			if (arg.contains("}"))
				arg = arg.replace("}", "");

			switch (cmd) {
			case "PLAYERHEAL": {
				int amt = Integer.valueOf(arg);

				player.heal(amt);
				break;
			}
			case "PLAYERMOVE": {

				setDirection(x, y);

				x = Math.abs(x);
				y = Math.abs(y);

				// Simple check to see which is moving farther, then moves that
				// number
				// of tiles.
				if (x > y)
					player.moveSpace((int) x);
				else
					player.moveSpace((int) y);

				break;
			}
			case "PLAYERDAMAGE": {

				int amt = Integer.valueOf(arg);

				player.dealDamage(amt);
				break;
			}
			case "TARGETDAMAGE": {
				if (currentTarget == null)
					return;

				int amt = Integer.valueOf(arg);
				if (c.getRange()[0].length == 3)
					amt += player.bonusSwordDamage;
				else
					amt += player.bonusBowDamage;

				System.out.println("Damage done: " + amt);

				int tilesBetween = (int) (currentTarget.getCenter().distance(
						player.getCenter()) / game.TILE_SIZE);
				// System.out.println("tiles between: " + tilesBetween);
				if (tilesBetween <= (c.getRange().length - 1) / 2) {
					currentTarget.dealDamage(amt);
					if (currentTarget.getHealth() <= 0)
						player.addGold(currentTarget.getGoldValue());
				}
				break;
			}
			case "TARGETSTATUS": {
				int tilesBetween = (int) (currentTarget.getCenter().distance(
						player.getCenter()) / game.TILE_SIZE);
				// System.out.println("tiles between: " + tilesBetween);
				if (tilesBetween > (c.getRange().length - 1) / 2)
					break;
				switch (arg) {
				case "burn":
					num++;
					int amt = Integer.valueOf(effects.get(num));
					if (currentTarget != null)
						currentTarget.inflictStatus("burn", amt);
				}

				break;
			}
			case "SHOP": {
				game.engine.setCurrentGameMode(2);
				break;
			}
			case "ENDTURN": {
				game.endTurn();
				break;
			}
			case "BUFFHEALTH": {
				int amt = Integer.valueOf(arg);
				player.addHealth(amt);
				break;
			}
			case "BUFFSWORDDAMAGE": {
				int amt = Integer.valueOf(arg);
				player.buffSword(amt);
				break;
			}
			case "BUFFBOWDAMAGE": {
				int amt = Integer.valueOf(arg);
				player.buffBow(amt);
				break;
			}
			}

		}

		// game.endTurn();

	}

	/**
	 * @param x
	 * @param y
	 */
	private void setDirection(float x, float y) {
		double angle = Math.toDegrees(Math.atan(y / x));

		if (angle == -90.0)
			player.setDir(0);
		else if (x > 0 && y < 0)
			player.setDir(1);
		else if (x > 0 && y == 0)
			player.setDir(2);
		else if (x > 0 && y > 0)
			player.setDir(3);
		else if (angle == 90.0)
			player.setDir(4);
		else if (x < 0 && y > 0)
			player.setDir(5);
		else if (x < 0 && y == 0)
			player.setDir(6);
		else
			player.setDir(7);
	}

	public void setCurrentTarget(NPC n) {
		currentTarget = n;
	}
}
