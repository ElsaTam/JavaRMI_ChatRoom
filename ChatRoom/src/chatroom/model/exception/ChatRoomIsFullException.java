package chatroom.model.exception;

/**
 * Exception threw when a room reached its maximum capacity
 */
public class ChatRoomIsFullException extends ChatRoomException {
  private static final long serialVersionUID = 7109801554181307952L;

  /**
   * Constructor
   */
  public ChatRoomIsFullException() {
    super("This chatroom is full");
  }
    
}