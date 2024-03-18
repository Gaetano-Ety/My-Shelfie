package server.controller.network;

import networkMessages.exceptions.InvalidMessageException;
import networkMessages.messages.OkMessage;
import networkMessages.messages.WrongMessage;
import org.json.JSONObject;
import server.controller.ServerController;
import server.interfaces.Observer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class PlayerManager extends Thread implements Observer{
	private final ServerController controller;
	private final BufferedReader input;
	private final PrintWriter output;
	private boolean firstTurn = true;
	private int playerID;
	private boolean ping;
	/**
	 * pingChecker is a thread that will launch checkPing()
	 * firstPingReader is a thread that will launch firstPingReader()
	 */
	private final Thread pingChecker, firstPingReader;
	
	public PlayerManager(Socket socket, int id, ServerController controller, String threadName) throws IOException{
		super(threadName);
		this.controller = controller;
		playerID = id;
		ping = true;
		pingChecker = new Thread(this::checkPing);
		firstPingReader = new Thread(this::firstPingReader);
		
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		output = new PrintWriter(socket.getOutputStream(), true);
	}
	
	@Override
	public void run(){
		// Starting the ping waiter and reader
		firstPingReader.start();
		pingChecker.start();
		
		try{
			if(controller.isDebugging()) System.out.println("Player-" + playerID + " connected");
			// If the number of players has not yet been chosen, only player 0 starts and the others wait
			if(playerID != 0){
				synchronized(controller){
					while(!controller.gameExists() && playerID != 0)
						controller.wait();
				}
				
				// If there are more players than expected, shut down this player
				if(playerID != 0 && playerID >= controller.getPlayersNumber()){
					output.println(new WrongMessage("ServerFullOfPlayer").toJSON());
					controller.removePlayerManager(playerID);
					return;
				}
			}
			
			firstPingReader.interrupt();
			
			// The firsts thing that a player receives is its own ID and an array with other players' nicknames
			output.println(controller.createIDMessage(playerID));
			output.println(controller.createNickNamesMessage());
			
			// From now on, the player manager has to read what comes from the player and send everything to the controller
			do{
				try{
					JSONObject received = new JSONObject(input.readLine());
					
					// Check if it's a ping
					if(Objects.equals(received.getString("type"), "PingMessage")){
						ping = true;
						continue;
					}
					if(controller.isDebugging()) System.out.println("Received: " + received);
					
					controller.manageMessage(received);
					output.println(new OkMessage().toJSON());
				}catch(InvalidMessageException e){
					output.println(new WrongMessage(e.getMessage()).toJSON());
				}
			}while(true);
		}catch(IOException | InterruptedException | NullPointerException e){
			pingChecker.interrupt();
			if(controller.isDebugging()) System.out.println("Player-" + playerID + " disconnected");
			
			/*
			 * The easy remove happens when the player doesn't have nickname yet.
			 * - If this is the player 0 there is easy remove only if the game is not started yet.
			 */
			if(!controller.haveANickname(playerID) && !((playerID == 0) && controller.gameExists())){
				output.println(new WrongMessage("AnomalyShutDown").toJSON());
				controller.easyRemovePlayerManager(playerID);
			}else
				try{
					controller.removePlayerManager(playerID);
				}catch(IOException | NullPointerException ex){
					anomalyShutDown();
				}
		}
	}
	
	/**
	 * Shut down the player manager alerting the client but not the controller
	 */
	public void anomalyShutDown(){
		output.println(new WrongMessage("AnomalyShutDown").toJSON());
		this.interrupt();
	}
	
	/**
	 * Override the ID
	 */
	public void changeID(int newID){
		playerID = newID;
	}
	
	/**
	 * This method will read data from controller and finally send the new data to player
	 */
	@Override
	public void update(){
		// Before the first turn, the player will receive a list with all nicknames and info about the number of player and goals
		if(firstTurn){
			output.println(controller.createFirstInfoMessage(playerID));
			output.println(controller.createNickNamesMessage());
			firstTurn = false;
		}
		output.println(controller.createGameDataMessage());
		
		if(controller.isLastRound() && controller.getTurn() == 0) output.println(controller.createFinalMessage());
	}
	
	@Override
	public void sendMessage(JSONObject obj){
		output.println(obj);
	}
	
	/**
	 * The server expects a ping every x << 2 seconds <br/>
	 * Every time a ping arrives to the player manager, sets boolean variable "ping" to true <br/>
	 * Every 2 seconds, the thread pingChecker, via this method, checks if "ping" is true <br/>
	 * If "ping" is true => the pings have arrived correctly so far - then sets "ping" to false and wait 2 seconds <br/>
	 * If "ping" is false => no pings have arrived in the last 2 seconds, so the playerManager drops the player
	 */
	private void checkPing(){
		// During some tests the ping's mechanic is off
		if(controller.isTestNetwork()) return;
		try{
			do{
				sleep(2000);
				if(this.isInterrupted() || pingChecker.isInterrupted()) return;
				if(!ping){
					synchronized(controller){
						if(controller.isDebugging()) System.out.println("Client-" + playerID + " is down");
						if(controller.gameExists() && controller.haveANickname(playerID))
							controller.removePlayerManager(playerID);
						else
							controller.easyRemovePlayerManager(playerID);
					}
				}
				ping = false;
			}while(true);
		}catch(IOException e){
			anomalyShutDown();
		}catch(InterruptedException ignored){
		}
	}
	
	/**
	 * This method is called in a special thread that starts in initial phases of connection; <br/>
	 * All other messages will be read after the ID has been sent to the player, instead pings will be read right away. <br/>
	 * After the ID has been sent, the firstPingReader will shut down and the ping will be read by the default reader.
	 */
	private void firstPingReader(){
		// During some tests the ping mechanics is off
		if(controller.isTestNetwork()) return;
		try{
			while(true){
				JSONObject obj = new JSONObject(input.readLine());
				if(firstPingReader.isInterrupted()) return;
				if(Objects.equals(obj.getString("type"), "PingMessage"))
					ping = true;
				else{
					if(controller.isDebugging()) System.out.println("Unexpected message: " + obj);
					return;
				}
			}
		}catch(IOException ignored){
		}
	}
}