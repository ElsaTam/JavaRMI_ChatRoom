package chatroom.model.common.interfaces;

import chatroom.model.common.objects.Data;
import chatroom.model.common.objects.Message;
import chatroom.model.common.objects.Token;
import chatroom.model.exception.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interface which represents a room to interact with clients
 */
public interface RoomInterface extends Remote {

  /**
   * Send a message to all users
   * @param userToken the sender's Token
   * @param message the message to send
   * @throws RemoteException threw if there is a problem with RMI
   * @throws UserNotRegisteredException threw if the user is not registered on the room
   */
  void sendMessage(Token userToken, Data message) throws RemoteException, UserNotRegisteredException;

  /**
   * Quit the room
   * @param userToken the user's Token
   * @throws RemoteException threw if there is a problem with RMI
   * @throws UserNotRegisteredException threw if the user is not registered on the room
   */
  void quit(Token userToken) throws RemoteException, UserNotRegisteredException;

  /**
   * Register a client to the room
   * @param client the user's listener
   * @param userToken the user's Token
   * @param psw the password of the room
   * @throws RemoteException threw if there is a problem with RMI
   * @throws AlreadyRegisteredToTheRoomException threw if the user is already registered on the room
   * @throws ChatRoomIsFullException threw if the room is full
   * @throws WrongPasswordException threw if the specified password is not correct
   */
  void registerClient(ThreadListenerInterface client, Token userToken, String psw) throws RemoteException, AlreadyRegisteredToTheRoomException, ChatRoomIsFullException, WrongPasswordException;

  /**
   * Get all messages which have not been already recovered
   * @param userToken the user's Token
   * @return a list which contains all new messages
   * @throws RemoteException threw if there is a problem with RMI
   * @throws UserNotRegisteredException threw if the user is not registered on the room
   */
  List<Message> getMessages(Token userToken) throws RemoteException, UserNotRegisteredException;

  /**
   * send a private message to the specified recipient
   * @param userToken the user's Token
   * @param dest the recipient's name
   * @param message the message to send
   * @throws RemoteException threw if there is a problem with RMI
   * @throws UserNotRegisteredException threw if the user is not registered on the room
   * @throws RecipientNotHereException threw if the recipient is not registered on the room
   */
  void sendPrivateMessage(Token userToken, String dest, Data message) throws RemoteException, UserNotRegisteredException, RecipientNotHereException;
}