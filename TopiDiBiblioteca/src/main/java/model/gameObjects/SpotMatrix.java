package model.gameObjects;

import model.Color;
import model.exceptions.InvalidColorException;
import model.exceptions.InvalidStringExcepton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SpotMatrix implements Cloneable{
	Spot[][] matrix;
	private final int nCol, nRow;
	
	/**
	 * Constructor for the all-void spots
	 */
	public SpotMatrix(int nCol, int nRow){
		this.nCol = nCol;
		this.nRow = nRow;
		matrix = new Spot[nCol][nRow];
		
		// Initialize to VOID (0) the board spots values
		for(int x = 0; x < nCol; x++)
			for(int y = 0; y < nRow; y++)
				matrix[x][y] = new Spot(Color.VOID);
	}
	
	/**
	 * Constructor that create the spots matrix from a single string <br/>
	 * The way the string is to be made is described by the method verifyString
	 */
	public SpotMatrix(String string) throws InvalidStringExcepton{
		int[] dim = verifyString(string);
		nRow = dim[1];
		nCol = dim[0];
		
		matrix = new Spot[nCol][nRow];
		// The InvalidColorException will be ignored because verifyString makes impossible for this to happen
		try{
			for(int x = 0; x < nCol; x++){
				int c = x * (nRow + 1);
				for(int y = 0; y < nRow; y++)
					matrix[x][y] = new Spot(Color.colorLike(String.valueOf(string.charAt(c + y))));
			}
		}catch(InvalidColorException ignored){/* Impossible */}
	}
	
	/**
	 * @return the number of columns
	 */
	public int nColumns(){
		return nCol;
	}
	
	/**
	 * @return the number of rows
	 */
	public int nRows(){
		return nRow;
	}
	
	/**
	 * Make void the linked spot
	 */
	public void remove(int x, int y){
		matrix[x][y] = new Spot(Color.VOID);
	}
	
	/**
	 * @return a hashmap with the colors of adjacent spots of the spot linked in input.
	 * The hashmap will have at most 4 keys: "up", "right", "down", "left"
	 */
	public HashMap<String, Color> adjacent(int x, int y){
		HashMap<String, Color> result = new HashMap<>();
		if(isValidIndex(x, y - 1)) result.put("up", matrix[x][y - 1].getColor());
		if(isValidIndex(x + 1, y)) result.put("right", matrix[x + 1][y].getColor());
		if(isValidIndex(x, y + 1)) result.put("down", matrix[x][y + 1].getColor());
		if(isValidIndex(x - 1, y)) result.put("left", matrix[x - 1][y].getColor());
		
		return result;
	}
	
	/**
	 * Verify if an index of the matrix exist
	 */
	private boolean isValidIndex(int x, int y){
		return x >= 0 && y >= 0 && x < nCol && y < nRow;
	}
	
	/**
	 * @param x column of the spot
	 * @param y row of the spot
	 * @return a clone of the spot in that coordinates
	 */
	public Spot getSpotAt(int x, int y){
		return matrix[x][y].clone();
	}
	
	/**
	 * @param x column of the spot
	 * @param y row of the spot
	 * @return the color of a spot in the matrix
	 */
	public Color getColorAt(int x, int y){
		return matrix[x][y].getColor();
	}
	
	/**
	 * toString returns a string which is a one-to-one summary of the matrix.
	 * Columns will be lined up and separated by '/', while void spots will be evaluated as empty spaces.
	 * Each card has its own letter
	 */
	@Override
	public String toString(){
		if(nCol == 0 || nRow == 0) return "";
		StringBuilder out = new StringBuilder();
		String[][] strings = toMatStrings();
		
		for(int x = 0; x < nCol; x++){
			for(int y = 0; y < nRow; y++){
				assert strings != null;
				out.append(strings[x][y]);
			}
			out.append("/");
		}
		
		return out.toString();
	}
	
	/**
	 * @return a SpotMatrix equals to this
	 */
	@Override
	public SpotMatrix clone(){
		try{
			return new SpotMatrix(this.toString());
		}catch(InvalidStringExcepton ignore){
			return null; // But this is impossible
		}
	}
	
	/**
	 * Transform this SpotMatrix into a strings matrix
	 */
	public String[][] toMatStrings(){
		if(nCol == 0) return null;
		
		String[][] strings = new String[nCol][nRow];
		
		// In this implementation the void spots will be represented with empty spaces
		for(int x = 0; x < nCol; x++)
			for(int y = 0; y < nRow; y++){
				strings[x][y] = String.valueOf(matrix[x][y].getColor().toString().charAt(0));
				if(strings[x][y].equals("V")) strings[x][y] = " ";
			}
		
		return strings;
	}
	
	/**
	 * This private method is functional for counting the cards that form a group of adjacent cards <br/>
	 * It is recursive, each time it adds to the "group" arrayList the coordinates (as a string) of the tiles belonging to the group. <br/>
	 * As soon as all the cards belonging to the group have been seen, it will do nothing and leave the arrayList modified with the coordinates of all the tiles in the group.
	 *
	 * @param sameColor indicate if the group must be formed by tiles of the same color or not
	 * @param group     has the coordinates of the tiles in the group until now
	 * @param x,y       are the coordinates of the tile
	 */
	void countGroup(ArrayList<String> group, int x, int y, boolean sameColor){
		if(matrix[x][y].isVoid()) return;
		
		group.add(Arrays.toString(new int[]{x, y}));
		HashMap<String, Color> adjacency = adjacent(x, y);
		
		if(!Objects.isNull(adjacency.get("up")))
			if(getColorAt(x, y) == adjacency.get("up") || !sameColor)
				if(!group.contains(Arrays.toString(new int[]{x, y - 1})))
					countGroup(group, x, y - 1, sameColor);
		
		if(!Objects.isNull(adjacency.get("right")))
			if(getColorAt(x, y) == adjacency.get("right") || !sameColor)
				if(!group.contains(Arrays.toString(new int[]{x + 1, y})))
					countGroup(group, x + 1, y, sameColor);
		
		if(!Objects.isNull(adjacency.get("down")))
			if(getColorAt(x, y) == adjacency.get("down") || !sameColor)
				if(!group.contains(Arrays.toString(new int[]{x, y + 1})))
					countGroup(group, x, y + 1, sameColor);
		
		if(!Objects.isNull(adjacency.get("left")))
			if(getColorAt(x, y) == adjacency.get("left") || !sameColor)
				if(!group.contains(Arrays.toString(new int[]{x - 1, y})))
					countGroup(group, x - 1, y, sameColor);
	}
	
	/**
	 * @return a clone of matrix
	 */
	Spot[][] getMatrix(){
		Spot[][] newMatrix = new Spot[nCol][nRow];
		
		for(int x = 0; x < nCol; x++)
			for(int y = 0; y < nRow; y++)
				newMatrix[x][y] = matrix[x][y].clone();
		
		return newMatrix;
	}
	
	/**
	 * Compare two SpotMatrix;
	 *
	 * @return true if they are equal
	 */
	public static boolean compare(SpotMatrix mat1, SpotMatrix mat2){
		int sizeX = mat1.nColumns();
		int sizeY = mat1.nRows();
		
		if(sizeX != mat2.nColumns()) return false;
		if(sizeY != mat2.nRows()) return false;
		
		String[][] s1 = mat1.toMatStrings();
		String[][] s2 = mat2.toMatStrings();
		
		for(int x = 0; x < sizeX; x++)
			for(int y = 0; y < sizeY; y++)
				if(!s1[x][y].equals(s2[x][y])) return false;
		
		return true;
	}
	
	/**
	 * @return the equivalent to (new SpotMatrix(str)).toString()
	 */
	public static String uniformString(String str) throws InvalidStringExcepton{
		verifyString(str);
		return str.toUpperCase().replace("V", " ");
	}
	
	/**
	 * This method use the regular expression to verify if a string is the representation of a spot matrix.
	 * The representation of matrix is of the form "XYZ.../ABC.../..." where the letter represents colors (void is ' ' or 'V') and '/' means the and of the column.
	 *
	 * @return the sizes of the matrix
	 */
	public static int[] verifyString(String string) throws InvalidStringExcepton{
		int[] result = {0, 0};
		if(string.isEmpty()) return result;
		
		result[0] = (int) string.chars().filter(c -> c == '/').count();
		result[1] = string.length() / result[0] - 1;
		
		Pattern pattern = Pattern.compile("([CBGPFTVcbgpftv ]{" + result[1] + "}/){" + result[0] + "}");
		Matcher matcher = pattern.matcher(string);
		
		if(matcher.find()) return result;
		else throw new InvalidStringExcepton();
	}
	
	/**
	 * Check if a set of coordinates representing a set of adjacent coordinates
	 */
	public static boolean areAdjacentSpots(int[][] coo){
		if(coo.length <= 1) return true;
		
		ArrayList<int[]> clone, validated;
		clone = Arrays.stream(coo).collect(Collectors.toCollection(ArrayList::new));
		validated = new ArrayList<>();
		
		validated.add(clone.remove(0));
		
		for(int j = 0; j < validated.size(); j++)
			for(int i = 0; i < clone.size(); i++)
				if(areAdjacent(validated.get(j), clone.get(i))){
					validated.add(clone.remove(i));
					i--;
				}
		
		return clone.isEmpty();
	}
	
	/**
	 * Check if a couple of coordinate is adjacent each other
	 */
	public static boolean areAdjacent(int[] c1, int[] c2){
		if(c1[0] == c2[0]) return c1[1] == c2[1] + 1 || c1[1] == c2[1] - 1;
		if(c1[1] == c2[1]) return c1[0] == c2[0] + 1 || c1[0] == c2[0] - 1;
		return false;
	}
	
	/**
	 * @return number of non-void spots in a spotMatrix
	 */
	public static int countNotVoidElement(SpotMatrix myMat){
		return Arrays.stream(myMat.getMatrix())
			.map(Arrays::stream)
			.map(streamSpot -> (int) streamSpot
				.filter(spot -> !spot.isVoid())
				.count())
			.reduce(0, Integer::sum);
	}
	
	/**
	 * Check that the input matrix is of the required size, and it hasn't null element
	 *
	 * @return true if the matrix is, else false
	 */
	static boolean verifyMatrix(Object[][] mat, int nCol, int nRow){
		if(Objects.isNull(mat)) return false;
		if(nCol != mat.length) return false;
		for(Object[] objs : mat){
			if(Objects.isNull(objs)) return false;
			if(objs.length != nRow) return false;
			for(Object o : objs)
				if(Objects.isNull(o)) return false;
		}
		return true;
	}
}