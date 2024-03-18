package model;

import model.exceptions.InvalidColorException;


/**
 * Represents all the possible colors of a spot;
 * If the spot is empty, the color is considered VOID
 */
public enum Color{
	VOID,
	CATS,
	BOOKS,
	GAMES,
	PLANTS,
	FRAMES,
	TROPHIES;
	
	// Convenience names.
	public static final Color V = VOID;
	public static final Color C = CATS;
	public static final Color B = BOOKS;
	public static final Color P = PLANTS;
	public static final Color G = GAMES;
	public static final Color F = FRAMES;
	public static final Color T = TROPHIES;
	
	/**
	 * @return the color of the index taken in input
	 */
	public static Color colorIn(int n) throws InvalidColorException{
		if(n < 0 || n > size()) throw new InvalidColorException();
		return values()[n];
	}
	
	/**
	 * @return the color which match with the string taken in input
	 * The method is case-insensitive, and it works both with whole words and with abbreviations
	 */
	public static Color colorLike(String str) throws InvalidColorException{
		return switch(str.toUpperCase()){
			case "V", "VOID", " " -> Color.V;
			case "C", "CATS" -> Color.C;
			case "B", "BOOKS" -> Color.B;
			case "P", "PLANTS" -> Color.P;
			case "G", "GAMES" -> Color.G;
			case "F", "FRAMES" -> Color.F;
			case "T", "TROPHIES" -> Color.T;
			default -> throw new InvalidColorException();
		};
	}
	
	/**
	 * @return the numbers of colors, void included
	 */
	public static int size(){return values().length;}
	
	/**
	 * @return the numbers of colors void excluded
	 */
	public static int nOfColors(){return values().length - 1;}
}
