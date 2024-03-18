package client.view.gui.mainSceneItems;

import client.view.gui.ImagesStore;
import client.view.gui.ObserverGUIItem;
import client.view.gui.UserInterfaceGUI;
import client.virtualModel.GameData;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The window with the chat
 */
public class ChatWindow implements ObserverGUIItem{
	private final GameData gameData;
	private final int friendID;
	private final VBox messages;
	private final String title;
	private final Scene chatScene;
	private ArrayList<String> chat;
	private final UserInterfaceGUI ui;
	private boolean isShowing;
	private final TextField textField;
	
	public ChatWindow(UserInterfaceGUI ui, String title, GameData gameData, int friendID){
		this.ui = ui;
		this.gameData = gameData;
		this.friendID = friendID;
		this.title = title;
		isShowing = false;
		
		chat = new ArrayList<>();
		chat = gameData.getCompletePlayerChat(gameData.getMyID());
		
		textField = new TextField();
		textField.setPromptText("Insert your message here");
		textField.setPadding(new Insets(2));
		textField.setPrefSize(1000, 30);
		HBox.setMargin(textField, new Insets(0, 8, 0, 0));
		
		Button button = new Button();
		button.setMinSize(100, 30);
		button.setText("Send message");
		
		HBox textBox = new HBox();
		textBox.getChildren().addAll(textField, button);
		VBox.setMargin(textBox, new Insets(10, 0, 0, 0));
		
		messages = new VBox();
		ScrollPane scrollPane = new ScrollPane(messages);
		scrollPane.setFitToWidth(true);
		scrollPane.setPadding(new Insets(5));
		
		VBox layout = new VBox();
		layout.setAlignment(Pos.BOTTOM_LEFT);
		layout.setPadding(new Insets(10));
		layout.getChildren().addAll(scrollPane, textBox);
		
		chatScene = new Scene(layout, 400, 400);
		
		button.setOnAction(e -> readMessage());
		chatScene.setOnKeyPressed(e -> {
			if(isShowing && e.getCode() == KeyCode.ENTER)
				readMessage();
			else if(isShowing && e.getCode() == KeyCode.ESCAPE){
				chatScene.getWindow().hide();
				isShowing = false;
				ui.removeFromChat(friendID);
			}
		});
	}
	
	private void readMessage(){
		String message = textField.getText();
		if(!Objects.isNull(message) && !message.equals("")){
			ui.createChatMessage(message, friendID);
			textField.clear();
		}
	}
	
	private void updateChat(){
		chat = (gameData.getCompletePlayerChat(friendID).size() > chat.size()) ? gameData.getCompletePlayerChat(friendID) : chat;
		messages.getChildren().clear();
		for(String message : chat){
			Label label = new Label(message);
			label.setWrapText(true);
			messages.getChildren().addAll(label);
		}
	}
	
	public void showChat(){
		if(!isShowing){
			isShowing = true;
			Stage window = new Stage();
			window.initModality(Modality.WINDOW_MODAL);
			window.setTitle(title);
			window.setScene(chatScene);
			window.getIcons().add(ImagesStore.getIcon());
			window.show();
			window.setOnCloseRequest(we -> {
				window.close();
				isShowing = false;
				ui.removeFromChat(friendID);
			});
		}
	}
	
	@Override
	public void update(){
		Platform.runLater(this::updateChat);
	}
}