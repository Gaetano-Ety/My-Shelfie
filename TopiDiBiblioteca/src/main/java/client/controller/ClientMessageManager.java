package client.controller;

import client.view.cli.CliMethods;
import client.view.cli.UserInterfaceCli;
import client.virtualModel.GameData;
import model.exceptions.InvalidPlayersNumberException;
import model.exceptions.InvalidStringExcepton;
import model.exceptions.NotAvailableGoalException;
import model.goals.CommonGoalFactory;
import networkMessages.MessageManager;
import networkMessages.messages.*;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

import static model.gameObjects.SpotMatrix.countNotVoidElement;

/**
 * CLIENT MESSAGE MANAGER
 * this class extends message manager, implements all its abstract methods
 * recognizes the type of message, if it is a message of interest, it changes the view properly
 */
public class ClientMessageManager extends MessageManager{
	private final ClientController controller;
	
	public ClientMessageManager(ClientController clientController){
		this.controller = clientController;
	}
	
	/**
	 * ID MESSAGE
	 * Sets ID
	 *
	 * @param message IDMessage
	 */
	@Override
	public void manageIDMessage(@NotNull IDMessage message){
		controller.gameData.setID(message.getID());
		wakeUpController();
	}
	
	/**
	 * OTHER NICK NAMES
	 * Sets array of other nicknames
	 * @param message NickNamesMessage
	 */
	@Override
	public void manageNickNamesMessage(@NotNull NickNamesMessage message){
		controller.gameData.setNickNames(message.getNickNames());
	}
	
	/**
	 * WRONG MESSAGE
	 * holds the client in a certain state (e.g. get the nickname from player), or closes the connection, depending on the type of wrong message
	 * @param message WrongMessage
	 */
	@Override
	public void manageWrongMessage(@NotNull WrongMessage message){
		String problem = message.getMessage();
		switch(problem){
			case "InvalidPlayerNickName" -> {
				controller.userInterface.tellError(4);
				controller.sendNicknameMessage();
			}
			case "InvalidUpdate" -> {
				if(controller.userInterface.getClass() == UserInterfaceCli.class)
					controller.sendUpdateMessage();
			}
			case "ServerFullOfPlayer" -> controller.stopGame(3);
			default -> controller.stopGame(2);
		}
	}
	
	/**
	 * OK Message
	 *
	 * @param message OkMessage
	 */
	@Override
	public void manageOkMessage(@NotNull OkMessage message){
		controller.waitingState++;
		wakeUpController();
	}
	
	/**
	 * CHAT MESSAGE
	 * Updates the chat if the receiver is already in it (for the sender point of view too)
	 * Notifies the player if he is not currently in the chat
	 * @param message is the message
	 */
	@Override
	protected void manageChatMessage(@NotNull ChatMessage message){
		int sender = message.getSender();
		int recipient = message.getRecipient();
		if(recipient == controller.gameData.getMyID() || recipient == -1 || sender == controller.gameData.getMyID()){
			controller.gameData.updatePlayersChat(sender, recipient, message.getMessage());
			if (sender!=controller.gameData.getMyID()) {
				if((controller.inChat(sender)&&recipient!=-1) || (recipient==-1 && controller.inChat(recipient))) controller.showNewMessage(sender, message.getMessage());
				else controller.tellMessage(sender, recipient, controller.openedChats()!=0);
			}
		}
		controller.updateObserverGUI();
	}
	
	/**
	 * FIRST INFO
	 * Sets all the info about the game in the view (goals, board)
	 * @param message FirstInfoMessage
	 */
	@Override
	public void manageFirstInfoMessage(@NotNull FirstInfoMessage message){
		try{
			controller.gameData.setNumOfPlayer(message.getPlayersNumber());
			
			// Set goals and scores
			controller.gameData.setGoals(message.getGoal1(), message.getGoal2());
			Stack<Integer> scores = CommonGoalFactory.StackFactory.makeStack(message.getPlayersNumber());
			controller.gameData.updateScores(scores, scores);
			controller.gameData.setPersonalGoal(message.getPersonalGoal());
		}catch(NotAvailableGoalException | InvalidPlayersNumberException e){
			// Do nothing, should never ever happen
		}
		
		controller.waitingState++;
		wakeUpController();
	}
	
	/**
	 * GAME DATA
	 * updates the state of the game in the view (turn, bookshelf)
	 * @param message GameDataMessage
	 */
	@Override
	public void manageGameDataMessage(@NotNull GameDataMessage message){
		boolean firstRound = controller.gameData.getTurn() < 0;
		
		// In the first round the goals are not reached, of course
		// After the firstInfoMessage, the stack of goals is declared with the initial values
		// If it is the first round but gameDataMessage arrived before firstInfoMessage, this "if" saves from exception
		if(!firstRound)
			try{
				if(controller.gameData.getGoalScores(1).size() > message.getScores1().size())
					controller.userInterface.tellGoalAchieved(controller.gameData.getTurn(), 1);
				if(controller.gameData.getGoalScores(2).size() > message.getScores2().size())
					controller.userInterface.tellGoalAchieved(controller.gameData.getTurn(), 2);
			}catch(NotAvailableGoalException e){
				throw new RuntimeException(e);
			}
		
		controller.gameData.setTurn(message.getTurn());
		controller.gameData.updateScores(message.getScores1(), message.getScores2());
		try{
			controller.gameData.updateBoard(message.getBoard());
			controller.gameData.updateBookShelves(message.getBookshelves());
		}catch(InvalidStringExcepton e){
			throw new RuntimeException(e);
		}
		
		if(!firstRound && controller.userInterface.getClass() == UserInterfaceCli.class){
			((UserInterfaceCli) controller.userInterface).printBoard();
			if(countNotVoidElement(controller.gameData.getBookShelf(controller.gameData.getMyID())) > 0)
				CliMethods.printMat(controller.gameData.getBookShelf(controller.gameData.getMyID()));
			((UserInterfaceCli) controller.userInterface).printActualTurn();
		}
		
		if(controller.getWaitingState() < (controller.gameData.getMyID() == 0 ? 4 : 3)){
			controller.waitingState++;
			wakeUpController();
		}
		
		controller.updateObserverGUI();
	}
	
	/**
	 * FINAL MESSAGE
	 * Reports all the scores to the view
	 * @param message FinalMessage
	 */
	@Override
	public void manageFinalMessage(@NotNull FinalMessage message){
		controller.userInterface.gameEnd(GameData.createRanking(message.getScores()));
		controller.stopGame(0);
	}
	
	@Override
	protected void manageUpdateMessage(@NotNull UpdateMessage message){/*DO NOTHING*/}
	
	@Override
	protected void manageNickNameMessage(@NotNull NickNameMessage message){/*DO NOTHING*/}
	
	@Override
	protected void manageNumOfPlayersMessage(@NotNull NumOfPlayersMessage message){/*DO NOTHING*/}
	
	/**
	 * Method that notifies the controller that a step was taken towards the start of the game
	 */
	private void wakeUpController(){
		synchronized(controller){
			controller.notify();
		}
	}
}