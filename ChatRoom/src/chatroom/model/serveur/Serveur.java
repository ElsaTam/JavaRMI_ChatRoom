package chatroom.model.serveur;

import chatroom.model.common.interfaces.RoomInterface;
import chatroom.model.common.interfaces.RoomManagerInterface;
import chatroom.model.common.interfaces.ServeurInterface;
import chatroom.model.common.objects.Token;
import chatroom.model.exception.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * The server which keep all registered users and rooms
 */
public class Serveur extends UnicastRemoteObject implements ServeurInterface {

  private static final long serialVersionUID = -463147130557881600L;
  private final List<String> pseudoList;
  private final Map<Long, String> tokenPseudoMap; //on utilise sous forme de Long car la recherche en utilisant Token ne fonctionnait malgre redifinition de equals et hashcode
  private final Map<String, Room> roomList;

  /**
   * Constructor
   * @throws RemoteException threw if there is problem with RMI
   */
  public Serveur() throws RemoteException {
    this.pseudoList = Collections.synchronizedList(new ArrayList<>());
    this.tokenPseudoMap = Collections.synchronizedMap(new HashMap<>());
    this.roomList = Collections.synchronizedMap(new HashMap<>());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RoomManagerInterface createPublicRoom(Token managerToken, String roomName, int maxUser) throws RemoteException, UserNotRegisteredException, RoomAlreadyExistingException {
    Long managerId = managerToken.getToken();
    if (!tokenPseudoMap.containsKey(managerId)){
      throw new UserNotRegisteredException();
    }
    if (roomList.containsKey(roomName)){
      throw new RoomAlreadyExistingException();
    }

    Room room = new Room(false, roomName, managerToken,this, maxUser, ""); // create the room for all users
    RoomManager roomMng = new RoomManager(room); // create the roomManager for the admin
    roomList.put(roomName, room); // register the room

    return roomMng;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RoomManagerInterface createPrivateRoom(Token managerToken, String roomName, int maxUser, String psw) throws RemoteException, UserNotRegisteredException, RoomAlreadyExistingException {
    Long managerId = managerToken.getToken();
    if (!tokenPseudoMap.containsKey(managerId)){
      throw new UserNotRegisteredException();
    }
    if (roomList.containsKey(roomName)){
      throw new RoomAlreadyExistingException();
    }

    Room room = new Room(true, roomName, managerToken,this, maxUser, psw); // create the room for all users
    RoomManager roomMng = new RoomManager(room); // create the roomManager for the admin
    roomList.put(roomName, room); // register the room

    return roomMng;  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getRooms() throws RemoteException {
    return new ArrayList<>(roomList.keySet());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Token connectUser(String pseudo) throws RemoteException, UserAlreadyRegisteredException {
    if(pseudoList.contains(pseudo)){
      throw new UserAlreadyRegisteredException();
    }
    Token userToken = generateToken();
    pseudoList.add(pseudo);
    tokenPseudoMap.put(userToken.getToken(), pseudo);

    return userToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RoomInterface connectToRoom(String roomName, Token userToken, String psw) throws RemoteException, UserNotRegisteredException, IncorrectRoomNameException, WrongPasswordException {
    if (!tokenPseudoMap.containsKey(userToken.getToken())){
      throw new UserNotRegisteredException();
    }
    if (!roomList.containsKey(roomName)){
      throw new IncorrectRoomNameException();
    }
    Room chosenRoom = roomList.get(roomName);
    if (chosenRoom.isPrivate() && !psw.equals(chosenRoom.getPassword())){
      throw new WrongPasswordException();
    }
    return chosenRoom;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void disconnectUser(Token userToken) throws RemoteException, UserNotRegisteredException {
    Long managerId = userToken.getToken();
    if(!tokenPseudoMap.containsKey(managerId)){
      throw new UserNotRegisteredException();
    }
    pseudoList.remove(tokenPseudoMap.get(managerId));
    tokenPseudoMap.remove(managerId);
  }

  /**
   * Remove the room from the List. The room should be cleared of any user
   * @param roomName the name of the room to remove
   * @throws UnexistingRoomException if the room doesn't exist or has already been deleted
   */
  void removeRoom(String roomName) throws UnexistingRoomException{
    if (!roomList.containsKey(roomName)){
      throw new UnexistingRoomException();
    }
    roomList.remove(roomName);
  }

  String getUserPseudo(Token userToken) {
    return tokenPseudoMap.get(userToken.getToken());
  }

  /**
   * Generate a token based on a pseudo
   * @return the token
   */
  private Token generateToken() {
    Random rand = new Random();
    Long id;
    do {
      id = rand.nextLong();
    } while(this.tokenPseudoMap.containsKey(id));
    return new Token(id);
  }
}
