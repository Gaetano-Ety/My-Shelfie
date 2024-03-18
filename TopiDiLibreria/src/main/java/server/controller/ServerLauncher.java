package server.controller;

import server.controller.network.ServerInterface;

public class ServerLauncher{
	public static void start(boolean debugMode){
		do{
			ServerInterface.getServer().launch(new ServerController(), debugMode);
		}while(true);
	}
	
	public static void start(ServerController controller){
		do{
			ServerInterface.getServer().launch(controller, true);
		}while(true);
	}
}
