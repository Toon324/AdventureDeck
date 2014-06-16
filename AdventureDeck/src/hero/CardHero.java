/**
 * 
 */
package hero;

import petri.api.GameEngine;
import petri.api.GameInitializer;

/**
 * @author Cody
 * 
 */
public class CardHero {

	private static GameInitializer gi;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		gi = new GameInitializer("Card Hero", true);

		GameEngine engine = gi.getEngine();

		engine.setWindowSize(1200, 700);
		gi.getFrame().setResizable(true);
		gi.getFrame().setSize(1200, 700);

		MainMenu mainMenu = new MainMenu(engine);
		LocalGame localGame = new LocalGame(engine);
		ShopMenu shop = new ShopMenu(engine);

		engine.addGameMode(mainMenu);
		engine.addGameMode(localGame);
		engine.addGameMode(shop);

		engine.setCurrentGameMode(0);

		gi.startGame();
	}

	public static void stopGame() {
		gi.stopGame();
		System.exit(0);
	}

}
