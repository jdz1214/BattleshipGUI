package server;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {
    public Button btnKick;
    @FXML private Watchtower app;
    @FXML public ListView<String> clientList;
    @FXML public TextArea txtAreaConsole;

    public ServerController() {
        clientList = new ListView<>();
        txtAreaConsole = new TextArea();
    }

    @FXML
    private void kickClicked() {
        String uname = clientList.getSelectionModel().getSelectedItem();
        app.kick(uname);
    }

    void init(Watchtower app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}
