/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom.model.exception;

/**
 * Exception threw when the user is not registered on a room or to the server
 * @author etamisie
 */
public class UserNotRegisteredException extends ChatRoomException {
  private static final long serialVersionUID = -1036501878504982129L;

  public UserNotRegisteredException() {
    super("This user doesn't exist");
  }
    
}
