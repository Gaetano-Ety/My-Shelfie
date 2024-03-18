package client.view.gui.mainSceneItems.rightMenuItems;

import client.view.gui.ImagesStore;
import client.view.gui.UserInterfaceGUI;
import client.virtualModel.GameData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import model.Color;

import java.util.ArrayList;

/**
 * This menu allows user to switch order of chosen tiles and chose the column of own bookshelf where put them
 */
public class PlayMenu{
	private final VBox layout;
	private final VBox hiddenLayout;
	private final HBox tiles;
	private final ArrayList<Color> selected;
	
	public PlayMenu(UserInterfaceGUI gui, GameData gameData){
		gui.setPlayMenu(this);
		selected = new ArrayList<>();
		tiles = new HBox();
		layout = new VBox();
		
		hiddenLayout = new VBox();
		hiddenLayout.setPadding(new Insets(0, 0, 0, 45));
		
		Button resetButton = new Button("Reset");
		resetButton.setOnAction(e -> {
			tiles.getChildren().clear();
			gui.resetBoard();
			selected.clear();
		});
		
		tiles.setMinSize(300, 100);
		tiles.setSpacing(5);
		tiles.setAlignment(Pos.CENTER_LEFT);
		
		Label label = new Label("Your selected tiles: ");
		label.setFont(Font.font(15));
		label.setPadding(new Insets(0, 0, 8, 0));
		
		HBox arrows = new HBox();
		arrows.setSpacing(49);
		arrows.setPadding(new Insets(0, 0, 0, 25));
		
		for(int i = 0; i < 5; i++){
			ImageView imageView = new ImageView(ImagesStore.getArrows());
			imageView.setFitHeight(30);
			imageView.setFitWidth(30);
			final int ii = i;
			imageView.setOnMouseClicked(e -> {
				if(selected.size() > 0)
					if(gameData.getFreeInColumn(ii, gameData.getMyID()) >= selected.size())
						gui.sendCardsToController(ii, selected.toArray(Color[]::new));
			});
			
			arrows.getChildren().add(imageView);
		}
		
		HBox box = new HBox();
		box.getChildren().addAll(tiles, resetButton);
		hiddenLayout.getChildren().addAll(label, box, arrows);
	}
	
	public void show(){
		if(layout.getChildren().size() == 0)
			layout.getChildren().add(hiddenLayout);
	}
	
	public void hide(){
		layout.getChildren().clear();
		tiles.getChildren().clear();
		selected.clear();
	}
	
	public void addCard(Color color){
		selected.add(color);
		ImageView tileImage = new ImageView();
		
		tileImage.setFitHeight(64);
		tileImage.setFitWidth(64);
		
		tileImage.setImage(ImagesStore.getTile(color.toString()));
		
		if(tiles.getChildren().size() > 0){
			ImageView roundImage = new ImageView();
			roundImage.setFitHeight(20);
			roundImage.setFitWidth(20);
			roundImage.setImage(ImagesStore.getRound());
			
			int n = tiles.getChildren().size();
			roundImage.setOnMouseClicked(e -> {
				roundTiles(n - 1, n + 1);
				roundColor((n - 1) / 2, (n - 1) / 2 + 1);
			});
			
			tiles.getChildren().add(roundImage);
		}
		
		tiles.getChildren().add(tileImage);
	}
	
	private void roundTiles(int a, int b){
		Node a1 = tiles.getChildren().remove(a);
		tiles.getChildren().add(a, tiles.getChildren().remove(b - 1));
		tiles.getChildren().add(b, a1);
	}
	
	private void roundColor(int a, int b){
		Color ca1 = selected.remove(a);
		selected.add(a, selected.remove(b - 1));
		selected.add(b, ca1);
	}
	
	public VBox getLayout(){
		return layout;
	}
}