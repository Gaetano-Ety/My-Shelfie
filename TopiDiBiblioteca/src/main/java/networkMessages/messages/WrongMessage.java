package networkMessages.messages;

import org.json.JSONObject;

public class WrongMessage extends Message{
	public WrongMessage(String message){
		super(message);
		type = "WrongMessage";
	}
	
	public WrongMessage(){
		type = WrongMessage.class.getSimpleName();
	}
	
	public String getMessage(){
		return message;
	}
	
	public JSONObject toJSON(){
		return new JSONObject(
			"{\"type\":\"WrongMessage\"," +
				"\"message\":\"" + message + "\"}"
		);
	}
	
	public static WrongMessage fromJSON(JSONObject obj){
		if(obj.has("message")) return new WrongMessage(obj.getString("message"));
		return new WrongMessage();
	}
}
