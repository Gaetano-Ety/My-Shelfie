package client.view.gui.mainSceneItems.leftMenuItems;

import client.view.gui.ImagesStore;
import client.view.gui.UserInterfaceGUI;
import client.virtualModel.GameData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Left menu of the main scene;<br/>
 * It has the list of players, the chat button(s), and the goal images.<br/>
 * It also shows the actual turn and a button that allows to play.
 */
public class LeftMenu{
	private final VBox layout;
	private final PlayersGUI playersGUI;
	
	public LeftMenu(UserInterfaceGUI gui, GameData gameData){
		layout = new VBox();
		layout.setPrefWidth(200);
		layout.setSpacing(5);
		playersGUI = new PlayersGUI(gui, gameData);
		VBox players = playersGUI.getLayout();
		HBox turn = new TurnIndicatorGUI(gui, gameData).getLayout();
		
		Label l1 = new Label("Goals: ");
		l1.setPadding(new Insets(10, 0, 0, 0));
		l1.setFont(Font.font(15));
		
		// Loading common goals images
		VBox commonGoals = new CommonGoalsGUI(gui, gameData).getLayout();
		
		// Loading personal goal images
		VBox personalGoal = new VBox();
		personalGoal.setAlignment(Pos.CENTER);
		ImageView pgw = new ImageView(ImagesStore.getPersonalGoal(gameData.getPersonalGoalID() + 1));
		pgw.setFitWidth(160);
		pgw.setFitHeight(232);
		VBox.setMargin(pgw, new Insets(8, 0, 0, 0));
		personalGoal.getChildren().add(pgw);
		
		layout.getChildren().addAll(players, turn, l1, commonGoals, personalGoal);
	}
	
	public VBox getLayout(){
		return layout;
	}
	
	public PlayersGUI getPlayersGUI(){
		return playersGUI;
	}
}