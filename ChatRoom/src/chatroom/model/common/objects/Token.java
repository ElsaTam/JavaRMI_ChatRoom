package chatroom.model.common.objects;

import java.io.Serializable;

/**
 * The token which is use for all interactions between a registered user and distant objects
 */
public class Token implements Serializable {
  private static final long serialVersionUID = -4254182307492253339L;
  private final Long id;

  /**
   * Constructor
   * @param token the token's value
   */
  public Token(Long token) {
    this.id = token;
  }

  /**
   * Getter
   * @return the token's value
   */
  public Long getToken() {
    return this.id;
  }

}
