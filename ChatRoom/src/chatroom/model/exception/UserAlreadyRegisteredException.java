/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom.model.exception;


/**
 * Exception threw when the chosen pseudo is already used
 */
public class UserAlreadyRegisteredException extends ChatRoomException {
  private static final long serialVersionUID = 1523569318333772581L;

  /**
   * Constructor
   */
  public  UserAlreadyRegisteredException() {
    super("This user name is already used");
  }
    
}
