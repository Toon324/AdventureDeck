/**
 * 
 */
package hero;

import java.io.IOException;

import javax.imageio.ImageIO;

import petri.api.GameEngine;
import petri.api.GameInitializer;

/**
 * @author Cody
 * 
 */
public class CardHero {

	static GameInitializer gi;
	
	private static final int WIDTH = 1225;
	private static final int HEIGHT = 700;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CardHero();
	}
	
	public CardHero() {
		
		gi = new GameInitializer("Card Hero", true);

		GameEngine engine = gi.getEngine();

		engine.setWindowSize(WIDTH, HEIGHT);
		gi.getFrame().setResizable(false);
		gi.getFrame().setSize(WIDTH, HEIGHT);
		
		try {
			gi.getFrame().setIconImage(ImageIO.read(this.getClass().getResourceAsStream("icon.png")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
