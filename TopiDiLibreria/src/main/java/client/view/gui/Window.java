package client.view.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Main window of the game
 */
public class Window extends Application{
	public static final int sizeH = 801, sizeW = 1424;
	private static final Object instanceLock = new Object();
	private Stage window;
	private static Window instance;
	
	@Override
	public void start(Stage primaryStage) throws Exception{
		instance = this;
		synchronized(instanceLock){
			instanceLock.notifyAll();
		}
		
		this.window = primaryStage;
		window.setTitle("MyBookshelf: by TopiDiLibreria");
		
		StackPane layout = new StackPane();
		layout.getChildren().add(new Label("Loading the components..."));
		
		window.setScene(new Scene(layout, sizeW, sizeH));
		window.setResizable(false);
		window.getIcons().add(ImagesStore.getIcon());
		window.show();
		window.setOnCloseRequest(e -> System.exit(0));
	}
	
	public void initialize(){
		launch();
	}
	
	public void showScene(Scene scene){
		window.setScene(scene);
	}
	
	public static Window getInstance(){
		synchronized(instanceLock){
			while(Objects.isNull(instance)){
				try{
					instanceLock.wait();
				}catch(InterruptedException e){
					System.exit(1);
				}
			}
		}
		return instance;
	}
	
	public double getWidth(){
		return Double.isNaN(window.getWidth()) ? sizeW : window.getWidth();
	}
	
	public double getHeight(){
		return Double.isNaN(window.getHeight()) ? sizeH : window.getHeight();
	}
}