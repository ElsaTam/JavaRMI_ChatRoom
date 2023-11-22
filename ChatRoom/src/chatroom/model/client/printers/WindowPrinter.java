/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom.model.client.printers;

import chatroom.model.common.objects.Data;
import chatroom.model.common.objects.DataFile;
import chatroom.model.common.objects.DataString;
import static chatroom.model.common.objects.DataType.FILE;
import static chatroom.model.common.objects.DataType.STRING;
import chatroom.model.common.objects.Message;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 *
 * @author TmpAdmin
 */
public class WindowPrinter implements Printer {
    
    private TextArea textAreaMessage;
    private TextFlow textFlowMessage;
    private Label errorLabel;
    private final String roomName;
    
    private final String directory = System.getProperty("user.home");

    public WindowPrinter(String name){
        this.roomName = name;
    }
    
    public void setTextArea(TextArea ta){
        this.textAreaMessage = ta;
    }
    
    public void setErrorLabel(Label el){
        this.errorLabel = el;
    }
    
    public void setTextFlow(TextFlow tf){
        this.textFlowMessage = tf;
    }
    
    @Override
    public void print(List<Message> messages) {
        for (Message curr : messages) {
            Data content = curr.getData();
            //final String sender = curr.getSender();
            final Text sender = new Text(curr.getSender());
            sender.setFont(Font.font(sender.getFont().getFamily(), FontWeight.BOLD, sender.getFont().getSize()));
            final Color textColor = (content.isAparte()) ? Color.BLUE : Color.BLACK;
            final String visibility = (content.isAparte()) ? " [privé]" : "";
           
            switch (content.getType()) {

                case STRING:
                    final Text msg = new Text(((DataString)content).getMessage() + "\n");
                    msg.setFill(textColor);
                    Platform.runLater(new Runnable(){
                        @Override public void run(){
                            textFlowMessage.getChildren().addAll(sender, new Text(visibility + " : "), msg);
                        }
                    });
                    break;
                    
                case FILE:
                    DataFile fileDist = (DataFile)content;
                    File recept = new File(fileDist.getName());
                    final Text info = new Text(" a envoyé un fichier" + visibility + " : ");
                    info.setFill(textColor);
                    try {
                            /* === CERATION DE L'HYPERLIEN === */
                        final Hyperlink link = new Hyperlink(fileDist.getName());
                        link.setOnAction(event ->{
                            if (Desktop.isDesktopSupported())
                                try {
                                    Desktop.getDesktop().open(recept);
                            } catch (IOException ex) {
                                Logger.getLogger(WindowPrinter.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                        
                            /* === ECRITURE DU FICHIER === */
                        if (recept.createNewFile()) {
                            FileOutputStream stream = new FileOutputStream(fileDist.getName());
                            try {
                                stream.write(fileDist.getContent());
                                Platform.runLater(new Runnable(){
                                    @Override public void run(){
                                        textFlowMessage.getChildren().addAll(sender, info, link, new Text("\n"));
                                    }
                                });
                            } catch (IOException e) {
                                this.printError(e);
                            } finally {
                                stream.close();
                            }
                            
                        } else {
                            Platform.runLater(new Runnable(){
                                @Override public void run(){
                                    textFlowMessage.getChildren().addAll(sender, new Text(" a envoyé un fichier" + visibility + " : "), link, new Text(" (le fichier était déjà présent et n'a pas été écrasé)\n"));
                                }
                            });
                        }
                    } catch (IOException e) {
                        this.printError(e);
                    }
                    break;
            }
        }
    }

    @Override
    public void printError(Exception e) {
        Platform.runLater(new Runnable(){
            @Override public void run(){
                errorLabel.setText("ERREUR : " + e.getMessage());
            }
        });
    }
    
}
