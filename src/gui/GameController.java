package gui;

import client.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;


public class GameController implements Initializable {
    @FXML private Label lblAttackHistory;
    @FXML private Label lblYourFleet;
    @FXML private GridPane gridAttackHistory;
    @FXML private GridPane gridFleet;
    @FXML private Button btnQuit;
    @FXML private TextArea txtGameChat;
    @FXML private TextField txtInput;
    @FXML private Main m;
          private String opponentUsername;


    @FXML
    private void quit() {
        m.startGUI();
    }

    @FXML private void onEnter() {
        String msg = txtInput.getText();
        if (msg != null) {
            m.send(txtInput.getText());
            txtInput.setText("");
        }

    }

    @FXML
    public void init(Main m, String opponentUsername) {
        this.m = m;
        lblAttackHistory.setText("Attack History vs. " + opponentUsername);
    }

    @FXML
    public void updateChat(String msg) {
        txtGameChat.appendText(msg);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}