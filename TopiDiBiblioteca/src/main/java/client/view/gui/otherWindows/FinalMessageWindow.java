package client.view.gui.otherWindows;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Window that show the final rank
 */
public class FinalMessageWindow{
	public static void show(HashMap<Integer, Integer> rank, String[] nickNames){
		Stage window = new Stage();
		window.setTitle("End game");
		window.setResizable(false);
		window.initModality(Modality.WINDOW_MODAL);
		
		Label title = new Label("Game over");
		title.setFont(Font.font(22));
		title.setPadding(new Insets(4));
		
		Label subTitle = new Label("Here is a rank of players score:");
		subTitle.setFont(Font.font(16));
		title.setPadding(new Insets(0, 0, 12, 0));
		
		VBox ranking = new VBox();
		ranking.setPadding(new Insets(8, 0, 8, 0));
		rank.forEach((key, value) -> {
			Label label = new Label(nickNames[key] + " : " + value + "points");
			ranking.getChildren().add(label);
		});
		
		Iterator<Map.Entry<Integer, Integer>> iterator = rank.entrySet().iterator();
		Map.Entry<Integer, Integer> winner = iterator.next();
		String winnerText = "\n" + nickNames[(winner.getKey())];
		boolean tie = false;
		while(iterator.hasNext()){
			Map.Entry<Integer, Integer> next = iterator.next();
			if(Objects.equals(winner.getValue(), next.getValue())){
				winnerText += " and " + nickNames[next.getKey()];
				tie = true;
			}
		}
		winnerText += (tie) ? " are the winners" : " is the winner";
		winnerText += " with " + winner.getValue() + " points!!!";
		
		
		Label labelWinner = new Label(winnerText);
		labelWinner.setFont(Font.font(14));
		labelWinner.setPadding(new Insets(0, 0, 10, 0));
		
		Button closeButton = new Button("Close the game");
		closeButton.setPadding(new Insets(2, 8, 2, 8));
		closeButton.setFont(Font.font(20));
		closeButton.setOnAction(e -> System.exit(0));
		
		VBox layout = new VBox();
		layout.setPadding(new Insets(10));
		layout.getChildren().addAll(title, subTitle, ranking, labelWinner, closeButton);
		Scene scene = new Scene(layout, 720, 512);
		
		window.setScene(scene);
		window.show();
		window.setOnCloseRequest(e -> System.exit(0));
	}
}
