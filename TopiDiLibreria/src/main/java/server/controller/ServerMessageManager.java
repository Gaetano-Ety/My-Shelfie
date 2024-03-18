package server.controller;

import model.exceptions.FullColumnException;
import model.exceptions.InvalidActionException;
import model.exceptions.InvalidPlayersNumberException;
import model.exceptions.InvalidStringExcepton;
import model.gameObjects.Board;
import model.gameObjects.BookShelf;
import model.gameObjects.SpotMatrix;
import networkMessages.MessageManager;
import networkMessages.exceptions.InvalidMessageException;
import networkMessages.exceptions.InvalidPlayerNickNameException;
import networkMessages.messages.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class ServerMessageManager extends MessageManager{
	private final ServerController controller;
	
	public ServerMessageManager(ServerController c){
		controller = c;
	}
	
	/**
	 * The first player must decide the number of players. <br/>
	 * If the chosen number is not valid, then an error message will be sent to the player and the reading of the number will be retried.
	 */
	@Override
	public void manageNumOfPlayersMessage(@NotNull NumOfPlayersMessage message) throws InvalidMessageException{
		try{
			controller.setNumberOfPlayers(message.getPlayersNumber());
		}catch(InvalidPlayersNumberException e){
			throw new InvalidMessageException("InvalidPlayersNumber");
		}
	}
	
	/**
	 * Set a nickname of a player
	 */
	@Override
	public void manageNickNameMessage(@NotNull NickNameMessage message) throws InvalidMessageException{
		try{
			controller.setNickName(message.getPlayerID(), message.getNickName());
		}catch(InvalidPlayerNickNameException e){
			throw new InvalidMessageException("InvalidPlayerNickName");
		}
	}
	
	/**
	 * Bring the cards from the board and then update bookshelves
	 */
	@Override
	public void manageUpdateMessage(@NotNull UpdateMessage message) throws InvalidMessageException{
		// Check if is a valid message
		if(!verifyUpdateMessage(message)) throw new InvalidMessageException("InvalidUpdate");
		
		JSONObject save = controller.getSavingOfGame();
		try{
			controller.putInBookshelf(message.getPlayerID(), message.getInputColumn(), message.getOrder());
			controller.takenCards(message.getCardsTaken());
			controller.nexTurn();
		}catch(FullColumnException | InvalidActionException e){
			try{
				controller.resumeAGame(save);
			}catch(InvalidStringExcepton ignored){/* Impossible */}
			throw new InvalidMessageException("InvalidUpdate");
		}
	}
	
	/**
	 * Check if the message given in input is a valid UpdateMessage
	 */
	private boolean verifyUpdateMessage(@NotNull UpdateMessage message){
		for(int[] coo : message.getCardsTaken()){
			if(coo[0] >= Board.getBoardLength() || coo[0] < 0) return false;
			if(coo[1] >= Board.getBoardLength() || coo[1] < 0) return false;
		}
		
		return SpotMatrix.areAdjacentSpots(message.getCardsTaken()) &&
			message.getInputColumn() >= 0 && message.getInputColumn() < BookShelf.getNumCols()
			&& message.getOrder().length <= 3
			&& message.getCardsTaken().length == message.getOrder().length
			&& message.getPlayerID() < controller.getPlayersNumber();
	}
	
	/*
	 *	IGNORED
	 *	These methods are not use by ServerMessageManager
	 */
	
	@Override
	public void manageWrongMessage(@NotNull WrongMessage message){/* TO DO */}
	
	@Override
	protected void manageOkMessage(@NotNull OkMessage message){/* Do nothing */}

	@Override
	protected void manageChatMessage(@NotNull ChatMessage message){
		controller.gameData.broadcast(message.toJSON());
	}

	@Override
	public void manageFirstInfoMessage(@NotNull FirstInfoMessage message){/* Do nothing */}
	
	@Override
	public void manageNickNamesMessage(@NotNull NickNamesMessage message){/* Do nothing */}
	
	@Override
	public void manageIDMessage(@NotNull IDMessage message){/* Do nothing */}
	
	@Override
	public void manageGameDataMessage(@NotNull GameDataMessage message){/* Do nothing */}
	
	@Override
	protected void manageFinalMessage(@NotNull FinalMessage message){/* Do nothing*/}
}
