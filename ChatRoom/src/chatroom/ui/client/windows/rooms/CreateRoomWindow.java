package chatroom.ui.client.windows.rooms;

import chatroom.model.client.printers.WindowPrinter;
import chatroom.model.exception.AlreadyRegisteredToTheRoomException;
import chatroom.model.exception.ChatRoomIsFullException;
import chatroom.model.exception.RoomAlreadyExistingException;
import chatroom.model.exception.UserNotRegisteredException;
import chatroom.model.exception.WrongPasswordException;
import chatroom.ui.client.ChatRoom;
import chatroom.ui.client.FXMLStartPageController;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author TmpAdmin
 */
public class CreateRoomWindow extends Stage{
    private final TextField textFieldName = new TextField();
    private final TextField textFieldCapacity = new TextField();
    private final TextField textFieldPassword = new TextField();
    private final Label errorLabel = new Label();
    private final ToggleGroup groupeVisibility = new ToggleGroup();
    
    private final FXMLStartPageController controller;
    private static CreateRoomWindow singleton;
    
    private CreateRoomWindow(FXMLStartPageController controller){
        this.controller = controller;
    }
    
    public static CreateRoomWindow getSingleton(FXMLStartPageController controller){
        if (singleton == null){
            singleton = new CreateRoomWindow(controller);
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
        
        /* nom de la salle */
        Label labelName = new Label("Nom de la salle");
        vbox.getChildren().add(labelName);
        vbox.getChildren().add(textFieldName);
        
        /* capacité */
        Label labelCapacity = new Label("Capacité max");
        vbox.getChildren().add(labelCapacity);
        // force the field to be numeric only
        textFieldCapacity.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d*")) {
                textFieldCapacity.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        vbox.getChildren().add(textFieldCapacity);
        
        /* public ou privé */
        Label labelVisibility = new Label("Visibilité");
        vbox.getChildren().add(labelVisibility);
        HBox hBoxVisibility = new HBox();
        RadioButton rbPublic = new RadioButton("Public");
        rbPublic.setUserData("public");
        rbPublic.setToggleGroup(groupeVisibility);
        RadioButton rbPrivate = new RadioButton("Privé");
        rbPrivate.setUserData("private");
        rbPrivate.setToggleGroup(groupeVisibility);
        rbPrivate.setSelected(true);
        hBoxVisibility.getChildren().addAll(rbPublic, rbPrivate);
        vbox.getChildren().add(hBoxVisibility);
        
        /* Mot de passe */
        Label labelPassword = new Label("Mot de passe");
        vbox.getChildren().add(labelPassword);
        vbox.getChildren().add(textFieldPassword);
        groupeVisibility.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> {
            if (groupeVisibility.getSelectedToggle().getUserData().toString().equals("public")){
                textFieldPassword.setEditable(false);
                labelPassword.setVisible(false);
                textFieldPassword.setVisible(false);
            } else{
                textFieldPassword.setEditable(true);
                labelPassword.setVisible(true);
                textFieldPassword.setVisible(true);
            }
        });
        
        vbox.getChildren().add(errorLabel);
        
        stackPane.getChildren().add(vbox);
        StackPane.setAlignment(vbox, Pos.CENTER);
        
        Button buttonValidate = new Button();
        buttonValidate.setText("CREER");
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
        String name = textFieldName.getText();
        if (textFieldCapacity.getText().equals("")){
            errorLabel.setText("Capacité invalide");
        } else{
            int capacity = Integer.parseInt(textFieldCapacity.getText());
            if (name.equals("")){
                errorLabel.setText("Nom invalide");
            } else if (capacity < 1){
                errorLabel.setText("Capacité invalide");
            } else{
                if (groupeVisibility.getSelectedToggle().getUserData().toString().equals("public")){
                    System.out.println("Creating public room...");
                    try {
                        WindowPrinter print = new WindowPrinter(name);
                        FXMLStartPageController.client.createPublicRoom(name, print, capacity);
                        controller.addRoomTab(name, print);
                        textFieldCapacity.setText("");
                        textFieldName.setText("");
                        textFieldPassword.setText("");
                        errorLabel.setText("");
                        this.close();
                        System.out.println("room cree");
                    } catch (UserNotRegisteredException ex) {
                        errorLabel.setText("Vous n'êtes pas connecté au serveur");
                        Logger.getLogger(CreateRoomWindow.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (RoomAlreadyExistingException ex) {
                        errorLabel.setText("Le nom de salle est déjà pris");
                        Logger.getLogger(CreateRoomWindow.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (RemoteException | WrongPasswordException | ChatRoomIsFullException | AlreadyRegisteredToTheRoomException ex) {
                        Logger.getLogger(CreateRoomWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else{
                    String password = textFieldPassword.getText();
                    if (password.equals("")){
                        errorLabel.setText("Mot de passe incorrect");
                    } else{
                        try {
                            System.out.println("Creating private room...");
                            WindowPrinter print = new WindowPrinter(name);
                            FXMLStartPageController.client.createPrivateRoom(name, password, print, capacity);
                            System.out.println("room cree");
                            controller.addRoomTab(name, print);
                            this.close();
                        } catch (RemoteException | WrongPasswordException | ChatRoomIsFullException | AlreadyRegisteredToTheRoomException ex) {
                            Logger.getLogger(CreateRoomWindow.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (UserNotRegisteredException ex) {
                            errorLabel.setText("Vous n'êtes pas connecté au serveur");
                            Logger.getLogger(CreateRoomWindow.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (RoomAlreadyExistingException ex) {
                            errorLabel.setText("Le nom de salle est déjà pris");
                            Logger.getLogger(CreateRoomWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }
}
