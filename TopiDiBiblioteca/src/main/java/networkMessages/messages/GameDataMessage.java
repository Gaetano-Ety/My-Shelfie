package networkMessages.messages;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Stack;
import java.util.stream.IntStream;

public class GameDataMessage extends Message{
	private final String board;
	private final String[] bookshelves;
	private final Stack<Integer> scores1, scores2; //scores "left"
	private final int turn;
	
	public GameDataMessage(String actualBoard, String[] actualBookShelves, int actualTurn, Stack<Integer>[] stacks){
		board = actualBoard;
		bookshelves = actualBookShelves.clone();
		turn = actualTurn;
		scores1 = stacks[0];
		scores2 = stacks[1];
		type = "GameDataMessage";
	}
	
	public Stack<Integer> getScores1(){
		return scores1;
	}
	
	public Stack<Integer> getScores2(){
		return scores2;
	}
	
	public String getBoard(){
		return board;
	}
	
	public String[] getBookshelves(){
		return bookshelves;
	}
	
	public int getTurn(){
		return turn;
	}
	
	@Override
	public JSONObject toJSON(){
		return new JSONObject()
			.put("type", type)
			.put("board", board)
			.put("bookshelves", new JSONArray(bookshelves))
			.put("scores1", new JSONArray(scores1))
			.put("scores2", new JSONArray(scores2))
			.put("turn", turn);
	}
	
	public static GameDataMessage fromJSON(JSONObject obj){
		JSONArray bsJSON = obj.getJSONArray("bookshelves");
		
		String[] bs = new String[bsJSON.length()];
		for(int i = 0; i < bsJSON.length(); i++)
			bs[i] = bsJSON.getString(i);
		
		// Extract the stacks from the JSON object
		JSONArray stackJSON;
		Stack<Integer>[] stacks = new Stack[2];
		
		stacks[0] = new Stack<>();
		stackJSON = obj.getJSONArray("scores1");
		IntStream.range(0, stackJSON.length()).map(stackJSON::getInt).forEach(stacks[0]::add);
		
		stacks[1] = new Stack<>();
		stackJSON = obj.getJSONArray("scores2");
		IntStream.range(0, stackJSON.length()).map(stackJSON::getInt).forEach(stacks[1]::add);
		
		return new GameDataMessage(obj.getString("board"), bs, obj.getInt("turn"), stacks);
	}
}
