package chatroom.model.exception;

/**
 * Exception threw when an user want to be connected to a room where he is already connected
 */
public class AlreadyRegisteredToTheRoomException extends ChatRoomException {

  private static final long serialVersionUID = -4341982412997969120L;

  /**
   * Constructor
   */
  public AlreadyRegisteredToTheRoomException() {
    super("This user is already connected to the room");
  }
}
