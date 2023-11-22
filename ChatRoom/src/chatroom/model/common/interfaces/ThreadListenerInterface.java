package chatroom.model.common.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The listener which is used to know when a message is sent or if the room will be deleted
 */
public interface ThreadListenerInterface extends Remote {

  /**
   * Callback used to quit a room
   * @throws RemoteException threw if there is a problem with RMI
   */
  void quit() throws RemoteException;

  /**
   * Callback used when the room is deleted
   * @throws RemoteException threw if there is a problem with RMI
   */
  void delete() throws RemoteException;

  /**
   * Callback used when there are new messages
   * @throws RemoteException threw if there is a problem with RMI
   */
  void send() throws RemoteException;
}