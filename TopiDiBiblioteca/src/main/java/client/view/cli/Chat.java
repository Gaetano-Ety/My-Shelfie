package client.view.cli;

import client.controller.ClientController;
import client.virtualModel.GameData;

import java.util.Objects;

/**
 * Class that contains all the method to interact with the chat
 */
public class Chat {
    private final UserInterfaceCli ui;
    private final ClientController controller;
    private final GameData gameData;

    public Chat(UserInterfaceCli ui, ClientController controller, GameData gameData){
        this.ui = ui;
        this.controller = controller;
        this.gameData = gameData;
    }

    /**
     * Prints a chat (5 last messages)
     *
     * @param id the id of the player's chat, -1 to print the game chat
     */
    public void readPlayerChat(int id) {
        controller.setInChat(id);
        synchronized (System.out) {
            for(int i = 0; i < controller.getGameData().getPlayerChat(id).size(); i++)
                System.out.println(controller.getGameData().getPlayerChat(id).get(i));
        }
        controller.removeFromChat(id);
    }

    /**
     * Prints a chat from the beginning
     *
     * @param id the id of the player's chat, -1 to print the game chat
     */
    public void readCompletePlayerChat(int id) {
        controller.setInChat(id);
        synchronized (System.out) {
            for(int i = 0; i < controller.getGameData().getCompletePlayerChat(id).size(); i++)
                System.out.println(controller.getGameData().getCompletePlayerChat(id).get(i));
        }
        controller.removeFromChat(id);
    }

    /**
     * Allows the player to write in the game chat and shows the last five messages in the chat,
     * but it is possible to expand the chat. when the chat is closed, the board etc. are shown.
     */
    public void gameChat() {
        controller.setInChat(-1);
        System.out.println("You are now in the game chat, (insert - to exit, + to see the chat from the beginning)");
        readPlayerChat(-1);
        String comment = "";
        while (!Objects.equals(comment, "-")) {
            comment = ui.getInput();
            if(!controller.isRunning()) return;
            if (Objects.equals(comment, "+")) readCompletePlayerChat(-1);
            else if (!Objects.equals(comment, "-")) controller.createChatMessage(comment, -1);
        }
        controller.removeFromChat(-1);
        System.out.println("You have logged out of the game chat");
        ui.printBoard();
        System.out.println("This is your bookshelf");
        CliMethods.printMat(gameData.getBookShelf(gameData.getMyID()));
        ui.printActualTurn();
    }

    /**
     * Allows the player to chat with someone in the game and shows the last five messages in the chat,
     * but it is possible to expand the chat. when the chat is closed, the board etc. are shown.
     * @param id id of the chosen player
     */
    public void playerChat(int id) {
        controller.setInChat(id);
        System.out.println("You are now chatting with " + controller.getGameData().getNickName(id) + " (insert - to exit, + to see the chat from the beginning)");
        readPlayerChat(id);
        String comment = "";
        while (!Objects.equals(comment, "-")) {
            comment = ui.getInput();
            if(!controller.isRunning()) return;
            if (Objects.equals(comment, "+")) readCompletePlayerChat(id);
            else if (!Objects.equals(comment, "-")) controller.createChatMessage(comment, id);
        }
        controller.removeFromChat(id);
        System.out.println("You have logged out of the chat with " + controller.getGameData().getNickName(id));
        ui.printBoard();
        System.out.println("This is your bookshelf");
        CliMethods.printMat(gameData.getBookShelf(gameData.getMyID()));
        ui.printActualTurn();
    }
}