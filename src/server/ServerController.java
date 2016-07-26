package server;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {
    @FXML private Watchtower app;
    @FXML public ListView<String> clientList;
    @FXML public TextArea txtAreaConsole;

    @FXML
    private void kickClicked() {
        String uname = clientList.getSelectionModel().getSelectedItem();
        app.kick(uname);
    }

    void init(Watchtower app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
