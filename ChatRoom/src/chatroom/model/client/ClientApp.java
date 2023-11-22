package chatroom.model.client;

import chatroom.model.client.printers.ConsolePrinter;
import chatroom.model.common.interfaces.ServeurInterface;
import chatroom.model.exception.*;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.Scanner;

public class ClientApp {
  /**
   * Methode main
   * @param args le nom de la machine ayant le service
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Invalid number of arguments");
    }
    String url = "rmi://" + args[0] + "/chatroomsManager";
    ServeurInterface serv;
    Client clnt;
    try {
      serv = (ServeurInterface) Naming.lookup(url); // on se connecte
      System.out.println("objet distant recupere");
      clnt = new Client("damien",serv);
      System.out.println("Client cree");
      clnt.connectToServer();
      System.out.println("client connecte");
      clnt.createPublicRoom("room1", new ConsolePrinter(), 2);
      System.out.println("room cree");
      Scanner scan = new Scanner(System.in);
      scan.next();
      System.out.println("send a file to room1");
      clnt.sendFile("room1", "E:\\Damien\\Documents\\Téléchargements\\1617_Exam.pdf");
      /*clnt.sendMessage("room1", "salut");
      scan.next();
      clnt.connectToRoom("room2", new ConsolePrinter(), "toto");
      System.out.println("Connected to room2");*/
      scan.next();
      scan.close();
      System.out.println("going to disconnect");
      clnt.quitRoom("room1");
      clnt.disconnect();
    } catch (NotBoundException | IOException | ChatRoomException e) {
      e.printStackTrace();
    }
  }
}
