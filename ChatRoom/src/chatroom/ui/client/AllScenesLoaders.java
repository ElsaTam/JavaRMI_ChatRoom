package chatroom.ui.client;


import javafx.fxml.FXMLLoader;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author TmpAdmin
 */
public class AllScenesLoaders {
    private FXMLLoader startPageLoader;
    
    public FXMLLoader getStartPage(){
        if (startPageLoader == null){
            startPageLoader = new FXMLLoader(FXMLStartPageController.class.getResource("/chatroom/ui/client/FXMLStartPage.fxml"));
        }
        return startPageLoader;
    }
}
