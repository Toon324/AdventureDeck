/**
 * 
 */
package hero;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import petri.api.Button;
import petri.api.GameEngine;
import petri.api.GameMode;

/**
 * @author Cody
 *
 */
public class MainMenu extends GameMode {

	private static boolean done = false;
	private final Font terminal = new Font("Monospaced", Font.PLAIN, 50);
	
	public MainMenu(GameEngine eng) {
		super(eng);
		
		Button start = new Button("Start Game", 0, -50); //Initialize Buttons off screen so we can place them properly later.
        start.setColorScheme(Color.black, Color.LIGHT_GRAY, Color.black);
        
        start.setEnabled(false);
       
        buttons.add(start);
	}
	
	 @Override
     public void run(int ms) {
             //Place buttons depending on size of window
             buttons.get(0).set((engine.getEnvironmentSize().x/2)-(buttons.get(0).getWidth()/2), (engine.getEnvironmentSize().y/4) *1);
            
             //Check buttons
             if (done && buttons.get(0).isClicked())
                     engine.setCurrentGameMode("LocalGame"); //Set to game
            
            //If generation is done, enable button
            if (done)
            	buttons.get(0).setEnabled(true);
            
             super.run(ms);
     }
	 
	 /**
		 * 
		 */
		public static void reportDone() {
		done = true;
			
		}
	 
	 @Override
     public void paint(Graphics g) {
             g.setColor(Color.white);
             g.fillRect(0, 0, engine.getEnvironmentSize().x, engine.getEnvironmentSize().y);
             g.setColor(Color.black);
             g.setFont(terminal);
             engine.centerTextHorizontally(g, "Card Hero", 0, engine.getEnvironmentSize().x, 60);
             super.paint(g);
     }

}
