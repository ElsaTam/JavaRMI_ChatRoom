package chatroom.ui.client.windows.send;

import chatroom.model.exception.NotRegisteredToTheRoomException;
import chatroom.ui.client.ChatRoom;
import chatroom.ui.client.FXMLStartPageController;
import chatroom.ui.client.RoomTab;
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
public class SendPublicMessageWindow extends Stage{
    private final TextField textFieldMessage = new TextField();
    private final Label errorLabel = new Label();
    
    private final FXMLStartPageController controller;
    private static SendPublicMessageWindow singleton;
    
    private SendPublicMessageWindow(FXMLStartPageController controller){
        this.controller = controller;
    }
    
    public static SendPublicMessageWindow getSingleton(FXMLStartPageController controller){
        if (singleton == null){
            singleton = new SendPublicMessageWindow(controller);
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
        
        vbox.getChildren().add(new Label("Message"));
        vbox.getChildren().add(textFieldMessage);
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
        String message = textFieldMessage.getText();
        if (message.equals(""))
            errorLabel.setText("Le message est vide");
        else {
            try {
                FXMLStartPageController.client.sendMessage(controller.getCurrentRoomName(), message);
                textFieldMessage.setText("");
                errorLabel.setText("");
                this.close();
            } catch (NotRegisteredToTheRoomException ex) {
                Logger.getLogger(RoomTab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
