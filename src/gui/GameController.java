package gui;

import client.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jdz on 7/12/16.
 */
public class GameController implements Initializable {
    @FXML private Label lblAttackHistory;
    @FXML private Label lblYourFleet;
    @FXML private GridPane gridAttackHistory;
    @FXML private GridPane gridFleet;
    @FXML private Button btnQuit;
    @FXML private TextArea txtGameChat;
    @FXML private TextField txtInput;
    @FXML private Main app;


    @FXML
    private void quit(ActionEvent event) {
        //TODO
    }

    @FXML private void onEnter(ActionEvent event) {
        //TODO send message for gamechat
    }

    @FXML
    public void init(Main app) {
        this.app = app;
    }

    @FXML
    public void updateChat(String msg) {
        txtGameChat.appendText(msg);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}