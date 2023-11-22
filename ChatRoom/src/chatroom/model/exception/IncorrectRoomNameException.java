package chatroom.model.exception;

/**
 * Exception threw when the name of the specified room to the server doesn't match with any registered room on the server
 */
public class IncorrectRoomNameException extends ChatRoomException {
  private static final long serialVersionUID = -6951255906666169204L;

  /**
   * Constructor
   */
  public IncorrectRoomNameException() {
    super("This name doesn't match with any room's name");
  }
    
}
