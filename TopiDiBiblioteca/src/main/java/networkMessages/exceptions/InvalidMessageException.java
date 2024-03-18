package networkMessages.exceptions;

public class InvalidMessageException extends Exception{
	private final String message;
	
	public InvalidMessageException(){
		this.message = "Invalid Message!";
	}
	
	public InvalidMessageException(String message){
		this.message = message;
	}
	
	@Override
	public String getMessage(){
		return message;
	}
}
