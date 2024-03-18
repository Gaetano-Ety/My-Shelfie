package client.controller.network;

import client.controller.ClientController;
import org.json.JSONObject;
import server.controller.network.ServerInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
public class ClientInterface extends Thread{
	private PrintWriter output;
	private final ClientController controller;
	private final String serverIP;
	
	public ClientInterface(ClientController c, String serverIP){
		controller = c;
		this.serverIP = serverIP;
	}

	/**
	 *Connection part: sets input and output, starts to send pings to server
	 * Game part: receives messages and sends them to the message manager
	 */
	@Override
	public void run(){
		try(Socket s = new Socket(serverIP, ServerInterface.port)){
			if(controller.isDebugging())
				System.out.println("Connected with the server - IP = " + serverIP + ", port = " + ServerInterface.port);
			// CONNECTION PART
			BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			output = new PrintWriter(s.getOutputStream(), true);
			new Thread(this::sendPing).start();
			
			//GAME
			while(!isInterrupted())
				controller.getMessage(new JSONObject(input.readLine()));
			
		}catch(IOException | NullPointerException e){
			if(!isInterrupted()){
				controller.stopGame(2);
			}
		}
	}

	/**
	 * Method used by ClientController to send messages to the server
	 * @param message is a message for the server
	 */
	public void sendMessage(JSONObject message){
		if(controller.isDebugging()) System.out.println("Send to the server: " + message);
		output.println(message);
	}

	/**
	 * Sends pings to let the server know the client is still alive and connected
	 */
	public void sendPing(){
		if(controller.isDebugging()) System.out.println("Ping sender started");
		try{
			while(controller.isRunning()){
				if(this.isInterrupted()) return;
				output.println(controller.createPing());
				sleep(350);
			}
		}catch(InterruptedException ignored){
		}
	}
}