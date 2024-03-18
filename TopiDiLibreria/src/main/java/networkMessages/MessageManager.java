package networkMessages;

import networkMessages.exceptions.InvalidMessageException;
import networkMessages.messages.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;

public abstract class MessageManager{
	private final ArrayList<JSONObject> notReads = new ArrayList<>();
	private final ArrayList<String> interests = new ArrayList<>();
	private boolean isAllInterested = false;
	
	/**
	 * Choose which message to handle and call the appropriate method
	 */
	public void manage(JSONObject message) throws InvalidMessageException{
		if(interests.contains(message.getString("type")) || isAllInterested)
			switch(message.getString("type")){
				case "FinalMessage" -> manageFinalMessage(FinalMessage.fromJSON(message));
				case "FirstInfoMessage" -> manageFirstInfoMessage(FirstInfoMessage.fromJSON(message));
				case "GameDataMessage" -> manageGameDataMessage(GameDataMessage.fromJSON(message));
				case "IDMessage" -> manageIDMessage(IDMessage.fromJSON(message));
				case "NickNameMessage" -> manageNickNameMessage(NickNameMessage.fromJSON(message));
				case "NickNamesMessage" -> manageNickNamesMessage(NickNamesMessage.fromJSON(message));
				case "NumOfPlayersMessage" -> manageNumOfPlayersMessage(NumOfPlayersMessage.fromJSON(message));
				case "OkMessage" -> manageOkMessage(new OkMessage());
				case "UpdateMessage" -> manageUpdateMessage(UpdateMessage.fromJSON(message));
				case "WrongMessage" -> manageWrongMessage(WrongMessage.fromJSON(message));
				case "ChatMessage" -> manageChatMessage(ChatMessage.fromJSON(message));
				default -> throw new InvalidMessageException();
			}
		else notReads.add(message);
	}

	/**
	 * Add a certain type of message to the interest list <br/>
	 * Then check if there are any unread messages of that type.
	 */
	public void addInterests(String type){
		if(!interests.contains(type)){
			interests.add(type);
			usefulCheck();
		}
	}
	
	/**
	 * Remove a certain type of message from the interest list
	 */
	public void removeInterests(String type){
		interests.remove(type);
	}
	
	/**
	 * Remove ALL the types of message from the interest list
	 */
	public void emptyInterests(){
		interests.clear();
		isAllInterested = false;
	}
	
	/**
	 * Set the messageManager interested in all message types
	 */
	public void setAllInterested(){
		isAllInterested = true;
	}
	
	/**
	 * Check if there are unread messages of our interest; if there are, it handles them
	 */
	public void usefulCheck(){
		for(int i = 0; i < notReads.size(); i++){
			JSONObject obj = notReads.get(i);
			if(interests.contains(obj.getString("type")) || isAllInterested){
				// If a message is of an invalid type, it will be ignored
				try{
					manage(obj);
				}catch(InvalidMessageException ignored){}
				notReads.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * @return the actual size of not read messages list
	 */
	public int notReadSize(){
		return notReads.size();
	}
	
	public void resetNotReads(){
		notReads.clear();
	}
	
	abstract protected void manageWrongMessage(@NotNull WrongMessage message);
	
	abstract protected void manageNumOfPlayersMessage(@NotNull NumOfPlayersMessage message) throws InvalidMessageException;
	
	abstract protected void manageGameDataMessage(@NotNull GameDataMessage message);
	
	abstract protected void manageFirstInfoMessage(@NotNull FirstInfoMessage message);
	
	abstract protected void manageNickNamesMessage(@NotNull NickNamesMessage message);
	
	abstract protected void manageNickNameMessage(@NotNull NickNameMessage message) throws InvalidMessageException;
	
	abstract protected void manageIDMessage(@NotNull IDMessage message);
	
	abstract protected void manageUpdateMessage(@NotNull UpdateMessage message) throws InvalidMessageException;
	
	abstract protected void manageFinalMessage(@NotNull FinalMessage message);
	abstract protected void manageOkMessage(@NotNull OkMessage message);

	abstract protected void manageChatMessage(@NotNull ChatMessage fromJSON);

}
