package chatroom.model.client;

import chatroom.model.client.printers.Printer;
import chatroom.model.common.interfaces.RoomInterface;
import chatroom.model.common.interfaces.RoomManagerInterface;
import chatroom.model.common.objects.Token;
import chatroom.model.exception.*;

import java.rmi.RemoteException;

/**
 * Connected thread used if the user is the manager of the room
 */
public class ManagerConnectedThread extends ConnectedThread {
  private  final RoomManagerInterface manager;

  /**
   * The constructor
   * @param token the token of the manager
   * @param dediedRoom the dedicated room of the thread
   * @param manager the manager of the room
   * @param print the printer used to get messages
   * @param psw the password of the room
   * @throws RemoteException threw if there is a problem with RMI
   * @throws WrongPasswordException threw if the specified password is incorrect
   * @throws ChatRoomIsFullException threw if the room is full
   * @throws AlreadyRegisteredToTheRoomException threw if the user is already connected to the room
   */
  public ManagerConnectedThread(Token token, RoomInterface dediedRoom, RoomManagerInterface manager, Printer print, String psw) throws RemoteException, WrongPasswordException, ChatRoomIsFullException, AlreadyRegisteredToTheRoomException {
    super(token, dediedRoom, print, psw);
    this.manager = manager;
  }

  /**
   * Delete the room
   * @throws NotAllowedException not threw in this implementation
   * @throws UserNotRegisteredException threw if the user is not connected to the room
   * @throws RemoteException threw if there is a problem with RMI
   */
  @Override
  protected final void delete() throws NotAllowedException, UserNotRegisteredException, RemoteException {
    this.manager.deleteRoom(super.getUser());
  }
}
