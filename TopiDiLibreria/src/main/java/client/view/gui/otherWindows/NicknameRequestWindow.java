package client.view.gui.otherWindows;

import client.view.gui.UserInterfaceGUI;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;

import java.util.Objects;

/**
 * Window that allows you to read the nickname of a player
 */
public class NicknameRequestWindow extends RequestWindow{
	private static String nickName = "";
	private final boolean error;
	private final String errorMessage = "Nickname not valid, insert only alphanumeric character and at lest one";
	
	private NicknameRequestWindow(boolean error){
		super("Nickname request");
		this.error = error;
		
		nickName = "";
		
		label1.setText("Insert your nickname:");
		if(error){
			label2.setText("Nickname already chosen!");
			label2.setFont(Font.font(12));
		}else label2.setText(errorMessage);
		
		button.setText("Send nickname");
		button.setOnAction(e -> readNickname());
		mainScene.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER)
				readNickname();
		});
	}
	
	/**
	 * Save the nickname in the nickname variable to allow other threads to read it
	 */
	private void readNickname(){
		synchronized(requestLock){
			nickName = text.getText();
			if(Objects.equals(nickName, "")) return;
			if(!UserInterfaceGUI.checkLocalUsernameAlphaNumeric(nickName)){
				label2.setFont(Font.font(12));
				if(error) label2.setText(errorMessage);
				
				nickName = "";
				return;
			}
			closeWindow();
		}
	}
	
	public static String getNickname(){
		return nickName;
	}
	
	public static void display(boolean alreadyChosen){
		new NicknameRequestWindow(alreadyChosen);
	}
	
	public static void reset(){
		nickName = "";
	}
}