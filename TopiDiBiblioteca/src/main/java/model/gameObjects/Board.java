package model.gameObjects;

import model.Color;
import model.exceptions.InvalidActionException;
import model.exceptions.InvalidColorException;
import model.exceptions.InvalidMatrixException;
import model.exceptions.InvalidPlayersNumberException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Board extends SpotMatrix{
	final private static int BOARD_LENGTH = 9;
	final private int[][] shape; // Shapes of game board
	private CardsBag bag;
	
	/**
	 * Generate the shape based one the players number using a factory pattern
	 */
	public Board(int nPlayers) throws InvalidPlayersNumberException {
		super(BOARD_LENGTH, BOARD_LENGTH);
		if(nPlayers < 2 || nPlayers > 4) throw new InvalidPlayersNumberException();
		
		shape = PossibleShape.decideShape(nPlayers);
		bag = new CardsBag();
	}
	
	/**
	 * Assigns a copy of another spotMatrix to the matrix
	 */
	public void copyMatrix(SpotMatrix toCopy) throws InvalidMatrixException {
		Spot[][] copy = toCopy.getMatrix();
		if(!verifyMatrix(copy)) throw new InvalidMatrixException();
		matrix = copy;
	}
	
	/**
	 * Empty the board of the remaining cards;
	 * Fill the board with the cards in the bag;
	 * If the bag empties before the board is completely filled, the board is left as it is
	 */
	public void fill(){
		// First round to void the matrix
		for(int x = 0; x < BOARD_LENGTH; x++)
			for(int y = shape[x][0]; y <= shape[x][1]; y++)
				try{
					if(!matrix[x][y].isVoid())
						bag.addCard(getColorAt(x, y));
				}catch(InvalidColorException ignored){}
		
		// Second round to fill the matrix
		for(int x = 0; x < BOARD_LENGTH; x++)
			for (int y = shape[x][0]; y <= shape[x][1]; y++)
				if (!bag.isEmpty()) matrix[x][y] = new Spot(bag.takeACard());
				else return;
	}
	
	/**
	 * This method verify if the game board is ready to be refilled
	 */
	public boolean isToRefill(){
		for(int c = 0; c < BOARD_LENGTH; c++)
			for(int r = 0; r < BOARD_LENGTH; r++){
				if(matrix[c][r].isVoid()) continue;
				for(Color s : adjacent(c, r).values())
					if(s != Color.V) return false;
			}
		
		return true;
	}
	
	/**
	 * @return true if a spot is free to be taken, false else. A void cell is considered always free.
	 */
	public boolean isFree(int x, int y){
		Collection<Color> adjacent = adjacent(x, y).values();
		return (matrix[x][y].isVoid() || adjacent.size() < 4 || adjacent.stream().anyMatch(c -> c == Color.V));
	}
	
	/**
	 * @return a string representing the cards bag
	 */
	public String getCardsBag(){
		return bag.toString();
	}
	
	public void resumeCardsBag(String str) throws InvalidActionException {
		String[] valuesStr = str.replaceAll("[\\[\\],]", "").split(" ");
		int[] values = Arrays.stream(valuesStr).mapToInt(Integer::parseInt).toArray();
		bag = new CardsBag(values);
	}
	
	/**
	 * @param groupSize is the size of the groups the method tries to find
	 * @return true if in the spotMatrix there is AT LEAST ONE group of AT LEAST groupSize adjacent tiles (NOT COLOR-SENSITIVE)
	 */
	public boolean thereAreGroupsOf(int groupSize){
		ArrayList<String> actualGroup;
		ArrayList<String> totalCounted = new ArrayList<>();
		
		for(int c = 0; c < BOARD_LENGTH; c++)
			for(int r = 0; r < BOARD_LENGTH; r++){
				if(matrix[c][r].isVoid()) continue;
				if(!totalCounted.contains(Arrays.toString(new int[]{c, r}))){
					actualGroup = new ArrayList<>();
					countGroup(actualGroup, c, r, false);
					if(actualGroup.size() >= groupSize) return true;
					totalCounted.addAll(actualGroup);
				}
			}
		return false;
	}
	
	/**
	 * Verify if a matrix of spots is a possible board according to the shape of board
	 *
	 * @return true if the matrix is, else false
	 */
	private boolean verifyMatrix(Spot[][] mat){
		// mat must have the same sizes of the board
		if(!verifyMatrix(mat, BOARD_LENGTH, BOARD_LENGTH)) return false;
		
		// Forall c, r (r<shape[c][0] or r>shape[c][1]) => mat[c][r].isVoid()
		for(int c = 0; c < BOARD_LENGTH; c++)
			for(int r = 0; r < BOARD_LENGTH; r++)
				if((r < shape[c][0] || r > shape[c][1]) && !mat[c][r].isVoid()) return false;
		
		return true;
	}
	
	static public int getBoardLength(){
		return BOARD_LENGTH;
	}
}

/**
 * This class is only used to decide what shape the board should have
 */
class PossibleShape{
	// PossibleShape in 2 players game
	private final static int[][] shape2 = {
		{0, -1}, {4, 5}, {3, 5}, {1, 6}, {1, 7}, {2, 7}, {3, 5}, {3, 4}, {0, -1}
	};
	
	// PossibleShape in 3 players game
	private final static int[][] shape3 = {
		{5, 5}, {4, 5}, {2, 6}, {0, 6}, {1, 7}, {2, 8}, {2, 6}, {3, 4}, {3, 3}
	};
	
	// PossibleShape in 4 players game
	private final static int[][] shape4 = {
		{4, 5}, {3, 5}, {2, 6}, {0, 7}, {0, 8}, {1, 8}, {2, 6}, {3, 5}, {3, 4}
	};
	
	/**
	 * There are 3 possible shapes of game board, depending on whether there are 2, 3 or 4 players.
	 * The matrix indicates, for each column, where it starts and where it ends (included)
	 */
	static int[][] decideShape(int nPlayers){
		if(nPlayers == 2) return shape2;
		if(nPlayers == 3) return shape3;
		return shape4;
	}
}