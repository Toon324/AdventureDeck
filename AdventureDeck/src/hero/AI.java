package hero;

import hero.Card.CardType;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;

import petri.api.Actor;
import petri.api.GameEngine;
import petri.api.GameImage;

public class AI extends Player {

	public AI(GameEngine e, GameImage i) {
		super(e, i);
		// TODO Auto-generated constructor stub
	}

	public void endTurn() {
		
		if (death)
			return;

		ArrayList<Card> usableCards = findUsableCards();

		while (usableCards.size() > 0) {
			findBestCard(usableCards);
			usableCards = findUsableCards(usableCards);
		}
//
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
		//System.out.println("Used " + c);
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
					return;
				}

			}

	}

	/**
	 * @param c
	 */
	private void handleCard(Card c) {
		
		if (c.getRange() == null) {
			GameEngine.log(c + " doesn't have a range!");
			return;
		}

		if (c.getRange().length == 1) // self
			cardHandler.handleChoice(c, 0, 0);
		else {
			if (c.getName().equalsIgnoreCase("walk") || c.getName().equalsIgnoreCase("run")) {
				// Movement
				
				NPC closest = getClosestTarget();
				//System.out.println("Closest target: " + closest);
				//System.out.println("Distance: " + closest.getCenter().distance(getCenter()));
				if (closest.getCenter().distance(getCenter()) <= 50)
					return; //Don't get closer than 1 tile
				
				//Based on centers, decides which direction to use to get to the closest target
				Point2D.Float p = closest.getCenter();
				int x = 0;
				int y = 0;
				
				if (p.getX() > getCenter().getX())
					x = 1;
				else
					x = -1;
				
				if (p.getY() > getCenter().getY())
					y = 1;
				else
					y = -1;
				
				//Go twice as far with run
				if (c.getName().equalsIgnoreCase("run")) {
					x *= 2;
					y *= 2;
				}
				
				cardHandler.handleChoice(c, x, y);

			} else {
				// Combat
				
				NPC closest = getClosestTarget();
				cardHandler.setCurrentTarget(closest);
				//System.out.println("Closest target: " + closest);
				
				//Based on centers, decides which direction to aim attack
				Point2D.Float p = closest.getCenter();
				int x = 0;
				int y = 0;
				
				if (p.getX() > getCenter().getX())
					x = 1;
				else
					x = -1;
				
				if (p.getY() > getCenter().getY())
					y = 1;
				else
					y = -1;
				
				cardHandler.handleChoice(c, x, y);
			}
		}

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

	private NPC getClosestTarget() {
		NPC closest = null;
		double smallestDistance = -1;

		for (Actor a : engine.getActors().getArrayList()) {
			if (!a.equals(this)) {
				NPC n = (NPC) a;
				double distance = n.getCenter().distance(getCenter());
				
				if (smallestDistance == -1 || distance < smallestDistance) {
					smallestDistance = distance;
					closest = n;
				}
			}
		}

		return closest;
	}

}
