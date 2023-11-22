/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom.model.exception;


/**
 * Exception threw when the room's name is not find in the server
 */
public class UnexistingRoomException extends ChatRoomException {
  private static final long serialVersionUID = 9204411920844487124L;

  /**
   * Constructor
   */
  public UnexistingRoomException() {
    super("This room doesn't exist");
  }
    
}
