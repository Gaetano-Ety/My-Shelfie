package networkMessages.messages;

import org.json.JSONObject;

public class ChatMessage extends Message{
    private final int sender; // ID of the sender
    private final int recipient; // ID of the recipient, if it is -1, all the players are recipients
    private final String message;
    
    public ChatMessage(int sender, int recipient, String message){
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        type = "ChatMessage";
    }
    
    public int getSender(){
        return sender;
    }

    public int getRecipient() {
        return recipient;
    }

    public String getMessage(){
        return message;
    }

    @Override
    public JSONObject toJSON() {
        return new JSONObject().put("sender", sender).put("recipient", recipient).put("message", message).put("type", type);
    }

    public static ChatMessage fromJSON(JSONObject obj){
        return new ChatMessage(
            obj.getInt("sender"),
            obj.getInt("recipient"),
            obj.getString("message")
        );
    }
}
