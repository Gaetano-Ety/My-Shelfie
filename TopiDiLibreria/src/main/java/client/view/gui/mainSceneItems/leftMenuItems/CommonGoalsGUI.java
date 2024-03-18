package client.view.gui.mainSceneItems.leftMenuItems;

import client.view.gui.ImagesStore;
import client.view.gui.ObserverGUIItem;
import client.view.gui.UserInterfaceGUI;
import client.virtualModel.GameData;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.exceptions.NotAvailableGoalException;

import java.util.EmptyStackException;

/**
 * Part of the gui representing the common goals
 */
public class CommonGoalsGUI implements ObserverGUIItem{
	private final VBox layout;
	private final ImageView[] scores;
	private final StackPane[] goals;
	private final int[] lastScore;
	private final GameData gameData;
	
	public CommonGoalsGUI(UserInterfaceGUI gui, GameData gameData){
		gui.sayNewObserver(this);
		this.gameData = gameData;
		layout = new VBox();
		goals = new StackPane[]{new StackPane(), new StackPane()};
		scores = new ImageView[]{new ImageView(), new ImageView()};
		lastScore = new int[2];
		
		for(int i = 0; i < 2; i++)
			try{
				ImageView cgw = new ImageView(ImagesStore.getCommonGoal(gameData.getGoalID(i + 1)));
				cgw.setFitWidth(192);
				cgw.setFitHeight(128);
				StackPane.setMargin(cgw, new Insets(8, 0, 0, 0));
				
				goals[i].getChildren().add(cgw);
				layout.getChildren().add(goals[i]);
				
				goals[i].getChildren().add(scores[i]);
				scores[i].setFitWidth(66);
				scores[i].setFitHeight(66);
				StackPane.setMargin(scores[i], new Insets(5, 0, 0, 90));
				updateScores();
			}catch(NotAvailableGoalException e){
				throw new RuntimeException(e);
			}
	}
	
	public VBox getLayout(){
		return layout;
	}
	
	public void updateScores(){
		for(int i = 0; i < 2; i++){
			try{
				int actual = gameData.getGoalScores(i + 1).peek();
				if(lastScore[i] == actual) continue;
				lastScore[i] = actual;
				scores[i].setImage(ImagesStore.getToken(actual));
			}catch(EmptyStackException e){
				lastScore[i] = 0;
				goals[i].getChildren().remove(scores[i]);
			}catch(NotAvailableGoalException e){
				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	public void update(){
		Platform.runLater(this::updateScores);
	}
}
