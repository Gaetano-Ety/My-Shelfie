package client.view.gui.mainSceneItems;

import client.view.gui.ImagesStore;
import client.view.gui.ObserverGUIItem;
import client.view.gui.UserInterfaceGUI;
import client.virtualModel.GameData;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import model.gameObjects.SpotMatrix;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Show the board with his tiles
 */
public class BoardGUI implements ObserverGUIItem{
	private final StackPane layout;
	private final GridPane actualBoard;
	private final GameData gameData;
	private String actualPrintedBoard;
	private final ImageView token;
	private final UserInterfaceGUI gui;
	
	public BoardGUI(UserInterfaceGUI gui, GameData gameData){
		this.actualPrintedBoard = "";
		this.gameData = gameData;
		layout = new StackPane();
		actualBoard = new GridPane();
		this.gui = gui;
		
		ImageView imageView;
		imageView = new ImageView(ImagesStore.getBoard());
		imageView.setFitHeight(720);
		imageView.setFitWidth(720);
		
		StackPane.setAlignment(imageView, Pos.TOP_CENTER);
		StackPane.setMargin(actualBoard, new Insets(34, 0, 0, 42));
		actualBoard.setHgap(3);
		actualBoard.setVgap(3);
		
		layout.getChildren().add(imageView);
		layout.getChildren().add(actualBoard);
		layout.setMinSize(420, 420);
		
		int dim = 90;
		token = new ImageView(ImagesStore.getToken(1));
		token.setFitHeight(dim);
		token.setFitWidth(dim);
		StackPane.setMargin(token, new Insets(237, 0, 0, 518));
		layout.getChildren().add(token);
		
		updateBoard();
	}
	
	public void updateBoard(){
		SpotMatrix board = gameData.getGameBoard();
		if(Objects.equals(actualPrintedBoard, board.toString())) return;
		actualPrintedBoard = board.toString();
		
		actualBoard.getChildren().clear();
		for(int x = 0; x < 9; x++)
			for(int y = 0; y < 9; y++)
				showCard(board, x, y);
		
		if(gameData.someOneCompleted())
			layout.getChildren().remove(token);
	}
	
	private void showCard(SpotMatrix board, int x, int y){
		int dim = 69;
		ImageView imageView = new ImageView();
		imageView.setFitHeight(dim);
		imageView.setFitWidth(dim);
		
		final int xx = x, yy = y;
		if(!board.getSpotAt(x, y).isVoid()){
			imageView.setImage(ImagesStore.getTile(board.getColorAt(x, y).toString()));
			imageView.setOnMouseClicked(e -> {
				if(gui.selectCard(xx, yy))
					imageView.setImage(null);
			});
		}
		
		actualBoard.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == x && GridPane.getRowIndex(node) == y);
		actualBoard.add(imageView, x, y);
	}
	
	public StackPane getLayout(){
		return layout;
	}
	
	@Override
	public void update(){
		Platform.runLater(this::updateBoard);
	}
	
	public void reset(ArrayList<int[]> actualInvisible){
		for(int[] coos : actualInvisible)
			showCard(gameData.getGameBoard(), coos[0], coos[1]);
	}
}