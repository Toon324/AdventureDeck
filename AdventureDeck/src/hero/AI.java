package hero;

import petri.api.GameEngine;
import petri.api.GameImage;

public class AI extends Player {
	

	public AI(GameEngine e, GameImage i) {
		super(e, i);
		// TODO Auto-generated constructor stub
	}
	
	public void endTurn() {
		
		
		System.out.println("Hand: ");
		for (int x=0; x < basicHand.length; x++) {
			Card c = basicHand[x];
			System.out.println(c);
			if (c.getCost() <= getAP()) {
				useAP(c.getCost());
				cardHandler.handleCard(c);
				System.out.println("Used card " + c);
				
			}
		}
		System.out.println("AP left: " + getAP() + " / " + getAPpool());
		
		super.endTurn();
	}

}
