package chatroom.model.exception;

/**
 * Exception threw when an user wants to interact with a room which he is not registered
 */
public class NotRegisteredToTheRoomException extends ChatRoomException {
  private static final long serialVersionUID = 1908856479745152995L;

  /**
   * Constructor
   */
  public NotRegisteredToTheRoomException() {
    super("You are not connected to this room");
  }
}
