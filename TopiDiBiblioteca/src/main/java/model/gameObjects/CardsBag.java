package model.gameObjects;


import model.Color;
import model.exceptions.InvalidActionException;
import model.exceptions.InvalidColorException;

import java.util.Arrays;

public class CardsBag{
	private int[] remainingCards = new int[Color.nOfColors()];
	private int sum;
	
	public CardsBag(){
		int numOfCards = 22; // Number of cards for color
		Arrays.fill(remainingCards, numOfCards);
		sum = numOfCards * Color.nOfColors();
	}
	
	public CardsBag(int[] cards) throws InvalidActionException{
		if(cards.length != Color.nOfColors()) throw new InvalidActionException();
		remainingCards = cards;
		sum = Arrays.stream(cards).sum();
	}
	
	/**
	 * @return a random card from the remaining; if the cardsBag is empty return void color
	 */
	public Color takeACard(){
		int random = (int) (Math.random() * sum) + 1; // Random number between 1 and the amount of remaining cards
		
		for(int c = 0; c < Color.nOfColors(); c++){
			random -= remainingCards[c];
			if(random <= 0){
				remainingCards[c]--;
				sum--;
				try{
					return Color.colorIn(c + 1); // c+1 because 0 is the void color
				}catch(InvalidColorException ignored){/* Impossible */}
			}
		}
		
		return Color.VOID;
	}
	
	public boolean isEmpty(){
		return sum == 0;
	}
	
	public void addCard(Color c) throws InvalidColorException{
		int n = c.ordinal();
		remainingCards[n - 1]++;
		sum++;
	}
	
	@Override
	public String toString(){
		return Arrays.toString(remainingCards);
	}
}

