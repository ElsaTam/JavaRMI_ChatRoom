/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom.model.exception;


/**
 * Exception threw when the room's name is already taken by an other room
 */
public class RoomAlreadyExistingException extends ChatRoomException {
  private static final long serialVersionUID = 2820196573122775927L;

  public  RoomAlreadyExistingException() {
    super("This room's name is already used");
  }
    
}
