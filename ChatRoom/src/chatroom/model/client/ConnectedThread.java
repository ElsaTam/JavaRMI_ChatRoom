package chatroom.model.client;

import chatroom.model.client.printers.Printer;
import chatroom.model.common.interfaces.RoomInterface;
import chatroom.model.common.objects.Data;
import chatroom.model.common.objects.Token;
import chatroom.model.exception.*;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class which is a thread dedicated to a room, needs to implements delete
 */
public abstract class ConnectedThread implements Runnable {
  private final Token user;
  private final RoomInterface room;
  private final Printer printer;
  private Interactions action;
  private Data toSend;
  private String destPrivate;
  private Exception exception;

  /**
   * Enum used to know what interaction the connected thread have to do
   */
  private enum Interactions {
    SEND,
    QUIT,
    DELETE,
    GET,
    PRIVATE,
    ERROR,
    NOTHING
  }

  /**
   * The constructor
   * @param token the user's Token
   * @param dedicatedRoom the dedicated's room
   * @param print the printed used to get messages and exceptions
   * @param psw the password of the Room
   * @throws RemoteException threw if there is a problem with RMI
   * @throws WrongPasswordException threw if the specified password is false
   * @throws ChatRoomIsFullException threw if the room is full
   * @throws AlreadyRegisteredToTheRoomException threw if the user is already registered on the room
   */
  protected ConnectedThread(Token token, RoomInterface dedicatedRoom, Printer print, String psw) throws RemoteException, WrongPasswordException, ChatRoomIsFullException, AlreadyRegisteredToTheRoomException {
    this.user = token;
    this.room = dedicatedRoom;
    this.printer = print;
    ThreadListener listen = new ThreadListener(this);
    Thread threadListener = new Thread(listen);
    threadListener.start();
    this.room.registerClient(listen, this.user, psw);
    this.action = Interactions.NOTHING;
    this.destPrivate = null;
    this.toSend = null;
    this.exception = null;
  }

  /**
   * Callback used to send a Message
   * @param data the message to send
   */
  public final void sendMessage(Data data) {
    this.action = Interactions.SEND;
    this.toSend = data;
    synchronized (this) {
      this.notify();
    }
  }

  /**
   * Callback used to send a private message
   * @param dest the recipient's name
   * @param data the message to send
   */
  public final void sendPrivateMessage(String dest, Data data) {
    this.action = Interactions.PRIVATE;
    this.toSend = data;
    this.destPrivate = dest;
    synchronized (this) {
      this.notify();
    }
  }

  /**
   * Callback used to quit the room
   */
  public final void quit() {
    this.action = Interactions.QUIT;
    synchronized (this) {
      this.notify();
    }
  }

  /**
   * Callback used to delete the room if the user is the manager
   */
  public final void deleteRoom() {
    this.action = Interactions.DELETE;
    synchronized (this) {
      this.notify();
    }
  }

  /**
   * Methods to implements to delete a room
   * @throws NotAllowedException threw if the user is not allowed to do this
   * @throws UserNotRegisteredException threw if the user is not registered on the room
   * @throws RemoteException threw if there is a problem with RMI
   */
  protected abstract void delete() throws NotAllowedException, UserNotRegisteredException, RemoteException;

  /**
   * Callback used to get messages
   */
  public final void get() {
    this.action = Interactions.GET;
    synchronized (this) {
      this.notify();
    }
  }

  /**
   * Callback used to get the user's Token
   * @return the user's token
   */
  protected Token getUser() {
    return this.user;
  }

  /**
   * The run method
   */
  @Override
  public final void run() {
    boolean stop = false;
    while (!stop) {
      synchronized (this) {
        try {
          this.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      switch (this.action) {
        case SEND:
          try {
            if (this.toSend!=null) {
              this.room.sendMessage(this.user, this.toSend);
              toSend = null;
              this.printer.print(this.room.getMessages(this.user));
            }
          } catch (RemoteException | UserNotRegisteredException e) {
            this.printer.printError(e);
          }
          break;
        case QUIT:
          try {
            this.room.quit(this.user);
          } catch (RemoteException | UserNotRegisteredException e) {
            this.printer.printError(e);
          } finally {
            stop = true;
          }
          break;
        case DELETE:
          try {
            this.delete();
          } catch (NotAllowedException | UserNotRegisteredException | RemoteException e) {
            this.printer.printError(e);
          } finally {
            stop = true;
          }
          break;
        case GET:
          try {
            this.printer.print(this.room.getMessages(this.user));
          } catch (RemoteException | UserNotRegisteredException e) {
            this.printer.printError(e);
          }
          break;
        case PRIVATE:
          try {
            if (this.toSend!=null && this.destPrivate!=null) {
              this.room.sendPrivateMessage(this.user, this.destPrivate, this.toSend);
              this.toSend = null;
              this.destPrivate = null;
              this.printer.print(this.room.getMessages(this.user));
            }
          } catch (RemoteException | UserNotRegisteredException | RecipientNotHereException e) {
            this.printer.printError(e);
          }
      
          break;
        case ERROR:
          this.printer.printError(this.exception);
          break;
        case NOTHING:
          break;
      }
    }
  }
}
