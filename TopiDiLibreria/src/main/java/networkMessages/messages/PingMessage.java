package networkMessages.messages;

import org.json.JSONObject;

public class PingMessage extends Message{
	public PingMessage(){
		type = "PingMessage";
	}

	@Override
	public JSONObject toJSON(){
		return new JSONObject("{\"type\":\"PingMessage\"}");
	}
}
