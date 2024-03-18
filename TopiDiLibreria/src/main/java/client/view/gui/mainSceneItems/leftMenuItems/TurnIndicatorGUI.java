package client.view.gui.mainSceneItems.leftMenuItems;

import client.view.gui.ObserverGUIItem;
import client.view.gui.UserInterfaceGUI;
import client.view.gui.otherWindows.AlertBox;
import client.virtualModel.GameData;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class TurnIndicatorGUI implements ObserverGUIItem{
	private final HBox layout;
	private final GameData gameData;
	private final Label text;
	private final UserInterfaceGUI gui;
	private int lastTurn;
	
	public TurnIndicatorGUI(UserInterfaceGUI gui, GameData gd){
		gui.sayNewObserver(this);
		gameData = gd;
		this.gui = gui;
		layout = new HBox();
		layout.setPrefHeight(35);
		text = new Label();
		layout.setSpacing(10);
		text.setFont(Font.font(18));
		lastTurn = -1; // An impossible turn
		
		update();
	}
	
	public HBox getLayout(){return layout;}
	
	private void updateTurn(){
		if(lastTurn == gameData.getTurn() && !gui.isGameEnded()) return;
		lastTurn = gameData.getTurn();
		
		layout.getChildren().clear();
		layout.getChildren().add(text);
		
		if(gameData.isMyTurn()){
			if(!gui.isGameEnded()){
				Button playButton = new Button("Play!");
				playButton.setPrefSize(86, 32);
				playButton.setOnAction(e -> gui.playTurn());
				layout.getChildren().add(playButton);
				// AlertBox.showAlertBox("Your turn", "It's your turn", "Ok", false);
			}
			text.setText("It's your turn");
		}else text.setText("It's turn of: " + gameData.getNickName(lastTurn));
	}
	
	@Override
	public void update(){
		Platform.runLater(this::updateTurn);
	}
}
