package server.interfaces;

import org.json.JSONObject;

public interface Observable{
	void updateAllObservers();
	
	void broadcast(JSONObject obj);
	
	void attach(Observer observer);
	
	void remove(Observer observer);
}
