package server;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jdz on 7/7/16.
 */
public class ServerController implements Initializable {
    @FXML private Watchtower app;
    @FXML public ListView clientList;
    @FXML public TextArea txtAreaConsole;
    @FXML private Button btnKick;

    @FXML
    private void kickClicked(ActionEvent event) {
        String uname = (String) clientList.getSelectionModel().getSelectedItem();
        app.kick(uname);
    }

    public void init (Watchtower app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
