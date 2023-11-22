package chatroom.model.serveur;

import java.rmi.Naming;

/**
 * Set up the server
 */
public class ServeurApp {
  /**
   * Main method
   * @param args not used
   */
  public static void main(String[] args) {

    Serveur serv;

    try {
      System.out.println("Creation de l'objet login.");
      serv = new Serveur();
      System.out.println("Enregistrement de l'objet login.");
      Naming.rebind("chatroomsManager",serv);
      String ip = java.net.InetAddress.getLocalHost().getHostAddress();
      System.out.println("Service login opérationnel at " + ip + ".");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
