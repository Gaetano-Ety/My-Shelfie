package clientTests.controllerTests;

import client.controller.ClientController;
import client.exceptions.InvalidTilesException;
import client.view.cli.CliMethods;
import client.virtualModel.GameData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientControllerTest{
	ClientController cc1 = new ClientController(0, true, "localhost");
	GameData gd1 = cc1.getGameData();
	
	@Test
	public void createNickNameSimpleTest(){
		cc1.forceKeyboardBufferedReader("nick");
		cc1.createNicknameMessage();
		assertEquals("nick", cc1.getGameData().getMyNickName());
	}
	
	@Test
	public void createNickNameInvalidNicknameTest(){
		String[] otherNicks = new String[4];
		otherNicks[0] = "nick";
		otherNicks[1] = "fran";
		cc1.getGameData().setNickNames(otherNicks);
		assertEquals("nick", gd1.getNickName(0));
		assertEquals("fran", gd1.getNickName(1));
		cc1.forceKeyboardBufferedReader("nick\nnick\njake");
		cc1.createNicknameMessage();
		assertEquals("jake", cc1.getGameData().getMyNickName());
	}
	
	@Test
	public void createNumOfPlayersTest(){
		cc1.forceKeyboardBufferedReader("4");
		cc1.createNumOfPlayersMessage();
		assertEquals(4, gd1.getNPlayers());
	}
	
	@Nested
	class updateMessageTests{
		@Test
		public void simpleCreateUpdateMessageTest(){
			try{
				cc1.getGameData().setNumOfPlayer(4);

				cc1.getGameData().setID(0);
				String sp = "    CG   /   FBP   /  CFPCC  /GBCCGFBT /GPBCGTFBF/ PTPCFPCT/  GGBPG  /   TPP   /   BC    /";

				cc1.getGameData().updateBoard(sp);
				CliMethods.printMat(cc1.getGameData().getGameBoard());
				CliMethods.printMat(cc1.getGameData().getBookShelf(0));
				
				cc1.forceKeyboardBufferedReader("3\n4\n4 8\n5 8\n5 7");
				cc1.createUpdateMessage();
				CliMethods.printMat(gd1.getGameBoard());

			}catch(Exception e){
				fail(e);
			}
		}

		//Tests what happens when the player tries to take more tiles than possible
		@Test
		public void createUpdateMessageFewTilesTest(){
			try{
				cc1.getGameData().setNumOfPlayer(4);

				cc1.getGameData().setID(0);
				String sp = "    CG   /         /         /         /         /         /         /         /         /";

				cc1.getGameData().updateBoard(sp);
				CliMethods.printMat(cc1.getGameData().getGameBoard());
				CliMethods.printMat(cc1.getGameData().getBookShelf(0));
				
				cc1.forceKeyboardBufferedReader("3\n1\n0 5\n0 6\n2\n1\n0 5\n0 4");
				assertThrows(InvalidTilesException.class, () -> cc1.createUpdateMessage());
				CliMethods.printMat(gd1.getGameBoard());
			}catch(Exception e){
				fail(e);
			}
		}

		//Tests what happens when the player tries to take more tiles than he can put in the column
		@Test
		public void createUpdateMessageTooManyInColTest(){
			try{
				cc1.getGameData().setNumOfPlayer(4);

				cc1.getGameData().setID(0);
				String sp ="    CG   /   FBP   /  CFPCC  /GBCCGFBT /GPBCGTFBF/ PTPCFPCT/  GGBPG  /   TPP   /   BC    /";
				String bs =" CCCCC/      /      /      /      /";

				cc1.getGameData().updateBookShelves(new String[]{bs, bs, bs, bs});
				cc1.getGameData().updateBoard(sp);
				CliMethods.printMat(cc1.getGameData().getGameBoard());
				CliMethods.printMat(cc1.getGameData().getBookShelf(0));
				
				cc1.forceKeyboardBufferedReader("3\n0\n1\n0\n3 0");
				assertThrows(InvalidTilesException.class, () -> cc1.createUpdateMessage());
				CliMethods.printMat(gd1.getGameBoard());
			}catch(Exception e){
				fail(e);
			}
		}

		@Test
		public void createUpdateMessageNotValidTilesFromBoardTest(){
			try{
				cc1.getGameData().setNumOfPlayer(4);

				cc1.getGameData().setID(0);
				String sp ="    CG   /   FBP   /  CFPCC  /GBCCGFBT /GPBCGTFBF/ PTPCFPCT/  GGBPG  /   TPP   /   BC    /";
				String bs ="      /      /      /      /      /";

				cc1.getGameData().updateBookShelves(new String[]{bs, bs, bs, bs});
				cc1.getGameData().updateBoard(sp);
				CliMethods.printMat(cc1.getGameData().getGameBoard());
				CliMethods.printMat(cc1.getGameData().getBookShelf(0));
				
				cc1.forceKeyboardBufferedReader("2\n0\n3 1\n5 1\n8 3\n8 4");
				cc1.createUpdateMessage();
				CliMethods.printMat(gd1.getGameBoard());

			}catch(Exception e){
				fail(e);
			}
		}
	}
}