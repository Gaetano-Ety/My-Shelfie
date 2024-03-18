package networkMessages.messages;

import org.json.JSONObject;

public class IDMessage extends Message{
	private final int ID; // ID of the player
	
	public IDMessage(int n){
		type = "IDMessage";
		ID = n;
	}
	
	public int getID(){
		return ID;
	}
	
	@Override
	public JSONObject toJSON(){
		return new JSONObject().put("ID", ID).put("type", type);
	}
	
	public static IDMessage fromJSON(JSONObject obj){
		return new IDMessage(obj.getInt("ID"));
	}
}
