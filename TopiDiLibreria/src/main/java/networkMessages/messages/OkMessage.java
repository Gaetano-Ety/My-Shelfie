package networkMessages.messages;

import org.json.JSONObject;

public class OkMessage extends Message{
	public OkMessage(){
		type = "OkMessage";
	}
	
	@Override
	public JSONObject toJSON(){return new JSONObject(this);}
}
