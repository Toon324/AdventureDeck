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
	
	private static final int WIDTH = 1225;
	private static final int HEIGHT = 700;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		gi = new GameInitializer("Card Hero", true);

		GameEngine engine = gi.getEngine();

		engine.setWindowSize(WIDTH, HEIGHT);
		gi.getFrame().setResizable(true);
		gi.getFrame().setSize(WIDTH, HEIGHT);

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
