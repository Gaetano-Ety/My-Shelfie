package client.view.gui.mainSceneItems.rightMenuItems;

import client.view.gui.ImagesStore;
import client.view.gui.ObserverGUIItem;
import client.virtualModel.GameData;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import model.gameObjects.BookShelf;
import model.gameObjects.SpotMatrix;

import java.util.Objects;

/**
 * Part of gui that show the bookshelves of the players
 */
public class BookshelfGUI implements ObserverGUIItem{
	private final StackPane layout;
	private final GameData gameData;
	private final GridPane[] bookshelves;
	private int actualPrinted = -1;
	private final String[] memorizedBookshelves;
	
	public BookshelfGUI(GameData gd){
		layout = new StackPane();
		gameData = gd;
		bookshelves = new GridPane[gameData.getNPlayers()];
		memorizedBookshelves = new String[gameData.getNPlayers()];
		
		for(int id = 0; id < gameData.getNPlayers(); id++){
			bookshelves[id] = new GridPane();
			bookshelves[id].setHgap(20);
			bookshelves[id].setVgap(10);
			StackPane.setMargin(bookshelves[id], new Insets(33, 0, 0, 57));
		}
		
		// Set the background bookshelf
		ImageView imageView = new ImageView(ImagesStore.getBookshelf());
		imageView.setFitHeight(500);
		imageView.setFitWidth(490);
		layout.getChildren().add(imageView);
		
		layout.setMinSize(420, 420);
		updateBookshelves();
		showABookshelf(gameData.getMyID());
	}
	
	public void showABookshelf(int id){
		if(id < 0 || id >= gameData.getNPlayers() || id == actualPrinted) return;
		actualPrinted = id;
		
		if(layout.getChildren().size() > 1)
			layout.getChildren().remove(1);
		layout.getChildren().add(bookshelves[id]);
	}
	
	private void updateBookshelves(){
		for(int i = 0; i < gameData.getNPlayers(); i++){
			SpotMatrix bs = gameData.getBookShelf(i);
			
			if(Objects.equals(memorizedBookshelves[i], bs.toString())) continue;
			memorizedBookshelves[i] = bs.toString();
			
			bookshelves[i].getChildren().clear();
			int dim = 59;
			for(int x = 0; x < BookShelf.getNumCols(); x++){
				for(int y = 0; y < BookShelf.getNumRows(); y++){
					ImageView imageView = new ImageView();
					imageView.setFitHeight(dim);
					imageView.setFitWidth(dim);
					
					if(!bs.getSpotAt(x, y).isVoid())
						imageView.setImage(ImagesStore.getTile(bs.getColorAt(x, y).toString()));
					
					bookshelves[i].add(imageView, x, y);
				}
			}
		}
	}
	
	public StackPane getLayout(){
		return layout;
	}
	
	@Override
	public void update(){
		Platform.runLater(this::updateBookshelves);
	}
}
