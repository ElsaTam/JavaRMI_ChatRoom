package chatroom.model.exception;

/**
 * Exception threw when an user tries a forbidden interaction
 */
public class NotAllowedException extends ChatRoomException {

  private static final long serialVersionUID = -2893308642588616141L;

  /**
   * Constructor
   */
  public NotAllowedException() {
    super("You are not allowed to do this");
  }
}
