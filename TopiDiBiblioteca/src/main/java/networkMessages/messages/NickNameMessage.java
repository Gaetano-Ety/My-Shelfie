package networkMessages.messages;

import org.json.JSONObject;

public class NickNameMessage extends Message{
	private final String nickName;
	private final int playerID;
	
	public NickNameMessage(String nick, int id){
		nickName = nick;
		playerID = id;
		type = "NickNameMessage";
	}
	
	public String getNickName(){
		return nickName;
	}
	
	public int getPlayerID(){
		return playerID;
	}
	
	@Override
	public JSONObject toJSON(){
		return new JSONObject().put("type", type).put("nickName", nickName).put("playerID", playerID);
	}
	
	public static NickNameMessage fromJSON(JSONObject obj){
		return new NickNameMessage(obj.getString("nickName"), obj.getInt("playerID"));
	}
}

