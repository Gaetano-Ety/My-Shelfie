package model.gameObjects;

import model.Color;

/**
 * Class that represents a spot the board or of the bookshelf
 */
public class Spot implements Cloneable{
	/**
	 * Color of this spot
	 */
	private final Color color;
	
	/**
	 * Constructor of the spot
	 *
	 * @param c color of the spot
	 */
	public Spot(Color c){
		color = c;
	}
	
	/**
	 * Method that controls if the spot is empty
	 */
	public boolean isVoid(){
		return color == Color.VOID;
	}
	
	public int getColorID(){
		return color.ordinal();
	}
	
	/**
	 * Method that returns the color of the spot
	 *
	 * @return color
	 */
	public Color getColor(){
		return color;
	}
	
	@Override
	public Spot clone(){
		try{
			return (Spot) super.clone();
		}catch(CloneNotSupportedException ignored){
			return null;
		}
	}
}