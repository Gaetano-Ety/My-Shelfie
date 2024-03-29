package model.goals;

import model.exceptions.NotAvailableGoalException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Stack;
import java.util.stream.IntStream;

public class CommonGoalFactory{
	/**
	 * alreadyPicked keeps trace of the common goal already created, if there is one
	 */
	private static int alreadyPicked = 0;
	
	/**
	 * Randomly creates a common goal, makes sure that it wasn't created before, confronting with static int alreadyPicked
	 *
	 * @return a common goal created randomly
	 */
	public static CommonGoal createCommonGoal(int nPlayers){
		int x = (int) (Math.random() * 12) + 1;
		if(x == alreadyPicked) x = (x + 1) % 12 + 1;
		alreadyPicked = x;
		
		try{
			return getCommonGoal(x, nPlayers);
		}catch(NotAvailableGoalException ignored){
			return null; // Impossible
		}
	}
	
	/**
	 * @param n indicates the id of goal that you want to generate
	 * @return a specific common goal
	 */
	public static CommonGoal createCommonGoal(int nPlayers, int n) throws NotAvailableGoalException{
		return getCommonGoal(n, nPlayers);
	}
	
	/**
	 * @param obj - Is the JSON representation of a common goal, obtained by toString() on a common goal.
	 * @return a specific common goal generated by the input.
	 */
	public static CommonGoal createCommonGoal(JSONObject obj) throws NotAvailableGoalException{
		int id = obj.getInt("ID");
		
		// Extract the stack from the JSON object
		Stack<Integer> stack = new Stack<>();
		JSONArray stackJSON = obj.getJSONArray("scores");
		IntStream.range(0, stackJSON.length()).map(stackJSON::getInt).forEach(stack::add);
		
		CommonGoal r = createCommonGoal(0, id);
		r.setStack(stack);
		
		return r;
	}
	
	@NotNull
	private static CommonGoal getCommonGoal(int n, int nPlayers) throws NotAvailableGoalException{
		CommonGoal r = switch(n){
			case 1 -> new CommonGoal1();
			case 2 -> new CommonGoal2();
			case 3 -> new CommonGoal3();
			case 4 -> new CommonGoal4();
			case 5 -> new CommonGoal5();
			case 6 -> new CommonGoal6();
			case 7 -> new CommonGoal7();
			case 8 -> new CommonGoal8();
			case 9 -> new CommonGoal9();
			case 10 -> new CommonGoal10();
			case 11 -> new CommonGoal11();
			case 12 -> new CommonGoal12();
			default -> throw new NotAvailableGoalException();
		};
		
		r.setStack(StackFactory.makeStack(nPlayers));
		return r;
	}
	
	/**
	 * Return a description of the goal by the id
	 */
	public static String getGoalDescription(int goalID) throws NotAvailableGoalException{
		return switch(goalID){
			case 1 -> "- Six groups each containing at least 2 tiles of the same type\n" +
					"- The tiles of one group can be different from those of another group.";
			case 2 -> "- Four groups each containing at least 4 tiles of the same type.\n" +
					"- The tiles of one group can be different from those of another group.";
			case 3 -> "- Four tiles of the same type in the four corners of the bookshelf.";
			case 4 -> """
					- Two groups each containing 4 tiles of the same type in a 2x2 square.
					- The 2 square must not have tiles in common and.
					- The tiles of one square can be different from those of the other square.""";
			case 5 -> "- Three columns each formed by 6 tiles of maximum three different types.";
			case 6 -> "- You must have 8 tiles of the same type in your bookshelf.";
			case 7 -> "- Five tiles of the same type forming a diagonal.";
			case 8 -> "- Four lines each formed by 5 tiles of maximum three different types.";
			case 9 -> "- Two columns each formed by 6 different types of tiles.";
			case 10 -> "- Two rows each formed by 5 different types of tiles.";
			case 11 -> "- Five tiles of the same type forming an X";
			case 12 -> "- Five columns of increasing or decreasing height by exactly 1 step at time.\n" +
					"- There must be no empty columns.";
			default -> throw new NotAvailableGoalException();
		};
	}
	
	/**
	 * Reset the alreadyPicked goal, for a new game
	 */
	public static void resetCommonGoal(){
		alreadyPicked = 0;
	}
	
	public static class StackFactory{
		public static Stack<Integer> makeStack(int n){
			
			Stack<Integer> stack = new Stack<>();
			switch(n){
				case 2 -> {
					stack.add(4);
					stack.add(8);
				}
				case 3 -> {
					stack.add(4);
					stack.add(6);
					stack.add(8);
				}
				case 4 -> {
					stack.add(2);
					stack.add(4);
					stack.add(6);
					stack.add(8);
				}
			}
			return stack;
		}
	}
}