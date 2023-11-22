package chatroom.model.serveur;

import chatroom.model.common.interfaces.RoomInterface;
import chatroom.model.common.interfaces.ThreadListenerInterface;
import chatroom.model.common.objects.Data;
import chatroom.model.common.objects.DataString;
import chatroom.model.common.objects.Message;
import chatroom.model.common.objects.Token;
import chatroom.model.exception.*;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import static java.rmi.server.UnicastRemoteObject.unexportObject;
import java.rmi.server.Unreferenced;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class which represents a chat room
 */
public class Room extends UnicastRemoteObject implements RoomInterface, Unreferenced {

  private static final long serialVersionUID = -6790995080222128753L;
  private final boolean isPrivateRoom;
  private final Map<Long, ClientInfo> clientsMap;
  private final List<Message> messages;
  private final String name;
  private final Serveur serveur;
  private final Token managerToken;
  private final int capacity;
  private AtomicInteger lastMsg;
  private final String password;

  /**
   * Private class which represent a client in the room
   */
  private class ClientInfo {
    private final String pseudo;
    private final ThreadListenerInterface listener;
    private AtomicInteger lastMsg;
    private final List<Message> privatesMsg;

    /**
     * Constructor
     * @param name the name of the user
     * @param listen its listener
     * @param msg the message's index
     */
    private ClientInfo(String name, ThreadListenerInterface listen, int msg) {
      this.pseudo = name;
      this.listener = listen;
      this.lastMsg = new AtomicInteger(msg);
      this.privatesMsg = Collections.synchronizedList(new ArrayList<>());
    }
  }

  /**
   * Constructor
   * @param isPrivateRoom true if the room needs a password, false otherwise
   * @param name the name of the room
   * @param manager the token of the creator
   * @param serv the server
   * @param cap the maximum number of user which can be in the room at the same time
   * @param psw the password of the room (not used if the room is public)
   * @throws RemoteException if there is a problem with RMI
   */
  Room(boolean isPrivateRoom, String name, Token manager, Serveur serv, int cap, String psw) throws RemoteException {
    this.isPrivateRoom = isPrivateRoom;
    this.lastMsg = new AtomicInteger();
    this.clientsMap = Collections.synchronizedMap(new HashMap<>());
    this.messages = Collections.synchronizedList(new ArrayList<>());
    this.managerToken = manager;
    this.name = name;
    this.serveur = serv;
    this.capacity = cap;
    this.password = psw;
  }


  /**
   * Getter
   * @return the manager's Token
   */
  Token getManagerToken() {
    return managerToken;
  }

  /**
   * Getter
   * @return the room's password
   */
  String getPassword() {
    return password;
  }

  /**
   * {@inheritDoc}
   */
    @Override
    public void sendMessage(Token userToken, Data message) throws RemoteException, UserNotRegisteredException {
        Long userId = userToken.getToken();
        if (!this.clientsMap.containsKey(userId)){
            throw new UserNotRegisteredException();
        }
        String userPseudo = this.clientsMap.get(userId).pseudo;
        this.messages.add(new Message(userPseudo, message));
        this.lastMsg.getAndIncrement();
        synchronized (this.clientsMap) {
            for (HashMap.Entry<Long, ClientInfo> entry : this.clientsMap.entrySet()) {
                if (! entry.getKey().equals(userId))
                    entry.getValue().listener.send();
            }
        }
    }

  /**
   * {@inheritDoc}
   */
  @Override
  public void quit(Token userToken) throws RemoteException, UserNotRegisteredException {
    Long userId = userToken.getToken();
    if (!this.clientsMap.containsKey(userId)){
      throw new UserNotRegisteredException();
    }
    if(userId.equals(managerToken.getToken())){ // if the manager has been disconnected, the room is destroyed
        destroyRoom();
    } else {
      ClientInfo info = this.clientsMap.remove(userId); // first the pseudo is removed
      info.listener.quit();
    }
    if (this.clientsMap.size()==0) {
      unexportObject(this, true);
    }
  }

