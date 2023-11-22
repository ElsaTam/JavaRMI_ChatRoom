package chatroom.ui.client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author TmpAdmin
 */
public class ChatRoom extends Application {
    private static final AllScenesLoaders allScenesLoaders = new AllScenesLoaders();
    private static Pane root;
    public static Stage stage;
    
    @Override
    public void start(Stage primaryStage) {
        ChatRoom.stage = primaryStage;
        changeScene("/chatroom/ui/client/FXMLStartPage.fxml");
    }

    public static void changeScene(String fxml){
        try{
            switch (fxml){
                case "/chatroom/ui/client/FXMLStartPage.fxml":
                    root = (Pane)(allScenesLoaders.getStartPage()).load();
                    break;
                default:
                    break;
                    
            }
        } catch (IOException ex){
            Logger.getLogger(ChatRoom.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
        
        root.prefHeightProperty().bind(newScene.heightProperty());
        root.prefWidthProperty().bind(newScene.widthProperty());
        stage.setTitle("ChatRoom");
        stage.show();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
