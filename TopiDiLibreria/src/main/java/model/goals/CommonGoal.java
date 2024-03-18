package model.goals;

import model.gameObjects.BookShelf;

import java.util.Stack;

/**
 * CommonGoal manages the creation of 2 different common goals,
 * makes possible to call the right method to verify id the goal has been achieved
 * and also keeps trace of the number of players that achieved the goal (and the remaining)
 */
public abstract class CommonGoal{
	/**
	 * scores is a stack of ints, that depends on the number of players
	 */
	private Stack<Integer> scores;
	
	/**
	 * Assigns a stack a copy of another stack
	 */
	public void setStack(Stack<Integer> stack){
		scores = new Stack<>();
		scores.addAll(stack);
	}
	
	/**
	 * @return highest available score, from the top of the stack
	 */
	public int achievedGoal(){
		return scores.pop();
	}
	
	public Stack<Integer> getStack(){
		Stack<Integer> out = new Stack<>();
		out.addAll(scores);
		return out;
	}
	
	/**
	 * @return a string with goal ID and a string representing the stack of goal
	 */
	@Override
	public String toString(){
		return "{\"ID\":" + getID() + ",\"scores\":" + scores.toString() + "}";
	}
	
	/**
	 * @param bookShelf is the bookshelf that has to be controlled
	 * @return is the goal was achieved
	 */
	public abstract boolean verifyCommonGoal(BookShelf bookShelf);
	
	public abstract int getID();
}