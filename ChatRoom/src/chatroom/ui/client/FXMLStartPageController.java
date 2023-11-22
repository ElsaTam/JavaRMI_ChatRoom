package chatroom.ui.client;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import chatroom.model.client.Client;
import chatroom.model.client.printers.WindowPrinter;
import chatroom.model.exception.NotRegisteredToTheRoomException;
import chatroom.model.exception.UserNotRegisteredException;
import chatroom.ui.client.windows.rooms.ConnectToRoomWindow;
import chatroom.ui.client.windows.server.ConnectToServWindow;
import chatroom.ui.client.windows.rooms.CreateRoomWindow;
import chatroom.ui.client.windows.send.SendPrivateFileWindow;
import chatroom.ui.client.windows.send.SendPrivateMessageWindow;
import chatroom.ui.client.windows.send.SendPublicFileWindow;
import chatroom.ui.client.windows.send.SendPublicMessageWindow;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author TmpAdmin
 */
public class FXMLStartPageController implements Initializable {
    public static Client client;

    @FXML TabPane tabPane;
    @FXML Label labelPseudo;
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ChatRoom.stage.setOnCloseRequest(event -> {
            if (client != null){
                try {
                    disconnectFromServ();
                } catch (RemoteException ex) {
                    Logger.getLogger(FXMLStartPageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    /* ===================================*/
    /* ============= ALERTS ==============*/
    /* ===================================*/
    
    private void displayAlert(String msg){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    /* ===================================*/
    /* ============= SERVER ==============*/
    /* ===================================*/
    
    @FXML
    private void connectToServ() throws RemoteException{
        if (client != null)
            displayAlert("Vous êtes déjà connecté");
        else {
            ConnectToServWindow connectToServWindow = ConnectToServWindow.getSingleton(this);
        }
    }
    
    public void displayPseudoIfConnected(String pseudo){
        labelPseudo.setText("Vous êtes connecté en tant que " + pseudo);
    }
    
    @FXML
    private void disconnectFromServ() throws RemoteException{
        if (client == null)
            displayAlert("Vous n'êtes pas connecté");
        else {
            try {
                client.disconnect();
                client = null;
                labelPseudo.setText("Vous n'êtes pas connecté");
                while (!tabPane.getSelectionModel().isEmpty()){
                    closeRoomTab();
                }
            } catch (UserNotRegisteredException ex) {
                Logger.getLogger(FXMLStartPageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /* ===================================*/
    /* ============= ROOMS ===============*/
    /* ===================================*/
    
    @FXML
    private void connectToRoom(){
        if (client == null)
            displayAlert("Vous n'êtes pas connecté");
        else {
            ConnectToRoomWindow connectToRoomWindow = ConnectToRoomWindow.getSingleton(this);
        }
    }
    
    @FXML
    private void createRoom() throws RemoteException{
        if (client == null)
            displayAlert("Vous n'êtes pas connecté");
        else {
            CreateRoomWindow createRoomWindow = CreateRoomWindow.getSingleton(this);
        }
    }
    
    public void addRoomTab(String name, WindowPrinter print){
        RoomTab tab = new RoomTab(name);
        //print.setTextArea(tab.getTextAreaMessage());
        print.setTextFlow(tab.getTextFlowMessage());
        print.setErrorLabel(tab.getErrorLabel());
        tabPane.getTabs().add(tab);
    }

    @FXML
    private void closeRoom(){
        if (client == null)
            displayAlert("Vous n'êtes pas connecté");
        else {
            try {
                client.quitRoom(tabPane.getSelectionModel().getSelectedItem().getId());
                closeRoomTab();
            } catch (NotRegisteredToTheRoomException ex) {
                Logger.getLogger(FXMLStartPageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @FXML
    private void closeAllRoom(){
        if (client == null)
            displayAlert("Vous n'êtes pas connecté");
        else {
            while (!tabPane.getSelectionModel().isEmpty()){
                closeRoom();
            }
        }
    }
    
    private void closeRoomTab(){
        tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedIndex());
    }
    
    /* ===================================*/
    /* ============== SEND ===============*/
    /* ===================================*/
    
    @FXML
    private void sendPublicMessage(){
        if (client == null)
            displayAlert("Vous n'êtes pas connecté");
        else {
            SendPublicMessageWindow sendPublicMessageWindow = SendPublicMessageWindow.getSingleton(this);
        }
    }
    
    @FXML
    private void sendPrivateMessage(){
        if (client == null)
            displayAlert("Vous n'êtes pas connecté");
        else {
            SendPrivateMessageWindow sendPrivateMessageWindow = SendPrivateMessageWindow.getSingleton(this);
        }
    }
    
    @FXML
    private void sendPublicFile(){
        if (client == null)
            displayAlert("Vous n'êtes pas connecté");
        else {
            SendPublicFileWindow sendPublicFileWindow = SendPublicFileWindow.getSingleton(this);
        }
    }
    
    @FXML
    private void sendPrivateFile(){
        if (client == null)
            displayAlert("Vous n'êtes pas connecté");
        else {
            SendPrivateFileWindow sendPrivateFileWindow = SendPrivateFileWindow.getSingleton(this);
        }
    }
    
    /* ===================================*/
    /* ============= GETTERS =============*/
    /* ===================================*/
    
    public String getCurrentRoomName(){
        return tabPane.getSelectionModel().getSelectedItem().getId();
    }
}
