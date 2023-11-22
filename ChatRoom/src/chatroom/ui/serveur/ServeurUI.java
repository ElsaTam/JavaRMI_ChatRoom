package chatroom.ui.serveur;

import chatroom.model.serveur.Serveur;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author TmpAdmin
 */
public class ServeurUI extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        Serveur serv;
        Label label = new Label();
        try {
            System.out.println("Creation de l'objet login.");
            serv = new chatroom.model.serveur.Serveur();
            System.out.println("Enregistrement de l'objet login.");
            Naming.rebind("chatroomsManager",serv);
            String ip = java.net.InetAddress.getLocalHost().getHostAddress();
            System.out.println("Service login opérationnel at " + ip + ".");
            label.setText("Serveur opérationnel at " + java.net.InetAddress.getLocalHost().getHostAddress() + ".");
        } catch (MalformedURLException | RemoteException | UnknownHostException e) {
            label.setText("Serveur non opérationnel\n" + e.getMessage());
        }
        
        StackPane root = new StackPane();
        root.getChildren().add(label);
        
        Scene scene = new Scene(root, 300, 250);
        
        primaryStage.setTitle("Serveur ChatRoom");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}