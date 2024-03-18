package server.controller.network;

import server.controller.ServerController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class ServerInterface{
	public static final int port = 1234;
	private static ServerInterface instance;
	private ServerSocket ss;
	private boolean active = false;
	
	private ServerInterface(){
	}
	
	public static ServerInterface getServer(){
		if(Objects.isNull(instance)) instance = new ServerInterface();
		return instance;
	}
	
	/**
	 * This launch the main server - every player that connects will be managed by the controller
	 */
	public void launch(ServerController controller, boolean debugMode){
		try{
			controller.startGame(debugMode);
			active = true;
			ss = new ServerSocket(port);
			
			if(debugMode){
				System.out.println("-The server is ready-");
				String ip = getMyIP();
				if(Objects.isNull(ip))
					System.out.println("Unable to read the IP");
				else
					System.out.println("The IP is: " + ip);
			}
			
			do{
				try{
					Socket s = ss.accept();
					controller.newPlayerManager(s);
				}catch(IOException ignored){
					// If a player fails to connect, the server keeps going
				}
			}while(active);
		}catch(IOException e){
			if(active){
				System.out.println("Error during the connection...");
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void closeConnection() throws IOException{
		active = false;
		ss.close();
	}
	
	public boolean isActive(){
		return active;
	}
	
	public String getMyIP(){
		try(Socket socket = new Socket()){
			socket.connect(new InetSocketAddress("google.com", 80));
			return socket.getLocalAddress().getHostAddress();
		}catch(IOException e){
			return null;
		}
	}
}

