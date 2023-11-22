package chatroom.model.exception;

/**
 * Exception threw when the specified recipient is not registered
 */
public class RecipientNotHereException extends ChatRoomException {
  private static final long serialVersionUID = -3261085328649303198L;

  /**
   * Constructor
   */
  public RecipientNotHereException() {
    super("The recipient is not in this room");
  }
}
