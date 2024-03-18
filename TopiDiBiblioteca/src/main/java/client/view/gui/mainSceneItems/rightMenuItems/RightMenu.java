package client.view.gui.mainSceneItems.rightMenuItems;

import client.view.gui.UserInterfaceGUI;
import client.virtualModel.GameData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Right menu of the main scene;<br/>
 * It has the set of bookshelves<br/>
 * It also shows, after the user press the play button, the play menu
 */
public class RightMenu{
	private final VBox layout;
	private final BookshelfGUI bsGUI;
	
	public RightMenu(UserInterfaceGUI gui, GameData gameData){
		layout = new VBox();
		layout.setSpacing(8);
		layout.setAlignment(Pos.BOTTOM_CENTER);
		BorderPane.setMargin(layout, new Insets(0, 0, 128, 0));
		
		bsGUI = new BookshelfGUI(gameData);
		
		gui.sayNewObserver(bsGUI);
		StackPane bookshelf = bsGUI.getLayout();
		
		// Buttons for the bookshelves
		HBox buttons = new HBox();
		buttons.setSpacing(6);
		buttons.setAlignment(Pos.CENTER);
		VBox.setMargin(buttons, new Insets(8, 0, 0, 0));
		
		for(int i = 0; i < gameData.getNPlayers(); i++){
			final int ii = i;
			String name;
			if(i == gameData.getMyID()) name = "- You -";
			else name = gameData.getNickName(i);
			
			Button b = new Button(name);
			b.setPrefSize(100, 25);
			b.setOnAction(e -> {
				resetColorButtons(buttons);
				bsGUI.showABookshelf(ii);
				setRedButton(b);
			});
			buttons.getChildren().add(b);
		}
		
		resetColorButtons(buttons);
		setRedButton((Button) buttons.getChildren().get(gameData.getMyID()));
		
		layout.getChildren().addAll(new PlayMenu(gui, gameData).getLayout(), bookshelf, buttons);
	}
	
	private void resetColorButtons(HBox buttonsBox){
		buttonsBox.getChildren().forEach(x -> ((Button) x)
			.setBorder(new Border(new BorderStroke(
				Color.BLACK,
				BorderStrokeStyle.SOLID,
				CornerRadii.EMPTY,
				new BorderWidths(2)
			))));
	}
	
	private void setRedButton(Button but){
		but.setBorder(new Border(new BorderStroke(
			Color.RED,
			BorderStrokeStyle.SOLID,
			CornerRadii.EMPTY,
			new BorderWidths(2)
		)));
	}
	
	public VBox getLayout(){
		return layout;
	}
}
