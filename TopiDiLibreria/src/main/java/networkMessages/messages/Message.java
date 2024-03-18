package networkMessages.messages;

import networkMessages.exceptions.InvalidMessageException;
import org.json.JSONObject;

public abstract class Message{
	String type, message = "";
	
	public Message(){
		type = "Message";
	}
	
	public Message(String str){
		message = str;
		type = "Message";
	}
	
	public String getType(){
		return type;
	}
	
	public abstract JSONObject toJSON();
	
	public static Message fromJSON(JSONObject obj) throws InvalidMessageException{
		return null;
	}
}
