import client.controller.ClientController;
import server.controller.ServerLauncher;

/**
 * Manual Launcher of Server and Client
 */
public class PseudoLauncher{
	private static final String serverIP = "localhost";
	
	static class myServerLauncher{
		public static void main(String[] args){
			ServerLauncher.start(true);
		}
	}
	
	static class MyClientLaunchers{
		static class MyClientLauncher1{
			public static void main(String[] args){
				new ClientController(1, false, serverIP).startController();
			}
		}
		
		static class MyClientLauncher2{
			public static void main(String[] args){
				new ClientController(1, false, serverIP).startController();
			}
		}
		
		static class MyClientLauncher3{
			public static void main(String[] args){
				new ClientController(0, false, serverIP).startController();
			}
		}
		
		static class MyClientLauncher4{
			public static void main(String[] args){
				new ClientController(0, false, serverIP).startController();
			}
		}
	}
}
