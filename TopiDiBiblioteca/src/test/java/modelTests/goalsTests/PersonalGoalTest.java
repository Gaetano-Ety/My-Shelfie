package modelTests.goalsTests;

import client.view.cli.CliMethods;
import model.exceptions.InvalidMatrixException;
import model.exceptions.InvalidStringExcepton;
import model.exceptions.NotAvailableGoalException;
import model.gameObjects.BookShelf;
import model.gameObjects.SpotMatrix;
import model.goals.PersonalGoal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class PersonalGoalTest{
	PersonalGoal goal;
	SpotMatrix spotMatrix;
	
	/**
	 * This is a test print only; the verification of correctness must be done by eye.
	 */
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12})
	public void printTest(int n){
		if(n == 12){
			PersonalGoal.resetPossiblePersonalGoal();
			return;
		}
		try{
			goal = new PersonalGoal(n);
			String goalStr = goal.toString();
			spotMatrix = new SpotMatrix(goalStr);

			System.out.println("Goals n. " + (n + 1) + ":");
			CliMethods.printMat(spotMatrix);
		}catch(InvalidStringExcepton | NotAvailableGoalException e){
			System.out.println("Exception recived: " + e.getClass());
			fail();
		}
	}
	
	/**
	 * This test serves to ensure that all personal goals generated are different from each other
	 */
	@Test
	public void differenceTest(){
		int count = 0;
		String goalStr = "";
		try{
			for(int c = 0; c < PersonalGoal.nOfPersonalGoals; c++){
				String str = new PersonalGoal().toString();
				if(!Objects.equals(str, goalStr))
					goalStr = str;
				else fail();
				System.out.println((c + 1) + ": " + str);
				count++;
			}
		}catch(NotAvailableGoalException e){
			System.out.println("generated  " + count + " goals before failing");
			fail();
		}
		PersonalGoal.resetPossiblePersonalGoal();
	}
	
	/**
	 * Test on the personal goal NotAvailableGoalException
	 */
	@Test
	public void exceptionTest(){
		assertThrows(
			NotAvailableGoalException.class,
			() -> {
				for(int c = 0; c <= PersonalGoal.nOfPersonalGoals; c++)
					new PersonalGoal();
			}
		);
		PersonalGoal.resetPossiblePersonalGoal();
	}
	
	@ParameterizedTest
	@MethodSource("scoresTestArguments")
	public void scoresTest(int ngoal, String bShelfie, int scoreExpected){
		if(ngoal == -1){
			PersonalGoal.resetPossiblePersonalGoal();
			return;
		}
		
		bShelfie = bShelfie.replace(" ", "C");
		
		try{
			BookShelf bs = new BookShelf();
			bs.copyMatrix(new SpotMatrix(bShelfie));
			
			goal = new PersonalGoal(ngoal);
			assertEquals(scoreExpected, goal.scoring(bs));
		}catch(NotAvailableGoalException | InvalidStringExcepton | InvalidMatrixException e){
			fail();
		}
	}
	
	static Stream<Arguments> scoresTestArguments(){
		return Stream.of(
			Arguments.of(0, "      /      /      /      / C    /", 1),
			Arguments.of(1, "  C   / P    /      /      /      /",  2),
			Arguments.of(2, " F   B/   C  /      /      /      /", 4),
			Arguments.of(3, "  T   /    B /  F C /   P  /G     /", 12),
			Arguments.of(4, "     G/ T F  /   B  /     C/    P /", 12),
			Arguments.of(5, "     P/    G /T     /  B F /C     /", 12),
			Arguments.of(6, "CTTTCC/PTPTPP/TTPBBB/GGPPGB/CGGGGC/", 9),
			Arguments.of(6, "C  T  /  P   /      / F    /      /", 6),
			Arguments.of(7, "   P  / C    /  T   /    BG/F     /", 12),
			Arguments.of(8, "     F/    T /G C   /      /   BP /", 12),
			Arguments.of(9, "  B   / G  F /      /   C P/T     /", 12),
			Arguments.of(10, "  G   / B    /P  F  /     T/    C /", 12),
			Arguments.of(11, "     C/ P    /B F   /   T  /    G /", 12),
			Arguments.of(-1, "", 0)
		);
	}
}
