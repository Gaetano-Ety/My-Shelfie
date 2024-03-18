package networkMessages.messages;

import org.json.JSONObject;

public class NumOfPlayersMessage extends Message{
	private final int playersNumber;
	
	public NumOfPlayersMessage(int n){
		type = "NumOfPlayersMessage";
		playersNumber = n;
	}
	
	public int getPlayersNumber(){
		return playersNumber;
	}
	
	@Override
	public JSONObject toJSON(){
		return new JSONObject().put("type", type).put("playersNumber", playersNumber);
	}
	
	public static NumOfPlayersMessage fromJSON(JSONObject obj){
		return new NumOfPlayersMessage(obj.getInt("playersNumber"));
	}
	
}
