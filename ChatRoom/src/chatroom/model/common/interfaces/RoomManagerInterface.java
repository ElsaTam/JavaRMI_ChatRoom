package chatroom.model.common.interfaces;

import chatroom.model.common.objects.Token;
import chatroom.model.exception.UserNotRegisteredException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface used to represent a manager of a room, it allows other features to a specific user
 */
public interface RoomManagerInterface extends Remote {

  /**
   * Delete the room
   * @param userToken the manager's Token
   * @throws RemoteException threw if there is a problem with RMI
   * @throws UserNotRegisteredException threw if the user is not registered on the server
   */
  void deleteRoom(Token userToken) throws RemoteException, UserNotRegisteredException;

  /**
   * Getter on the room
   * @return the room's interface
   * @throws RemoteException threw if there is a problem with RMI
   */
  RoomInterface getRoom() throws RemoteException;

}