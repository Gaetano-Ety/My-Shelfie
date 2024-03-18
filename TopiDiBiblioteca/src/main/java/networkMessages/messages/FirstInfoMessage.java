package networkMessages.messages;

import org.json.JSONObject;

/**
 * Thi class representing a message with the goals info and the players number
 */
public class FirstInfoMessage extends Message{
	private final int goal1, goal2, personalGoal, playersNumber;
	
	public FirstInfoMessage(int g1, int g2, int pG, int pn){
		goal1 = g1;
		goal2 = g2;
		personalGoal = pG;
		playersNumber = pn;
		type = "FirstInfoMessage";
	}
	
	public int getGoal1(){
		return goal1;
	}
	
	public int getGoal2(){
		return goal2;
	}
	
	public int getPersonalGoal(){
		return personalGoal;
	}
	
	public int getPlayersNumber(){return playersNumber;}
	
	@Override
	public JSONObject toJSON(){
		return new JSONObject(this);
	}
	
	public static FirstInfoMessage fromJSON(JSONObject obj){
		return new FirstInfoMessage(obj.getInt("goal1"), obj.getInt("goal2"), obj.getInt("personalGoal"), obj.getInt("playersNumber"));
	}
}
