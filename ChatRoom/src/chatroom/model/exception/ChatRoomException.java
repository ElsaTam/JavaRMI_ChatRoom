package chatroom.model.exception;

/**
 * Mother clas of our project's exceptions
 */
public class ChatRoomException extends Exception {
  private static final long serialVersionUID = 2884857545379061634L;

  /**
   * Constructor
   * @param msg the error's message
   */
  public ChatRoomException(String msg) {
    super(msg);
  }
}
