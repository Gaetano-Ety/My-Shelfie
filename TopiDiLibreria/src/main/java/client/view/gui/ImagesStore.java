package client.view.gui;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

/**
 * This class uses only static variables and methods to store the images as they are read from the files, so that an image can be displayed as fast as possible and reduce the possibility of errors.
 */
public class ImagesStore{
	private static final String resourcesFolder = "resources/";
	private static Image board, bookshelf, round, arrows, background, icon;
	private static HashMap<String, Image> tiles;
	private static HashMap<Integer, Image> tokens, commonGoals, personalGoal;
	
	public static Image getBoard(){
		if(Objects.isNull(board))
			try(FileInputStream input = new FileInputStream(resourcesFolder + "board.png")){
				board = new Image(input);
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		return board;
	}
	
	public static Image getBookshelf(){
		if(Objects.isNull(bookshelf))
			try(FileInputStream input = new FileInputStream(resourcesFolder + "bookshelf.png")){
				bookshelf = new Image(input);
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		return bookshelf;
	}
	
	public static Image getRound(){
		if(Objects.isNull(round))
			try(FileInputStream input = new FileInputStream(resourcesFolder + "round.png")){
				round = new Image(input);
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		return round;
	}
	
	public static Image getBackground(){
		if(Objects.isNull(background))
			try(FileInputStream input = new FileInputStream(resourcesFolder + "parquet.jpg")){
				background = new Image(input);
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		return background;
	}
	
	public static Image getIcon(){
		if(Objects.isNull(icon))
			try(FileInputStream input = new FileInputStream(resourcesFolder + "icon.png")){
				icon = new Image(input);
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		return icon;
	}
	
	public static Image getArrows(){
		if(Objects.isNull(arrows))
			try(FileInputStream input = new FileInputStream(resourcesFolder + "arrows_icon.png")){
				arrows = new Image(input);
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		return arrows;
	}
	
	public static Image getToken(int idx){
		if(Objects.isNull(tokens)) tokens = new HashMap<>();
		
		if(Objects.isNull(tokens.get(idx))){
			String name = idx == 1 ? "end_game_rotate.png" : ("scoring" + idx + "_rotate.png");
			try(FileInputStream input = new FileInputStream(resourcesFolder + "tokens_images/" + name)){
				tokens.put(idx, new Image(input));
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		}
		
		return tokens.get(idx);
	}
	
	public static Image getCommonGoal(int idx){
		if(Objects.isNull(commonGoals)) commonGoals = new HashMap<>();
		
		if(Objects.isNull(commonGoals.get(idx))){
			try(FileInputStream input = new FileInputStream(resourcesFolder + "commonGoals_images/cg" + idx + ".jpg")){
				commonGoals.put(idx, new Image(input));
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		}
		
		return commonGoals.get(idx);
	}
	
	public static Image getPersonalGoal(int idx){
		if(Objects.isNull(personalGoal)) personalGoal = new HashMap<>();
		
		if(Objects.isNull(personalGoal.get(idx))){
			try(FileInputStream input = new FileInputStream(resourcesFolder + "personalGoals_images/personalgoal" + idx + ".png")){
				personalGoal.put(idx, new Image(input));
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		}
		
		return personalGoal.get(idx);
	}
	
	public static Image getTile(String addr){
		if(Objects.isNull(tiles)) tiles = new HashMap<>();
		
		if(Objects.isNull(tiles.get(addr))){
			try(FileInputStream input = new FileInputStream(resourcesFolder + "tiles_images/" + addr.toLowerCase() + "1.png")){
				tiles.put(addr, new Image(input));
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		}
		
		return tiles.get(addr);
	}
}