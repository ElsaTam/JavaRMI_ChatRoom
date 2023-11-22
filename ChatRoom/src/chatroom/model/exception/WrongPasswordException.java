package chatroom.model.exception;


/**
 * Exception threw when the specified password is incorrect
 */
public class WrongPasswordException extends ChatRoomException {
  private static final long serialVersionUID = -3952099913740214946L;

  /**
   * Constructor
   */
  public WrongPasswordException(){
    super("It's not the good password");
  }
    
}
