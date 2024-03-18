import client.controller.ClientController;
import server.controller.ServerLauncher;

import java.util.Objects;

public class Launcher{
	/**
	 * Argument accepted:
	 * [Server IP]
	 * --server (debug)
	 * --cli [Server IP] (--debug)
	 * --gui [Server IP] (--debug)
	 */
	public static void main(String[] args){
		switch(args.length){
			case 1 -> {
				if(Objects.equals("--server", args[0]))
					ServerLauncher.start(false);
				else if(isAnIP(args[0]))
					new ClientController(0, false, args[0]).startController();
				else System.out.println("Invalid arguments");
			}
			
			case 2 -> {
				if(Objects.equals("--server", args[0]))
					if(Objects.equals("debug", args[1]))
						ServerLauncher.start(true);
					else System.out.println("Invalid arguments");
				else if(isAnIP(args[1]))
					if(Objects.equals("--cli", args[0]))
						new ClientController(0, false, args[1]).startController();
					else if(Objects.equals("--gui", args[0]))
						new ClientController(1, false, args[1]).startController();
					else System.out.println("Invalid arguments");
				else System.out.println("Invalid arguments");
			}
			
			case 3 -> {
				if(isAnIP(args[1]) && Objects.equals("--debug", args[2]))
					if(Objects.equals("--cli", args[0]))
						new ClientController(0, true, args[1]).startController();
					else if(Objects.equals("--gui", args[0]))
						new ClientController(1, true, args[1]).startController();
					else System.out.println("Invalid arguments");
				else System.out.println("Invalid arguments");
			}
		}
	}
	
	private static boolean isAnIP(String ip){
		if(Objects.equals(ip, "localhost")) return true;
		return ip.matches("^([0-9]*\\.){3}[0-9]*$");
	}
}