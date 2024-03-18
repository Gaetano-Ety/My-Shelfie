package client.view.gui.otherWindows;

import client.view.gui.ImagesStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Class that is extended when it will be necessary to ask the player for data
 */
public abstract class RequestWindow{
	public static final Object requestLock = new Object();
	private static Stage newWindow;
	final Label label1;
	final Label label2;
	final Button button;
	final TextField text;
	final Scene mainScene;
	
	public RequestWindow(String title){
		if(Objects.isNull(newWindow)){
			newWindow = new Stage();
			newWindow.initModality(Modality.APPLICATION_MODAL);
		}else newWindow.close();
		
		newWindow.setTitle(title);
		
		label1 = new Label();
		label2 = new Label();
		label2.setFont(Font.font(0));
		VBox.setMargin(label2, new Insets(20, 0, 0, 0));
		
		text = new TextField();
		text.setPromptText("Insert here");
		text.setMaxSize(250, 20);
		VBox.setMargin(text, new Insets(10));
		
		button = new Button();
		
		VBox layout = new VBox();
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(label1, text, button, label2);
		layout.setPadding(new Insets(10));
		
		mainScene = new Scene(layout, 400, 400);
		newWindow.setScene(mainScene);
		newWindow.getIcons().add(ImagesStore.getIcon());
		newWindow.setOnCloseRequest(e -> closeWindow());
		newWindow.show();
	}
	
	public void closeWindow(){
		synchronized(requestLock){
			requestLock.notify();
		}
		newWindow.close();
	}
}