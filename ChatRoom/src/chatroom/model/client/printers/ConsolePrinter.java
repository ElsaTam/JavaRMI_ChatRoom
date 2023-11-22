package chatroom.model.client.printers;

import chatroom.model.common.objects.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ConsolePrinter implements Printer {

  @Override
  public void print(List<Message> messages) {
    for (Message curr : messages) {
      Data content = curr.getData();
      switch (content.getType()) {

        case STRING:
          System.out.println(curr.getSender() + " : " + ((DataString)content).getMessage());
          break;
        case FILE:
          DataFile fileDist = (DataFile)content;
          File recept = new File(fileDist.getName());
          try {
            if (recept.createNewFile()) {
              FileOutputStream stream = new FileOutputStream(fileDist.getName());
              try {
                stream.write(fileDist.getContent());
              } catch (IOException e) {
                this.printError(e);
              } finally {
                stream.close();
              }
            } else {
              System.out.println(fileDist.getName() + " est deja present dans le dossier ");
            }
          } catch (IOException e) {
            this.printError(e);
          }
          break;
      }
    }
  }

  @Override
  public void printError(Exception e) {
    e.printStackTrace();
  }
}
