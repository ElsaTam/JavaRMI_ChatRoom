/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom.ui.client.windows.send;

import chatroom.model.exception.NotRegisteredToTheRoomException;
import chatroom.ui.client.ChatRoom;
import chatroom.ui.client.FXMLStartPageController;
import chatroom.ui.client.RoomTab;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author TmpAdmin
 */
public class SendPrivateFileWindow extends Stage{
    private final TextField textFieldUserName = new TextField();
    private final TextField textFieldPath = new TextField();
    private final Label errorLabel = new Label();
    
    private final FXMLStartPageController controller;
    private static SendPrivateFileWindow singleton;
    
    private SendPrivateFileWindow(FXMLStartPageController controller){
        this.controller = controller;
    }
    
    public static SendPrivateFileWindow getSingleton(FXMLStartPageController controller){
        if (singleton == null){
            singleton = new SendPrivateFileWindow(controller);
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
        
        vbox.getChildren().add(new Label("Pseudo du destinataire"));
        vbox.getChildren().add(textFieldUserName);
        
        vbox.getChildren().add(new Label("Fichier à envoyer"));
        
        textFieldPath.setPrefWidth(250);
        textFieldPath.setPrefHeight(50);
        
        HBox pathHBox = new HBox();
        FileChooser fileChooser = new FileChooser();
        Button browseButton = new Button();
        browseButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("browse.png"))));
        browseButton.setOnAction((final ActionEvent e) -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null){
                textFieldPath.setText(file.getPath());
            }
        });
        Label labelPath = new Label("Path");
        labelPath.setPrefWidth(40);
        pathHBox.getChildren().addAll(textFieldPath, browseButton);
        
        vbox.getChildren().add(pathHBox);
        
        vbox.getChildren().add(errorLabel);
        stackPane.getChildren().add(vbox);
        StackPane.setAlignment(vbox, Pos.CENTER);
        
        Button buttonValidate = new Button();
        buttonValidate.setText("ENVOYER");
        buttonValidate.setOnAction((ActionEvent e) -> {
            send();
        });
        stackPane.getChildren().add(buttonValidate);
        StackPane.setAlignment(buttonValidate, Pos.BOTTOM_CENTER);
        
        Scene createRoomScene = new Scene(stackPane, 300, 200);
        this.setScene(createRoomScene);
        this.show();
    }
    
    private void send(){
        String dest = textFieldUserName.getText();
        String path = textFieldPath.getText();
        if (path.equals(""))
            errorLabel.setText("Veuillez choisir un fichier");
        else if (dest.equals(""))
            errorLabel.setText("Veuillez indiquer un destinataire");
        else {
            try {
                FXMLStartPageController.client.sendPrivateFile(controller.getCurrentRoomName(), dest, path);
                textFieldUserName.setText("");
                textFieldPath.setText("");
                errorLabel.setText("");
                this.close();
            } catch (NotRegisteredToTheRoomException ex) {
                Logger.getLogger(RoomTab.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SendPublicFileWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
