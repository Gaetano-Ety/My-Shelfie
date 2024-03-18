package model.goals;

import model.exceptions.InvalidStringExcepton;
import model.exceptions.NotAvailableGoalException;
import model.gameObjects.BookShelf;
import model.gameObjects.SpotMatrix;

import java.util.ArrayList;

public class PersonalGoal{
	/**
	 * The personal goal is represented like a bookshelf with only the spots of the goal colored
	 */
	private final String effectiveGoal;
	private final int goalID;
	public final static int nOfPersonalGoals = 12;
	
	/**
	 * Generate the goal string randomly using a factory pattern
	 */
	public PersonalGoal() throws NotAvailableGoalException {
		effectiveGoal = PossiblePersonalGoal.choose();
		try{
			goalID = PossiblePersonalGoal.find(effectiveGoal);
		}catch(InvalidStringExcepton e){
			throw new RuntimeException();
		}
	}
	
	public PersonalGoal(int n) throws NotAvailableGoalException{
		effectiveGoal = PossiblePersonalGoal.choose(n);
		goalID = n;
	}
	
	public int scoring(BookShelf bookShelf){
		int count = 0;
		String myMatrixString = bookShelf.toString();
		
		for(int i = 0; i < myMatrixString.length(); i++){
			char c = effectiveGoal.charAt(i);
			if(c != '/' && c != ' ')
				if(c == myMatrixString.charAt(i)) count++;
		}
		
		return switch(count){
			case 1 -> 1;
			case 2 -> 2;
			case 3 -> 4;
			case 4 -> 6;
			case 5 -> 9;
			case 6 -> 12;
			default -> 0;
		};
	}
	
	@Override
	public String toString(){return effectiveGoal;}
	
	public int getGoalID(){
		return goalID;
	}
	
	/**
	 * @return a string representing the particular personal goal
	 */
	public static String choose(int n) throws NotAvailableGoalException{
		return PossiblePersonalGoal.choose(n);
	}
	
	/**
	 * Reset the possible choose of personal goal, as if none had ever been chosen
	 */
	public static void resetPossiblePersonalGoal(){
		PossiblePersonalGoal.reset();
	}
}

/**
 * This class contains all the possible pattern of personal goals and a method that choose one of them random
 */
class PossiblePersonalGoal{
	private static final ArrayList<Integer> chosen = new ArrayList<>();
	
	/**
	 * Array of strings representing the possible personal goal
	 */
	private final static String[] goals = {
		"P     /   G  /F    T/  B   / C    /",
		"  C   / P    /  G   /    T /   B F/",
		" F   B/   C  /  P   / G    /   T  /",
		"  T   /    B /  F C /   P  /G     /",
		"     G/ T F  /   B  /     C/    P /",
		"     P/    G /T     /  B F /C     /",
		"C  T  /  P   /     B/ F    /    G /",
		"   P  / C    /  T   /    BG/F     /",
		"     F/    T /G C   /      /   BP /",
		"  B   / G  F /      /   C P/T     /",
		"  G   / B    /P  F  /     T/    C /",
		"     C/ P    /B F   /   T  /    G /"
	};
	
	/**
	 * @return one of the possible goal
	 */
	public static String choose() throws NotAvailableGoalException{
		if(chosen.size() == PersonalGoal.nOfPersonalGoals) throw new NotAvailableGoalException();
		
		int n = (int) (Math.random() * PersonalGoal.nOfPersonalGoals);
		while(chosen.contains(n))
			n = (n + 1) % PersonalGoal.nOfPersonalGoals;
		
		chosen.add(n);
		return goals[n];
	}
	
	/**
	 * @return a specific personal goal
	 */
	static String choose(int n) throws NotAvailableGoalException{
		if(n < 0 || n >= PersonalGoal.nOfPersonalGoals) throw new NotAvailableGoalException();
		return goals[n];
	}
	
	/**
	 * @return the id of a goal given the goal itself
	 */
	static int find(String str) throws InvalidStringExcepton{
		int[] dim = SpotMatrix.verifyString(str);
		if(dim[0] != BookShelf.getNumCols() || dim[1] != BookShelf.getNumRows()) throw new InvalidStringExcepton();
		
		for(int i = 0; i < PersonalGoal.nOfPersonalGoals; i++)
			if(goals[i].equals(str)) return i;
		throw new InvalidStringExcepton();
	}
	
	/**
	 * For Tests<br/>
	 * Reset the chosen goal array, so consecutive tests can choose more than 12 goals if necessary
	 */
	static void reset(){
		chosen.clear();
	}
}
