package networkMessages.messages;

import org.json.JSONArray;
import org.json.JSONObject;

public class NickNamesMessage extends Message{
	private final String[] nickNames;
	
	public NickNamesMessage(String[] nicks){
		nickNames = nicks.clone();
		type = "NickNamesMessage";
	}
	
	public String[] getNickNames(){
		return nickNames.clone();
	}
	
	@Override
	public JSONObject toJSON(){
		return new JSONObject().put("nickNames", new JSONArray(nickNames)).put("type", type);
	}
	
	public static NickNamesMessage fromJSON(JSONObject obj){
		JSONArray arrOfNicks = obj.getJSONArray("nickNames");
		
		String[] result = new String[arrOfNicks.length()];
		for(int i = 0; i < arrOfNicks.length(); i++)
			result[i] = arrOfNicks.getString(i);
		
		return new NickNamesMessage(result);
	}
}
