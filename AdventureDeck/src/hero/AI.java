package hero;

import java.util.ArrayList;

import petri.api.GameEngine;
import petri.api.GameImage;

public class AI extends Player {
	

	public AI(GameEngine e, GameImage i) {
		super(e, i);
		// TODO Auto-generated constructor stub
	}
	
	public void endTurn() {
		
		ArrayList<Card> usableCards = findUsableCards();
		
		
		System.out.println("Usable cards:");
		for (Card c : usableCards) {
			System.out.println(c);
		}
		
		while (usableCards.size() > 0) {
			findBestCard(usableCards);
			usableCards = findUsableCards(usableCards);
		}
		
		System.out.println("AP left: " + getAP() + " / " + getAPpool());
		
		super.endTurn();
	}

	

	/**
	 * @param usableCards
	 */
	private void findBestCard(ArrayList<Card> usableCards) {
		for (Object o : usableCards.toArray()) {
			
			Card c = (Card)o;
			
			if (c.getName().contains("potion")) {
				if (getHealth() <= getMaxHealth() * .4)
					useCard(c, usableCards);
			}
			else if (c.getName().equalsIgnoreCase("walk"))
				useCard(c, usableCards);
			else
				usableCards.remove(c);
		}
		
		
	}
	
	/**
	 * @param c
	 */
	private void useCard(Card c, ArrayList<Card> usable) {
		usable.remove(c);
		useAP(c.getCost());
		System.out.println("Used " + c);
	}

	/**
	 * @return
	 */
	private double getMaxHealth() {
		return maxHealth;
	}

	/**
	 * @param usableCards
	 * @return
	 */
	private ArrayList<Card> findUsableCards(ArrayList<Card> toCheck) {
		ArrayList<Card> usableCards = new ArrayList<Card>();
		
		for (Card c: toCheck)
			if (c.getCost() <= getAP())
				usableCards.add(c);
		
		return usableCards;
	}

	/**
	 * @return
	 */
	private ArrayList<Card> findUsableCards() {
		ArrayList<Card> usableCards = new ArrayList<Card>();
		
		for (int x=0; x < basicHand.length; x++) {
			Card c = basicHand[x];
			if (c.getCost() <= getAP())
				usableCards.add(c);

		}
		
		for (int x=0; x < spellHand.length; x++) {
			Card c = spellHand[x];
			System.out.println(c);
			if (c.getCost() <= getAP())
				usableCards.add(c);

		}
		
		for (int x=0; x < itemHand.length; x++) {
			Card c = itemHand[x];
			System.out.println(c);
			if (c.getCost() <= getAP())
				usableCards.add(c);
		}
		return usableCards;
	}

}
