package client.view.gui.otherWindows;

import client.view.gui.ImagesStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A window with a message and a button
 */
public class AlertBox{
	/**
	 * Show an alert box
	 *
	 * @param title      is the title of the window
	 * @param message    is the text that you want to show in the alert box
	 * @param buttonText is the text that you want to show in the button
	 * @param terminate  indicate if you want close the game with the button
	 */
	public static void showAlertBox(String title, String message, String buttonText, boolean terminate){
		Stage window = new Stage();
		window.setTitle(title);
		window.setResizable(false);
		window.initModality(Modality.APPLICATION_MODAL);
		
		Label text = new Label(message);
		text.setFont(Font.font(20));
		text.setPadding(new Insets(10));
		
		Button button = new Button(buttonText);
		button.setPadding(new Insets(2, 8, 2, 8));
		button.setFont(Font.font(22));
		
		if(terminate) button.setOnAction(e -> System.exit(1));
		else button.setOnAction(e -> window.close());
		
		VBox layout = new VBox();
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(text, button);
		
		Scene scene = new Scene(layout, 500, 400);
		
		window.setScene(scene);
		window.getIcons().add(ImagesStore.getIcon());
		window.show();
		if(terminate) window.setOnCloseRequest(e -> System.exit(1));
		else window.setOnCloseRequest(e -> window.close());
	}
}
