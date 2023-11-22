package chatroom.model.client;

import chatroom.model.client.printers.Printer;
import chatroom.model.common.interfaces.RoomInterface;
import chatroom.model.common.interfaces.RoomManagerInterface;
import chatroom.model.common.interfaces.ServeurInterface;
import chatroom.model.common.objects.DataFile;
import chatroom.model.common.objects.DataString;
import chatroom.model.common.objects.Token;
import chatroom.model.exception.*;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an user who communicates with the server and chatrooms
 */
public class Client implements Serializable {
  private static final long serialVersionUID = -1598894819362569432L;
  private Token userId;
  private final String pseudo;
  private final Map<String, ConnectedThread> roomThreads;
  private final ServeurInterface serv;


  /**
   * Create a client with a pseudo
   * @param name the pseudo of the client
   * @param server the server
   */
  public Client(String name, ServeurInterface server) {
    this.pseudo = name;
    this.serv = server;
    this.userId = null;
    this.roomThreads = new HashMap<>();
  }

  /**
   * Connect the client to the server
   * @throws RemoteException threw if there is a problem with RMI
   * @throws UserAlreadyRegisteredException threw when the user's name is already registered in the server
   */
  public void connectToServer() throws RemoteException, UserAlreadyRegisteredException {
      this.userId = this.serv.connectUser(this.pseudo);
  }

  /**
   * Connect the client to the chosen room
   * @param roomName the name of the room which the client want to be connected
   * @param print the printer use to display messages (in console or with ui)
   * @param psw the password of the room
   * @throws RemoteException threw if there is a problem with RMI
   * @throws IncorrectRoomNameException threw if the specified name doesn't match with any room registered on the server
   * @throws ChatRoomIsFullException threw if the specified room is full
   * @throws UserNotRegisteredException threw if the Token of the user doesn't match with any user's Token in the server
   * @throws AlreadyRegisteredToTheRoomException threw if the user is already connected to the room
   * @throws WrongPasswordException threw if the specified password is incorrect
   */
  public void connectToRoom(String roomName, Printer print, String psw) throws RemoteException, IncorrectRoomNameException, ChatRoomIsFullException, UserNotRegisteredException, AlreadyRegisteredToTheRoomException, WrongPasswordException {
    RoomInterface room = this.serv.connectToRoom(roomName, this.userId, psw);
    UserConnectedThread connected = new UserConnectedThread(this.userId, room, print, psw);
    Thread thread = new Thread(connected);
    thread.start();
    this.roomThreads.put(roomName, connected);

  }

  /**
   * Send a message in the specified room
   * @param roomName the name of the room where the user wants to send a message
   * @param text the message's content
   * @throws NotRegisteredToTheRoomException threw if the user is not registered on the room
   */
  public void sendMessage(String roomName, String text) throws NotRegisteredToTheRoomException {
    DataString message = new DataString(text, false);
    ConnectedThread chosenRoom = this.roomThreads.get(roomName);
    if (chosenRoom != null) {
      chosenRoom.sendMessage(message);
    } else {
      throw new NotRegisteredToTheRoomException();
    }
  }

  /**
   * The list of existing room
   * @return names of the created rooms
   * @throws RemoteException threw if there is a problem with RMI
   */
  public List<String> roomsList() throws RemoteException {
    return this.serv.getRooms();
  }

  /**
   * Create a public room
   * @param name the name of the room
   * @param print the printer use to display messages (in console or with ui)
   * @param maxUsers the number of users which can be connected to the room at the same time
   * @throws UserNotRegisteredException threw if the Token of the user doesn't match with any user's Token in the server
   * @throws RoomAlreadyExistingException threw if the specified name is not available in the server
   * @throws RemoteException threw if there is a problem with RMI
   */
  public void createPublicRoom(String name, Printer print, int maxUsers) throws UserNotRegisteredException, RoomAlreadyExistingException, RemoteException, WrongPasswordException, ChatRoomIsFullException, AlreadyRegisteredToTheRoomException {
    RoomManagerInterface manager = this.serv.createPublicRoom(this.userId, name, maxUsers);
    RoomInterface room = manager.getRoom();
    ManagerConnectedThread connected = new ManagerConnectedThread(this.userId, room, manager, print, "");
    Thread thread = new Thread(connected);
    thread.start();
    this.roomThreads.put(name, connected);
  }

