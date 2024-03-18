package serverTests.controllerTests;

import model.exceptions.InvalidStringExcepton;
import model.goals.PersonalGoal;
import modelTests.GameExampleJSON;
import networkMessages.messages.NickNameMessage;
import networkMessages.messages.NumOfPlayersMessage;
import org.json.JSONObject;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.opentest4j.AssertionFailedError;
import server.controller.ServerController;
import server.controller.ServerLauncher;
import server.controller.network.ServerInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

public class NetworkTests{
	private ServerController controller;
	private final ServerInterface server = ServerInterface.getServer();
	private Thread t_Server;
	
	@Nested
	class ConnectionTests{
		/**
		 * Testing if 1 player connects correctly
		 */
		@Test
		public void connectionTest1(){
			try{
				startServer();
				Socket s = new Socket("localhost", ServerInterface.port);
				
				BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
				
				assertEquals("IDMessage", new JSONObject(input.readLine()).getString("type"));
				
				closeServerTest();
			}catch(InterruptedException | IOException e){
				fail();
			}
		}
		
		/**
		 * Testing the case where more than required players try to connect
		 */
		@Test
		public void connectionTest2(){
			try{
				int n = 3;
				
				startServer();
				Socket[] sockets = new Socket[n];
				BufferedReader[] inputs = new BufferedReader[n];
				
				controller.resumeAGame(GameExampleJSON.ex5);
				
				for(int i = 0; i < n; i++){
					sockets[i] = new Socket("localhost", ServerInterface.port);
					inputs[i] = new BufferedReader(new InputStreamReader(sockets[i].getInputStream()));
				}
				
				assertEquals("IDMessage", new JSONObject(inputs[0].readLine()).getString("type"));
				assertEquals("IDMessage", new JSONObject(inputs[1].readLine()).getString("type"));
				
				assertEquals("WrongMessage", new JSONObject(inputs[2].readLine()).getString("type"));
				
				closeServerTest();
			}catch(InterruptedException | IOException | InvalidStringExcepton e){
				fail();
			}
		}
		
		/**
		 * Test in case a player crashes
		 */
		@Test
		public void connectionTest3(){
			try{
				ServerController controller = new ServerController();
				controller.setIsTestNetwork();
				controller.resumeAGame(GameExampleJSON.ex4);
				Thread serverThread = new Thread(() -> ServerLauncher.start(controller), "ServerThread");
				serverThread.start();
				
				sleep(150);
				
				for(int i = 0, n = 2; i < n; i++)
					new Thread(this::runDummyClient, "Client-" + i).start();
				
				sleep(100);
				
				controller.removePlayerManager(0);
				
				sleep(100);
				
				assertTrue(ServerInterface.getServer().isActive());
				serverThread.interrupt();
				ServerInterface.getServer().closeConnection();
			}catch(InvalidStringExcepton | IOException | InterruptedException e){
				fail(e);
			}
		}
		
		private void runDummyClient(){
			try(Socket s = new Socket("localhost", ServerInterface.port)){
				BufferedReader inputs = new BufferedReader(new InputStreamReader(s.getInputStream()));
				while(true) System.out.println(inputs.readLine());
			}catch(IOException e){
				Thread.currentThread().interrupt();
			}
		}
	}
	
	@Nested
	class PlayerManagerTests{
		private static boolean ok;
		private static int notCompleted;
		private static final Object lock = new Object();
		
		/**
		 * Test if all the initial phases of game are performed correctly
		 */
		@ParameterizedTest
		@ValueSource(ints = {2, 3, 4})
		public void allWorksTest(int n){
			ok = true;
			notCompleted = n;
			
			try{
				startServer();
				ArrayList<Thread> ths = new ArrayList<>();
				
				for(int x = 0; x < n; x++){
					ths.add(new Thread(() -> this.connect1Client(n), "Client-" + x));
					ths.get(x).start();
				}
				
				for(int x = 0; x < n; x++) ths.get(x).join();
				
				closeServerTest();
				assertTrue(ok);
			}catch(InterruptedException | IOException e){
				fail();
			}finally{
				PersonalGoal.resetPossiblePersonalGoal();
			}
		}
		
		private void connect1Client(int n){
			try(Socket s = new Socket("localhost", ServerInterface.port)){
				BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
				PrintWriter output = new PrintWriter(s.getOutputStream(), true);
				
				JSONObject m1 = new JSONObject(input.readLine());
				JSONObject m2 = new JSONObject(input.readLine());
				
				JSONObject idData, nicksData;
				if(m1.getString("type").equals("IDMessage")) idData = m1;
				else if(m2.getString("type").equals("IDMessage")) idData = m2;
				else throw new RuntimeException();
				
				int ID = idData.getInt("ID");
				System.out.println("Client-" + ID + ": id received");
				
				if(m1.getString("type").equals("NickNamesMessage")) nicksData = m1;
				else if(m2.getString("type").equals("NickNamesMessage")) nicksData = m2;
				else throw new RuntimeException();
				
				System.out.println("Client-" + ID + ": nicknames received");
				
				// The server expects the number of players and nickname
				if(ID == 0){
					output.println(new NumOfPlayersMessage(n).toJSON());
					assertEquals("OkMessage", new JSONObject(input.readLine()).getString("type"));
				}
				output.println(new NickNameMessage("Player" + ID, ID).toJSON());
				
				// It expects, in some order: FirstInfoMessage, NickNamesMessage, GameDataMessage and an OkMessage for the nickname
				String[] ms = new String[4];
				ms[0] = new JSONObject(input.readLine()).getString("type");
				ms[1] = new JSONObject(input.readLine()).getString("type");
				ms[2] = new JSONObject(input.readLine()).getString("type");
				ms[3] = new JSONObject(input.readLine()).getString("type");
				
				assertTrue(Arrays.asList(ms).contains("FirstInfoMessage"));
				assertTrue(Arrays.asList(ms).contains("OkMessage"));
				assertTrue(Arrays.asList(ms).contains("NickNamesMessage"));
				assertTrue(Arrays.asList(ms).contains("GameDataMessage"));
				
				synchronized(PlayerManagerTests.lock){
					if(notCompleted <= 1){
						lock.notifyAll();
						Thread.currentThread().interrupt();
					}else{
						notCompleted--;
						lock.wait();
					}
				}
			}catch(IOException | AssertionFailedError | RuntimeException | InterruptedException e){
				System.out.println("Exception received: " + e.getClass().getSimpleName() + e.getMessage());
				ok = false;
			}
		}
	}
	
	/**
	 * Start the server and wait a bit
	 */
	private void startServer() throws InterruptedException{
		controller = new ServerController();
		controller.setIsTestNetwork();
		t_Server = new Thread(() -> server.launch(controller, true), "Server");
		t_Server.start();
		sleep(100);
	}
	
	/**
	 * Close the connection and the thread of the server
	 */
	private void closeServerTest() throws IOException{
		server.closeConnection();
		t_Server.interrupt();
	}
}
