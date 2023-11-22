package chatroom.model.client;

import chatroom.model.common.interfaces.ThreadListenerInterface;


import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import static java.rmi.server.UnicastRemoteObject.unexportObject;
import java.rmi.server.Unreferenced;

/**
 * Class used as a listener on a room to know when there is a new message or if there is a disconnection
 */
public class ThreadListener extends UnicastRemoteObject implements ThreadListenerInterface, Runnable, Unreferenced {

  private static final long serialVersionUID = -3236245022961897157L;
  private final ConnectedThread client;
  private ActionType action;

  /**
   * Enum used to know which action the listener has to do
   */
  private enum ActionType {
    QUIT,
    DESTROY,
    MESSAGE,
    NOTHING
  }

  /**
   * Constructor
   * @param clnt the connected thread of the room
   * @throws RemoteException
   */
  ThreadListener(ConnectedThread clnt) throws RemoteException {
    this.client = clnt;
    this.action = ActionType.NOTHING;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void quit() throws RemoteException {
    this.action = ActionType.QUIT;
    synchronized (this) {
      this.notify();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void delete() throws RemoteException {
    this.action = ActionType.DESTROY;
    synchronized (this) {
      this.notify();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void send() throws RemoteException {
    this.action = ActionType.MESSAGE;
    synchronized (this) {
      this.notify();
    }
  }

  /**
   * The run method
   */
  @Override
  public void run() {
    boolean stop = false;
    while (!stop) {
      try {
        synchronized(this) {
          this.wait();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      switch (this.action) {
        case QUIT:
          stop = true;
          break;
        case DESTROY:
          this.client.quit();
          break;
        case MESSAGE:
          this.client.get();
          break;
        case NOTHING:
          break;
      }
    }
    try {
      unexportObject(this, true);
    } catch (NoSuchObjectException e) {
      e.printStackTrace();
    }
  }

  /**
   * Disconnect the thread he is not yet referenced
   */
  @Override
  public void unreferenced() {
    this.action = ActionType.QUIT;
    synchronized (this) {
      this.notify();
    }
  }
}