  /**
   * {@inheritDoc}
   */
  void destroyRoom() throws RemoteException, UserNotRegisteredException {
    try {
      this.serveur.removeRoom(this.name); // the room is removed from the serveur
    } catch (UnexistingRoomException ignored) { //if the room is not register to the server, the destruction must continue
    }
    this.sendMessage(managerToken, new DataString("La ChatRoom va etre detruite", false)); // the message is sent, all the users are notified
    synchronized (this) {
      try {
        this.wait(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    synchronized (this.clientsMap) {
      for (HashMap.Entry<Long, ClientInfo> entry : this.clientsMap.entrySet()) {
        if (entry.getKey().equals(this.managerToken.getToken())) {
          entry.getValue().listener.quit();
        } else {
          entry.getValue().listener.delete();
        }
      }
    }
  }

  /**
   * Getter
   * @return true if the room is private, false otherwise
   */
  public boolean isPrivate() {
    return this.isPrivateRoom;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void registerClient(ThreadListenerInterface client, Token userToken, String psw) throws RemoteException, AlreadyRegisteredToTheRoomException, ChatRoomIsFullException, WrongPasswordException {
    Long userId = userToken.getToken();
    if (this.clientsMap.containsKey(userId)){
      throw new AlreadyRegisteredToTheRoomException();
    }
    if (!psw.equals(password)){
      throw new WrongPasswordException();
    }
    if (this.clientsMap.size() >= capacity){
      throw new ChatRoomIsFullException();
    }
    this.clientsMap.put(userId, new ClientInfo(serveur.getUserPseudo(userToken), client, this.lastMsg.get())); // the threadListener is registered
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Message> getMessages(Token userToken) throws RemoteException, UserNotRegisteredException {
    Long userId = userToken.getToken();
    if (!this.clientsMap.containsKey(userId)){
      throw new UserNotRegisteredException();
    }
    List<Message> msgs = new ArrayList<>();
    ClientInfo info = this.clientsMap.get(userId);
    if (info.lastMsg.get()<this.lastMsg.get()) {
      synchronized (this.messages) {
        for (int i=info.lastMsg.get(); i<this.lastMsg.get(); i++) {
          msgs.add(this.messages.get(i));
        }
      }
      info.lastMsg.set(this.lastMsg.get());
    }
    synchronized (info.privatesMsg) {
      msgs.addAll(info.privatesMsg);
      info.privatesMsg.clear();
    }
    return msgs;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendPrivateMessage(Token userToken, String dest, Data message) throws UserNotRegisteredException, RecipientNotHereException, RemoteException {
    Long userId = userToken.getToken();
    if (!this.clientsMap.containsKey(userId)){
      throw new UserNotRegisteredException();
    }

    ClientInfo senderInfo = this.clientsMap.get(userId);
    boolean find = false;
    Iterator<Map.Entry<Long, ClientInfo>> ite = this.clientsMap.entrySet().iterator();
    synchronized (this.clientsMap) {
      while (ite.hasNext() && !find) {
        Map.Entry<Long, ClientInfo> curr = ite.next();
        ClientInfo currClient = curr.getValue();
        if (currClient.pseudo.equals(dest)) {
          find = true;
          Message mess = new Message(senderInfo.pseudo, message);
          senderInfo.privatesMsg.add(mess);
          currClient.privatesMsg.add(mess);
          //senderInfo.listener.send();
          currClient.listener.send();
        }
      }
      if (!find) {
        throw new RecipientNotHereException();
      }
    }


  }

  /**
   * Delete the room if it's not referenced anymore
   */
  @Override
  public void unreferenced() {
    try {
      this.serveur.removeRoom(this.name); // the room is removed from the server
    } catch (UnexistingRoomException e) {
      e.printStackTrace();
    }
    try {
      unexportObject(this, true);
    } catch (NoSuchObjectException e) {
      e.printStackTrace();
    }
  }
}