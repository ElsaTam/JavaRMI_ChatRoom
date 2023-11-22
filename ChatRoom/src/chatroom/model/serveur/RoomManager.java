package chatroom.model.serveur;

import chatroom.model.common.interfaces.RoomInterface;
import chatroom.model.common.interfaces.RoomManagerInterface;
import chatroom.model.common.objects.Token;
import chatroom.model.exception.UnexistingRoomException;
import chatroom.model.exception.UserNotRegisteredException;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import static java.rmi.server.UnicastRemoteObject.unexportObject;
import java.rmi.server.Unreferenced;

/**
 * A room manager used by the room's creator to have more features
 */
public class RoomManager extends UnicastRemoteObject implements RoomManagerInterface, Unreferenced {

  private static final long serialVersionUID = -8126241497449743592L;
  private final Room room;

  /**
   * Constructor
   * @param room the dedicated room
   * @throws RemoteException threw if there is a problem with RMI
   */
  RoomManager(Room room) throws RemoteException {
    this.room = room;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteRoom(Token userToken) throws RemoteException, UserNotRegisteredException {
    if (userToken.getToken().equals(this.room.getManagerToken().getToken())) {
        this.room.destroyRoom();
    }
    unexportObject(this, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RoomInterface getRoom() throws RemoteException {
    return this.room;
  }


  /**
   * Unexport the object if it's not referenced anymore
   */
  @Override
  public void unreferenced() {
    try {
      unexportObject(this, true);
    } catch (NoSuchObjectException e) {
      e.printStackTrace();
    }
  }
}