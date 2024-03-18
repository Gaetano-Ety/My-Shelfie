package networkMessages.messages;

import networkMessages.exceptions.InvalidMessageException;
import org.json.JSONArray;
import org.json.JSONObject;
import model.Color;
import model.exceptions.InvalidColorException;

public class UpdateMessage extends Message{
	private final int playerID, inputColumn;
	private final int[][] cardsTaken;
	private final Color[] order;
	
	public UpdateMessage(int id, int[][] taken, int column, Color[] ord){
		playerID = id;
		inputColumn = column;
		cardsTaken = taken;
		order = ord.clone();
		type = "UpdateMessage";
	}
	
	public int[][] getCardsTaken(){
		return cardsTaken;
	}
	
	public int getInputColumn(){
		return inputColumn;
	}
	
	public int getPlayerID(){
		return playerID;
	}
	
	public Color[] getOrder(){
		return order;
	}
	
	public JSONObject toJSON(){
		JSONObject out = new JSONObject()
			.put("type", type)
			.put("playerID", playerID)
			.put("inputColumn", inputColumn)
			.put("cardsTaken", new JSONArray(cardsTaken));
		
		for (Color c : order) out.append("order", c.toString());
		
		return out;
	}
	
	public static UpdateMessage fromJSON(JSONObject obj) throws InvalidMessageException{
		int id = obj.getInt("playerID");
		int column = obj.getInt("inputColumn");
		
		JSONArray ctJSON = obj.getJSONArray("cardsTaken");
		int[][] cardsTaken = new int[ctJSON.length()][2];
		for(int i = 0; i < ctJSON.length(); i++){
			JSONArray arr = ctJSON.getJSONArray(i);
			cardsTaken[i][0] = arr.getInt(0);
			cardsTaken[i][1] = arr.getInt(1);
		}
		
		JSONArray colorsJSON = obj.getJSONArray("order");
		Color[] order = new Color[colorsJSON.length()];
		try{
			for(int i = 0; i < colorsJSON.length(); i++)
				order[i] = Color.colorLike(colorsJSON.getString(i));
		}catch(InvalidColorException e){
			throw new InvalidMessageException("InvalidColor");
		}
		
		return new UpdateMessage(id, cardsTaken, column, order);
	}
	
}
