package chatroom.model.client.printers;

import chatroom.model.common.objects.Message;

import java.util.List;

/**
 * Interface used to get messages and exceptions threw by connected threads et threads listener
 */
public interface Printer {
  void print(List<Message> messages);
  void printError(Exception e);
}
