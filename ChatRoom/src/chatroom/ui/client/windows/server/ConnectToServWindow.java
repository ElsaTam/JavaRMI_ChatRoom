/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom.ui.client.windows.server;

import chatroom.model.client.Client;
import chatroom.model.common.interfaces.ServeurInterface;
import chatroom.model.exception.UserAlreadyRegisteredException;
import chatroom.ui.client.ChatRoom;
import chatroom.ui.client.FXMLStartPageController;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author TmpAdmin
 */
public class ConnectToServWindow extends Stage {
    private static ConnectToServWindow singleton;
    private TextField textFieldAdress;
    private TextField textFieldPseudo;
    private final FXMLStartPageController controller;
    private Label errorLabel;
    
    private ConnectToServWindow(FXMLStartPageController controller){
        this.controller = controller;
    }
    
    public static ConnectToServWindow getSingleton(FXMLStartPageController controller){
        if (singleton == null){
            singleton = new ConnectToServWindow(controller);
            singleton.initialize(ChatRoom.stage);
        } else{
            singleton.show();
        }
        return singleton;
    }
    
    public void initialize(Stage primaryStage){
        this.initModality(Modality.APPLICATION_MODAL);
        this.initOwner(primaryStage);
        
        StackPane stackPane = new StackPane();
        
        VBox vbox = new VBox();
        vbox.getChildren().add(new Label("Adresse du serveur"));
        textFieldAdress = new TextField();
        vbox.getChildren().add(textFieldAdress);
        vbox.getChildren().add(new Label("Pseudo"));
        textFieldPseudo = new TextField();
        vbox.getChildren().add(textFieldPseudo);
        errorLabel = new Label();
        vbox.getChildren().add(errorLabel);
        
        stackPane.getChildren().add(vbox);
        StackPane.setAlignment(vbox, Pos.CENTER);
        
        Button buttonValidate = new Button();
        buttonValidate.setText("SE CONNECTER");
        buttonValidate.setOnAction((ActionEvent e) -> {
            validate();
        });
        stackPane.getChildren().add(buttonValidate);
        StackPane.setAlignment(buttonValidate, Pos.BOTTOM_CENTER);
        
        Scene createRoomScene = new Scene(stackPane, 300, 200);
        this.setScene(createRoomScene);
        this.show();
    }
    
    private void validate(){
        String adress = textFieldAdress.getText();
        String pseudo = textFieldPseudo.getText();
        if (adress.equals("")){
            errorLabel.setText("Adresse invalide");
        } else if(pseudo.equals("")){
            errorLabel.setText("Pseudo invalide");
        } else{
            errorLabel.setText("En attente du serveur...");
            String rmiurl = "rmi://" + adress + "/chatroomsManager";
            ServeurInterface serv = null;
            try {
                serv = (ServeurInterface) Naming.lookup(rmiurl);
                FXMLStartPageController.client = new Client(pseudo,serv);
                FXMLStartPageController.client.connectToServer();
                controller.displayPseudoIfConnected(pseudo);
                textFieldAdress.setText("");
                textFieldPseudo.setText("");
                errorLabel.setText("");
                this.close();
            } catch (UnknownHostException ex){
                errorLabel.setText("Serveur inconnu");
                Logger.getLogger(ConnectToServWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NotBoundException | MalformedURLException | RemoteException ex) {
                Logger.getLogger(ConnectToServWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch(UserAlreadyRegisteredException ex){
                errorLabel.setText("Le pseudo est déjà utilisé");
                Logger.getLogger(ConnectToServWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
