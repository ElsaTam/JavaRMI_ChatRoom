package chatroom.model.common.objects;

import java.io.Serializable;

/**
 * A message with one sender and one data
 */
public class Message implements Serializable {
  private static final long serialVersionUID = 4293788092215196796L;
  private final String sender;
  private final Data data;

  /**
   * Constructor
   * @param expeditor the name of the sender
   * @param toSend the data's message
   */
  public Message(String expeditor, Data toSend) {
    this.sender = expeditor;
    this.data = toSend;
  }

  /**
   * Getter
   * @return the name of the sender
   */
  public String getSender() {
    return sender;
  }

  /**
   * Getter
   * @return the message sent
   */
  public Data getData() {
    return data;
  }
}
