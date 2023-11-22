package chatroom.ui.client;

import chatroom.model.exception.NotRegisteredToTheRoomException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

/**
 *
 * @author TmpAdmin
 */
public class RoomTab extends Tab{
    private final TextArea textAreaMessage;
    private final TextFlow textFlowMessage;
    private final TextField textFieldmessage;
    private final Label errorLabel;
    private final Button sendMessageButton;
    
    RoomTab(String id){
        super(id);
        this.setId(id);
        textAreaMessage = new TextArea();
        textAreaMessage.setEditable(false);
        
        textFlowMessage = new TextFlow();
        
        textFieldmessage = new TextField();
        sendMessageButton = new Button();
        sendMessageButton.setText("ENVOYER");
        sendMessageButton.setOnAction((ActionEvent e) -> {
            send();
        });
        
        HBox hBox = new HBox();
        hBox.getChildren().addAll(textFieldmessage, sendMessageButton);
        
        errorLabel = new Label();
        
        VBox vBox = new VBox();
        vBox.getChildren().addAll(textFlowMessage, hBox, errorLabel);
        this.setContent(vBox);
    }
    
    private void send(){
        String message = textFieldmessage.getText();
        if (!message.equals("")){
            try {
                FXMLStartPageController.client.sendMessage(this.getId(), message);
                textFieldmessage.setText("");
            } catch (NotRegisteredToTheRoomException ex) {
                Logger.getLogger(RoomTab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    TextFlow getTextFlowMessage(){
        return textFlowMessage;
    }
    
    TextArea getTextAreaMessage(){
        return textAreaMessage;
    }

    Label getErrorLabel() {
        return errorLabel;
    }
}
