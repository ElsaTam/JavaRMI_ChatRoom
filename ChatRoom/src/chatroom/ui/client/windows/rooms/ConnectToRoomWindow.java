package chatroom.ui.client.windows.rooms;

import chatroom.model.client.printers.WindowPrinter;
import chatroom.model.exception.AlreadyRegisteredToTheRoomException;
import chatroom.model.exception.ChatRoomIsFullException;
import chatroom.model.exception.IncorrectRoomNameException;
import chatroom.model.exception.UserNotRegisteredException;
import chatroom.model.exception.WrongPasswordException;
import chatroom.ui.client.ChatRoom;
import chatroom.ui.client.FXMLStartPageController;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author TmpAdmin
 */
public class ConnectToRoomWindow extends Stage{
    private final ListView listViewRoom = new ListView();
    private final TextField textFieldName = new TextField();
    private final TextField textFieldPassword = new TextField();
    private final Label errorLabel = new Label();
    private final FXMLStartPageController controller;
    private static ConnectToRoomWindow singleton;
    
    private ConnectToRoomWindow(FXMLStartPageController controller){
        this.controller = controller;
    }
    
    public static ConnectToRoomWindow getSingleton(FXMLStartPageController controller){
        if (singleton == null){
            singleton = new ConnectToRoomWindow(controller);
            singleton.initialize(ChatRoom.stage);
        } else{
            singleton.refreshListRooms();
            singleton.show();
        }
        return singleton;
    }
    
    public void initialize(Stage primaryStage){
        this.initModality(Modality.APPLICATION_MODAL);
        this.initOwner(primaryStage);
        
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        
            /* ===== partie de gauche ===== */
        
        StackPane stackPane = new StackPane();
        
        VBox vbox = new VBox();
        Label labelName = new Label("Nom de la salle");
        vbox.getChildren().add(labelName);
        vbox.getChildren().add(textFieldName);
        vbox.getChildren().add(new Label("Mot de passe (optionnel)"));
        vbox.getChildren().add(textFieldPassword);
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
        
        
            /* ===== partie de droite ===== */
        
        refreshListRooms();
        
            /* ===== finalisation ===== */
            
        splitPane.getItems().addAll(stackPane, listViewRoom);
        
        Scene createRoomScene = new Scene(splitPane, 300, 200);
        this.setScene(createRoomScene);
        this.show();
    }
    
    private void validate(){
        String name = textFieldName.getText();
        if (FXMLStartPageController.client == null){
            errorLabel.setText("Veuillez d'abord vous connecter au serveur");
        } else if (name.equals("")){
            errorLabel.setText("Nom invalide");
        } else{
            errorLabel.setText("En attente du serveur...");
            String password = textFieldPassword.getText();
            try {
                WindowPrinter print = new WindowPrinter(name);
                FXMLStartPageController.client.connectToRoom(name, print, password);
                controller.addRoomTab(name, print);
                textFieldName.setText("");
                textFieldPassword.setText("");
                errorLabel.setText("");
                this.close();
            } catch (IncorrectRoomNameException ex) {
                errorLabel.setText("Nom de salle inconnu");
                Logger.getLogger(ConnectToRoomWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ChatRoomIsFullException ex) {
                errorLabel.setText("La salle est pleine");
                Logger.getLogger(ConnectToRoomWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UserNotRegisteredException ex) {
                errorLabel.setText("Vous n'êtes pas connecté au serveur");
                Logger.getLogger(ConnectToRoomWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (AlreadyRegisteredToTheRoomException ex) {
                errorLabel.setText("Vous êtes déjà connecté à cette salle");
                Logger.getLogger(ConnectToRoomWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RemoteException ex) {
                Logger.getLogger(ConnectToRoomWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (WrongPasswordException ex) {
                errorLabel.setText("Mot de passe incorrect");
                Logger.getLogger(ConnectToRoomWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void refreshListRooms(){
        listViewRoom.getItems().clear();
        try {
            ObservableList rooms = FXCollections.observableArrayList();
            for (String roomName : FXMLStartPageController.client.roomsList()){
                rooms.add(roomName);
            }
            listViewRoom.setItems(rooms);
            listViewRoom.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
                @Override
                public void changed(ObservableValue<? extends String> ov, String old_val, String new_val){
                    textFieldName.setText(new_val);
                }
            });
        } catch (RemoteException ex) {
            Logger.getLogger(ConnectToRoomWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
