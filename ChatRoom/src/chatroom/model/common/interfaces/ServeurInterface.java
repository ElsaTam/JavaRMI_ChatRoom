package chatroom.model.common.interfaces;

import chatroom.model.common.objects.Token;
import chatroom.model.exception.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interface which defines the client's interactions with the server
 */
public interface ServeurInterface extends Remote {

  /**
   * Create a public room
   * @param managerToken the manager's token
   * @param roomName the room's name
   * @param maxUser the capacity of user which can be in the room at the same time
   * @return a stub of the room's manager
   * @throws RemoteException threw if there is a problem with RMI
   * @throws UserNotRegisteredException threw if the user's Token is not registered on the server
   * @throws RoomAlreadyExistingException threw if the room's name is taken by an other room
   */
  RoomManagerInterface createPublicRoom(Token managerToken, String roomName, int maxUser) throws RemoteException, UserNotRegisteredException, RoomAlreadyExistingException;

  /**
   * Create a private room
   * @param managerToken the manager's token
   * @param roomName the room's name
   * @param maxUser the capacity of user which can be in the room at the same time
   * @param psw the password of the room
   * @return a stub of the room's manager
   * @throws RemoteException threw if there is a problem with RMI
   * @throws UserNotRegisteredException threw if the user's Token is not registered on the server
   * @throws RoomAlreadyExistingException threw if the room's name is taken by an other room
   */
  RoomManagerInterface createPrivateRoom(Token managerToken, String roomName, int maxUser, String psw) throws RemoteException, UserNotRegisteredException, RoomAlreadyExistingException;

  /**
   * Get all room's names registered in the server
   * @return the list of room's names
   * @throws RemoteException threw if there is a problem with RMI
   */
  List<String> getRooms() throws RemoteException;

  /**
   * Connect an user on the server
   * @param pseudo the user's name
   * @return an unique Token dedicated to the user
   * @throws RemoteException threw if there is a problem with RMI
   * @throws UserAlreadyRegisteredException threw if pseudo is taken by an other user
   */
  Token connectUser(String pseudo) throws RemoteException, UserAlreadyRegisteredException;

  /**
   * Connect an user on a room
   * @param roomName the room's name
   * @param userToken the user's Token
   * @param psw the room's password
   * @return a stub of the room
   * @throws RemoteException threw if there is a problem with RMI
   * @throws UserNotRegisteredException threw if there the user is not registered on the server
   * @throws IncorrectRoomNameException threw if the specified room's name doesn't match with any room registered on the server
   * @throws WrongPasswordException threw if the specified password is incorrect
   */
  RoomInterface connectToRoom(String roomName, Token userToken, String psw) throws RemoteException, UserNotRegisteredException, IncorrectRoomNameException, WrongPasswordException;

  /**
   * Disconnect an user of the server
   * @param userToken the user's Token
   * @throws RemoteException threw if there is a problem with RMI
   * @throws UserNotRegisteredException threw if the user is not registered on the server
   */
  void disconnectUser(Token userToken) throws RemoteException, UserNotRegisteredException;

}