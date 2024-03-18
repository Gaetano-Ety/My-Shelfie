package networkMessages.messages;

import org.json.JSONArray;
import org.json.JSONObject;

public class FinalMessage extends Message{
	private final int[] scores;
	
	public FinalMessage(int[] scores){
		this.scores = scores;
		type = "FinalMessage";
	}
	
	public int[] getScores(){
		return scores.clone();
	}
	
	@Override
	public JSONObject toJSON(){
		return new JSONObject().put("scores", new JSONArray(scores)).put("type", type);
	}
	
	public static FinalMessage fromJSON(JSONObject obj){
		JSONArray arrOfInt = obj.getJSONArray("scores");
		
		int[] result = new int[arrOfInt.length()];
		for(int i = 0; i < arrOfInt.length(); i++)
			result[i] = arrOfInt.getInt(i);
		
		return new FinalMessage(result);
	}
}
