package server.interfaces;

import org.json.JSONObject;

public interface Observer{
	void update();
	
	void sendMessage(JSONObject obj);
}