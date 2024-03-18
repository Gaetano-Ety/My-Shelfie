package clientTests.controllerTests.networkTests;

import client.controller.ClientController;
import client.view.cli.CliMethods;
import model.exceptions.InvalidStringExcepton;
import model.gameObjects.SpotMatrix;
import networkMessages.messages.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import server.controller.network.ServerInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Stack;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ClientInterfaceTest{
	ClientController cc;
	
	@Test
	public void test1(){
		try{
			cc = new ClientController(0, true, "localhost");
			cc.forceKeyboardBufferedReader("mario\n4\nmario\ngetTiles\n1\n0\n3 0\n\n");
			Thread ts = new Thread(this::dummyServer);
			ts.start();
			sleep(100);
			
			cc.startController();
			
			ts.join();
			cc.stopGame(0);
		}catch(InterruptedException e){
			fail(e);
		}
	}
	
	public void dummyServer(){
		try(ServerSocket ss = new ServerSocket(ServerInterface.port)){
			Socket socket = ss.accept();
			
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
			
			IDMessage idMessage = new IDMessage(0);
			NickNamesMessage nickNamesMessage = new NickNamesMessage(new String[]{});
			output.println(idMessage.toJSON());
			output.println(nickNamesMessage.toJSON());
			
			JSONObject nickname = readNotPing(input);
			System.out.println("Server received: " + nickname);
			assertEquals(NickNameMessage.class.getSimpleName(), nickname.getString("type"));
			
			JSONObject numOfPlayers = readNotPing(input);
			System.out.println("Server received: " + numOfPlayers);
			assertEquals(NumOfPlayersMessage.class.getSimpleName(), numOfPlayers.getString("type"));

			WrongMessage wrongName = new WrongMessage("InvalidPlayerNickName");
			OkMessage okMessage = new OkMessage();
			output.println(wrongName.toJSON());
			output.println(okMessage.toJSON());
			output.println(okMessage.toJSON());

			String[] nicks1 = new String[]{"mario", "gaia", "giacomo", "banana"};
			NickNamesMessage nickNamesMessage1 = new NickNamesMessage(nicks1);
			output.println(nickNamesMessage1.toJSON());

			FirstInfoMessage firstInfoMessage = new FirstInfoMessage(9, 12, 9, 4);
			output.println(firstInfoMessage.toJSON());
			Stack[] stacks = createStack();
			String[] bookshelves = new String[]{"      /      /      /      /      /", "      /      /      /      /      /", "      /      /      /      /      /", "      /      /      /      /      /"};
			GameDataMessage gameDataMessage = new GameDataMessage("    CG   /   FBP   /  CFPCC  /GBCCGFBT /GPBCGTFBF/ PTPCFPCT/  GGBPG  /   TPP   /   BC    /", bookshelves, 0, stacks);
			CliMethods.printMat(new SpotMatrix("    CG   /   FBP   /  CFPCC  /GBCCGFBT /GPBCGTFBF/ PTPCFPCT/  GGBPG  /   TPP   /   BC    /"));
			output.println(gameDataMessage.toJSON());

			JSONObject newNick = readNotPing(input);
			System.out.println("Server received: " + newNick);
			assertEquals(NickNameMessage.class.getSimpleName(), newNick.getString("type"));

			JSONObject update = readNotPing(input);
			System.out.println("Server received: " + update);
			assertEquals(UpdateMessage.class.getSimpleName(), update.getString("type"));
			
			int[] scores = {1, 23, 34, 0};
			FinalMessage finalMessage = new FinalMessage(scores);
			output.println(finalMessage.toJSON());
			
			socket.close();
		}catch(IOException | InvalidStringExcepton e){
			throw new RuntimeException(e);
		}
	}
	
	private JSONObject readNotPing(BufferedReader input) throws IOException{
		JSONObject obj;
		do{
			obj = new JSONObject(input.readLine());
		}while(Objects.equals(obj.getString("type"), "PingMessage"));
		return obj;
	}
	
	private Stack[] createStack(){
		Stack<Integer> stack1 = new Stack<>();
		stack1.add(2);
		stack1.add(4);
		stack1.add(6);
		stack1.add(8);
		Stack[] stacks = new Stack[2];
		stacks[0] = stack1;
		stacks[1] = stack1;
		return stacks;
	}
}