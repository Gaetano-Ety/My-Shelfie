package client.view.gui.mainSceneItems.leftMenuItems;

import client.view.gui.UserInterfaceGUI;
import client.virtualModel.GameData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Part of main scene with the list of players and chat button(s)
 */
public class PlayersGUI{
	/*playerChatButton.setStyle("-fx-background-color: MediumSeaGreen");*/
	private final VBox layout;
	Button[] playerChatButton;
	Button publicChatButton;
	private final int numOfPlayers;
	UserInterfaceGUI gui;
	
	public PlayersGUI(UserInterfaceGUI gui, GameData gameData){
		this.gui = gui;
		numOfPlayers = gameData.getNPlayers();
		String[] nickNames = gameData.getNickNames();
		layout = new VBox();
		layout.setSpacing(5);
		
		Label l1 = new Label("List of players: ");
		l1.setFont(Font.font(18));
		
		Label l2 = new Label("You are: " + gameData.getMyNickName());
		l2.setFont(Font.font(14));
		
		layout.getChildren().addAll(l1, l2);
		
		playerChatButton = new Button[gameData.getNPlayers()];
		playerChatButton[gameData.getMyID()] = new Button();
		for(int i = 0; i < nickNames.length; i++){
			if(gameData.getMyID() == i) continue;
			HBox div = new HBox();
			
			HBox player = new HBox();
			player.setPrefSize(150, 25);
			player.getChildren().add(new Text(" - " + nickNames[i]));
			
			HBox button = new HBox();
			button.setAlignment(Pos.CENTER_RIGHT);
			playerChatButton[i] = new Button("Chat");
			playerChatButton[i].setStyle("-fx-background-color: CDFBFE");
			final int id = i;
			playerChatButton[i].setOnAction(e -> gui.openChat(id));
			playerChatButton[i].resize(50, 25);
			button.getChildren().add(playerChatButton[i]);
			
			div.getChildren().addAll(player, button);
			
			layout.getChildren().add(div);
		}
		
		if(numOfPlayers > 2){
			publicChatButton = new Button("Public Chat");
			publicChatButton.setStyle("-fx-background-color: CDFBFE");
			publicChatButton.setPrefSize(128, 32);
			publicChatButton.setFont(Font.font(14));
			publicChatButton.setOnAction(e -> gui.openChat());
			VBox.setMargin(publicChatButton, new Insets(0, 0, 0, 10));
			layout.getChildren().add(publicChatButton);
		}
	}
	
	public VBox getLayout(){
		return layout;
	}
	
	public void changeColor(int id){
		if (id == -1 && numOfPlayers > 2) publicChatButton.setStyle("-fx-background-color: F77375");
		else playerChatButton[id].setStyle("-fx-background-color: F77375");

	}

	public void changeBackColor(int id) {
		if(id==-1 && numOfPlayers>2) publicChatButton.setStyle("-fx-background-color: CDFBFE");
		else playerChatButton[id].setStyle("-fx-background-color: CDFBFE");
	}
}
