package networkMessagesTests;

import networkMessages.MessageManager;
import networkMessages.exceptions.InvalidMessageException;
import networkMessages.messages.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class MessageManagerTests{
	static final HashMap<String, Boolean> result = new HashMap<>();
	private final MyManager manager = new MyManager();
	
	private final JSONObject[] messages = {
		new JSONObject("{\"type\":\"FirstInfoMessage\", \"goal1\":2, \"goal2\":12, \"personalGoal\":2, \"playersNumber\":2}"),
		new JSONObject("{\"type\":\"FinalMessage\", \"scores\":[2, 4, 6]}"),
		new JSONObject("{\"type\":\"GameDataMessage\", \"board\":\"BBB/\", \"bookshelves\":[\"BBB/\",\"BBB/\"], \"scores1\":[2, 4, 6], \"scores2\":[2, 4, 6], \"turn\":1}"),
		new JSONObject("{\"type\":\"IDMessage\", \"ID\":2}"),
		new JSONObject("{\"type\":\"NickNameMessage\", \"playerID\": 1, \"nickName\":\"Banana\"}"),
		new JSONObject("{\"type\":\"NickNamesMessage\", \"nickNames\":[\"Banana1\",\"Banana2\"]}"),
		new JSONObject("{\"type\":\"NumOfPlayersMessage\", \"playersNumber\":2}"),
		new JSONObject("{\"type\":\"OkMessage\"}"),
		new JSONObject("{\"type\":\"UpdateMessage\", \"playerID\":1, \"inputColumn\":2, \"cardsTaken\":[[1,2], [2,2], [4,5]], \"order\":[\"B\", \"G\", \"T\"]}"),
		new JSONObject("{\"type\":\"WrongMessage\", \"message\":\"banana\"}")
	};
	
	/**
	 * Test if all the message are passing through the manager
	 */
	@Test
	public void readAllMessage(){
		initializeResult();
		manager.setAllInterested();
		
		try{
			for(JSONObject message : messages){
				System.out.println("Manage test: " + message.getString("type"));
				manager.manage(message);
				assertTrue(result.get(message.getString("type")));
			}
		}catch(InvalidMessageException e){
			fail();
		}
	}
	
	/**
	 * Test if all the message are passing through the manager after an indeterminate time
	 */
	@Test
	public void readLaterAllMessage(){
		initializeResult();
		manager.emptyInterests();
		
		try{
			for(JSONObject message : messages){
				manager.manage(message);
				assertFalse(result.get(message.getString("type")));
			}
			
			manager.setAllInterested();
			manager.usefulCheck();
			
			for(JSONObject message : messages)
				assertTrue(result.get(message.getString("type")));
			
			assertEquals(0, manager.notReadSize());
		}catch(InvalidMessageException e){
			fail();
		}
	}
	
	private void initializeResult(){
		for(JSONObject obj : messages)
			result.put(obj.getString("type"), false);
	}
}

/**
 * Message Manager created for the test - Every method set a specific static variable to true
 */
class MyManager extends MessageManager{
	
	@Override
	protected void manageWrongMessage(@NotNull WrongMessage message){
		MessageManagerTests.result.replace(message.getType(), true);
	}
	
	@Override
	protected void manageNumOfPlayersMessage(@NotNull NumOfPlayersMessage message){
		MessageManagerTests.result.replace(message.getType(), true);
	}
	
	@Override
	protected void manageGameDataMessage(@NotNull GameDataMessage message){
		MessageManagerTests.result.replace(message.getType(), true);
	}
	
	@Override
	protected void manageFirstInfoMessage(@NotNull FirstInfoMessage message){
		MessageManagerTests.result.replace(message.getType(), true);
	}
	
	@Override
	protected void manageNickNamesMessage(@NotNull NickNamesMessage message){
		MessageManagerTests.result.replace(message.getType(), true);
	}
	
	@Override
	protected void manageNickNameMessage(@NotNull NickNameMessage message){
		MessageManagerTests.result.replace(message.getType(), true);
	}
	
	@Override
	protected void manageIDMessage(@NotNull IDMessage message){
		MessageManagerTests.result.replace(message.getType(), true);
	}
	
	@Override
	protected void manageUpdateMessage(@NotNull UpdateMessage message){
		MessageManagerTests.result.replace(message.getType(), true);
	}
	
	@Override
	protected void manageFinalMessage(@NotNull FinalMessage message){
		MessageManagerTests.result.replace(message.getType(), true);
	}
	
	@Override
	protected void manageOkMessage(@NotNull OkMessage message){
		MessageManagerTests.result.replace(message.getType(), true);
	}
	
	@Override
	protected void manageChatMessage(@NotNull ChatMessage message){
		MessageManagerTests.result.replace(message.getType(), true);
	}
}
