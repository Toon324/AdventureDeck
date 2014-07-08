package hero;

import hero.Card.CardType;

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
		Card lowestCost = null;
		for (Object o : usableCards.toArray()) {

			Card c = (Card) o;

			if (c.getName().contains("potion")) {
				if (getHealth() <= getMaxHealth() * .4) {
					useCard(c, usableCards);
					return;
				}
			} else if (c.getName().equalsIgnoreCase("walk")
					|| c.getName().equalsIgnoreCase("run")) {
				useCard(c, usableCards);
				return;
			} else if (lowestCost == null || c.getCost() < lowestCost.getCost())
				lowestCost = c;
		}

		useCard(lowestCost, usableCards);

	}

	/**
	 * @param c
	 */
	private void useCard(Card c, ArrayList<Card> usable) {
		System.out.println("Used " + c);
		usable.remove(c);
		useAP(c.getCost());

		handleCard(c);

		if (c.getCardType() == CardType.BASIC)
			for (int x = 0; x < basicHand.length; x++) {
				if (basicHand[x] == c) {
					basicHand[x] = null;
					basicDeck.add(c);
					return;
				}

			}
		else if (c.getCardType() == CardType.SPELL)
			for (int x = 0; x < spellHand.length; x++) {
				if (spellHand[x] == c) {
					spellHand[x] = null;
					spellDeck.add(c);
					return;
				}

			}
		else if (c.getCardType() == CardType.ITEM)
			for (int x = 0; x < itemHand.length; x++) {
				if (itemHand[x] == c) {
					itemHand[x] = null;
					itemDeck.add(c);
					return;
				}

			}

	}

	/**
	 * @param c
	 */
	private void handleCard(Card c) {
		cardHandler.handleChoice(c, 1, 1);

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

		for (Card c : toCheck)
			if (c.getCost() <= getAP())
				usableCards.add(c);

		return usableCards;
	}

	/**
	 * @return
	 */
	private ArrayList<Card> findUsableCards() {
		ArrayList<Card> usableCards = new ArrayList<Card>();

		for (int x = 0; x < basicHand.length; x++) {
			Card c = basicHand[x];
			if (c != null && c.getCost() <= getAP())
				usableCards.add(c);

		}

		for (int x = 0; x < spellHand.length; x++) {
			Card c = spellHand[x];
			System.out.println(c);
			if (c != null && c.getCost() <= getAP())
				usableCards.add(c);

		}

		for (int x = 0; x < itemHand.length; x++) {
			Card c = itemHand[x];
			System.out.println(c);
			if (c != null && c.getCost() <= getAP())
				usableCards.add(c);
		}
		return usableCards;
	}

}
