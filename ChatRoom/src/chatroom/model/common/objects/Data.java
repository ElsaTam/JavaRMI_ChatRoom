package chatroom.model.common.objects;

import java.io.Serializable;

/**
 * Abstract class which represents a message send in a room
 */
public abstract class Data implements Serializable {
  private static final long serialVersionUID = 4594785232916565344L;
  private final DataType type;
  private final boolean aparte;

  /**
   * Constructor
   * @param typeData type of data (FILE or STRING)
   * @param priv if the message is to only one other user
   */
  protected Data(DataType typeData, boolean priv) {
    this.type = typeData;
    this.aparte = priv;
  }

  /**
   * Getter
   * @return the type of data
   */
  public DataType getType(){
    return this.type;
  }

  /**
   * Getter
   * @return true is it's a private message, false otherwise
   */
  public boolean isAparte() {
    return aparte;
  }
}
