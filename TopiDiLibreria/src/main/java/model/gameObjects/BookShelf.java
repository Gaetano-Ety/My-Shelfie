package model.gameObjects;

import model.Color;
import model.exceptions.FullColumnException;
import model.exceptions.InvalidMatrixException;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class represents one bookshelf of a single player
 */
public class BookShelf extends SpotMatrix{
	private final static int numRows = 6;
	private final static int numCols = 5;
	
	/**
	 * Bookshelf constructor that initializes every spot of the bookshelf with the color void
	 */
	public BookShelf(){
		super(numCols, numRows);
	}
	
	public static int getNumCols(){
		return numCols;
	}
	
	public static int getNumRows(){
		return numRows;
	}
	
	/**
	 * Make the bookshelf like a SpotMatrix
	 */
	public void copyMatrix(SpotMatrix toCopy) throws InvalidMatrixException{
		Spot[][] copy = toCopy.getMatrix();
		if(!verifyMatrix(copy)) throw new InvalidMatrixException();
		matrix = copy;
	}
	
	/**
	 * method that returns the number of free spot in the selected column
	 *
	 * @param col column
	 * @return number of free spots
	 */
	public int freeInColumn(int col){
		int count = 0;
		for(Spot s : matrix[col])
			if(s.isVoid()) count++;
			else break;
		return count;
	}
	
	/**
	 * Add a tile to the specified column if it is free
	 */
	public void addCard(int col, Color c) throws FullColumnException{
		if(c == Color.V) return;
		int free = freeInColumn(col);
		if(free < 1) throw new FullColumnException();
		matrix[col][free - 1] = new Spot(c);
	}
	
	/**
	 * @return the number of groups that have at least n adjacent cards of the same color (COLOR-SENSITIVE)
	 * This method doesn't count the void spots groups.
	 */
	public int nOfGroups(int n){
		int count = 0;
		ArrayList<String> actualGroup;
		ArrayList<String> totalCounted = new ArrayList<>();
		
		for(int c = 0; c < numCols; c++)
			for(int r = 0; r < numRows; r++){
				if(matrix[c][r].isVoid()) continue;
				
				if(!totalCounted.contains(Arrays.toString(new int[]{c, r}))){
					actualGroup = new ArrayList<>();
					countGroup(actualGroup, c, r, true);
					if(actualGroup.size() >= n) count++;
					totalCounted.addAll(actualGroup);
				}
			}
		return count;
	}
	
	/**
	 * Verify if a matrix of spots can be a bookshelf.
	 * A bookshelf has, for every column, the initial part void (if it isn't full) and the final part non-void.
	 * Not exists a void spot after a non-void spot.
	 *
	 * @return true if these propriety are respected, false else
	 */
	private boolean verifyMatrix(Spot[][] mat){
		// Mat must have the same sizes of the board
		if(!verifyMatrix(mat, numCols, numRows)) return false;
		
		// Forall c Exists i Forall r ((r>i => !mat[c][r].isVoid()) (r<=i => mat[c][r].isVoid()))
		for(int c = 0; c < numCols; c++){
			boolean find = false;
			for(int r = 0; r < numRows; r++){
				if(mat[c][r].isVoid()){
					if(find) return false;
				}else find = true;
			}
		}
		return true;
	}
}