package chatroom.model.client;

import chatroom.model.client.printers.Printer;
import chatroom.model.common.interfaces.RoomInterface;
import chatroom.model.common.objects.Token;
import chatroom.model.exception.*;

import java.rmi.RemoteException;

/**
 * Connected thread for an user which is not the manager of the room
 */
public class UserConnectedThread extends ConnectedThread {

  /**
   * Constructor
   * @param token the user's Token
   * @param dediedRoom the room's interface
   * @param print the printer used to get messages and exceptions
   * @param psw the password of the room
   * @throws RemoteException threw if there is a problem with RMI
   * @throws WrongPasswordException threw if the specified password is incorrect
   * @throws ChatRoomIsFullException threw if the room is full
   * @throws AlreadyRegisteredToTheRoomException threw if the user is already registered on the room
   */
  public UserConnectedThread(Token token, RoomInterface dediedRoom, Printer print, String psw) throws RemoteException, WrongPasswordException, ChatRoomIsFullException, AlreadyRegisteredToTheRoomException {
    super(token, dediedRoom, print, psw);
  }

  /**
   * Throw a NotAllowedException because an user cannot delete a room
   * @throws NotAllowedException always threw
   * @throws UserNotRegisteredException not threw
   * @throws RemoteException not threw
   */
  @Override
  protected final void delete() throws NotAllowedException, UserNotRegisteredException, RemoteException {
    throw new NotAllowedException();
  }
}
