package chatroom.model.common.objects;

import java.io.Serializable;

/**
 * Class which represents a string send in the room
 */
public class DataString extends Data implements Serializable {
  private static final long serialVersionUID = -5424470124162164621L;
  private final String message;

  /**
   * Constructor
   * @param msg the content of the message
   * @param priv true if the message is private
   */
  public DataString(String msg, boolean priv) {
    super(DataType.STRING, priv);
    this.message = msg;
  }

  /**
   * Getter
   * @return the content of the message
   */
  public String getMessage() {
    return this.message;
  }
}