  /**
   * Create a private room
   * @param name the name of the room
   * @param password the password of the room
   * @param maxUsers the number of users which can be connected to the room at the same time
   * @throws RemoteException threw if there is a problem with RMI
   * @throws UserNotRegisteredException threw if the user is not registered on the room
   * @throws RoomAlreadyExistingException threw if the specified name is not available in the server
   */
  public void createPrivateRoom(String name, String password, Printer print, int maxUsers) throws RemoteException, UserNotRegisteredException, RoomAlreadyExistingException, WrongPasswordException, ChatRoomIsFullException, AlreadyRegisteredToTheRoomException {
    RoomManagerInterface manager = this.serv.createPrivateRoom(this.userId, name, maxUsers, password);
    RoomInterface room = manager.getRoom();
    ManagerConnectedThread connected = new ManagerConnectedThread(this.userId, room, manager, print, password);
    Thread thread = new Thread(connected);
    thread.start();
    this.roomThreads.put(name, connected);
  }

  /**
   * Disconnect the user of the specified room, if the user was the creator of the room, it will delete it
   * @param roomName the name of the room
   * @throws NotRegisteredToTheRoomException threw if the user is not registered on the room
   */
  public void quitRoom(String roomName) throws NotRegisteredToTheRoomException {
    ConnectedThread chosenRoom = this.roomThreads.get(roomName);
    if (chosenRoom!=null) {
      chosenRoom.quit();
      this.roomThreads.remove(roomName);
    } else {
      throw new NotRegisteredToTheRoomException();
    }
  }

  /**
   * Disconnect the user of all rooms and then disconnect him of the server
   * @throws RemoteException threw if there is a problem with RMI
   * @throws UserNotRegisteredException threw if the user is not registered on the room
   */
  public void disconnect() throws RemoteException, UserNotRegisteredException {
    for (HashMap.Entry<String, ConnectedThread> entry : this.roomThreads.entrySet()) {
      entry.getValue().quit();
    }
    this.roomThreads.clear();
    this.serv.disconnectUser(this.userId);

  }

  /**
   * Send a private message to a chosen user of the specified room
   * @param roomName the name of the room
   * @param dest the recipient's name
   * @param text the content of the message
   * @throws NotRegisteredToTheRoomException threw if the user is not registered on the room
   */
  public void sendPrivateMessage(String roomName, String dest, String text) throws NotRegisteredToTheRoomException {
    DataString message = new DataString(text, true);
    ConnectedThread chosenRoom = this.roomThreads.get(roomName);
    if (chosenRoom != null) {
      chosenRoom.sendPrivateMessage(dest, message);
    } else {
      throw new NotRegisteredToTheRoomException();
    }
  }

  /**
   * Send a file in the specified room
   * @param roomName the name of the room
   * @param path the path of the file
   * @throws IOException threw if there is a problem with the specified file
   * @throws NotRegisteredToTheRoomException threw if the user is not registered on the room
   */
  public void sendFile(String roomName, String path) throws IOException, NotRegisteredToTheRoomException {
    DataFile file = new DataFile(path, false);
    ConnectedThread chosenRoom = this.roomThreads.get(roomName);
    if (chosenRoom != null) {
      chosenRoom.sendMessage(file);
    } else {
      throw new NotRegisteredToTheRoomException();
    }
  }

  /**
   * Send a file to a chosen user of the specified room
   * @param roomName the name of the room
   * @param dest the recipient's name
   * @param path the path of the file
   * @throws IOException threw if there is a problem with the specified file
   * @throws NotRegisteredToTheRoomException threw if the user is not registered on the room
   */
  public void sendPrivateFile(String roomName, String dest, String path) throws IOException, NotRegisteredToTheRoomException {
    DataFile file = new DataFile(path, true);
    ConnectedThread chosenRoom = this.roomThreads.get(roomName);
    if (chosenRoom != null) {
      chosenRoom.sendPrivateMessage(dest, file);
    } else {
      throw new NotRegisteredToTheRoomException();
    }
  }
}