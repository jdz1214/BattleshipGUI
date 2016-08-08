package gui;

import client.Main;
import commoncore.Game;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;


public class GameController implements Initializable {
    @FXML private TitledPane titledPane;
    @FXML private Label lblAttackHistory;
    @FXML private Label lblYourFleet;
    @FXML private GridPane gridAttackHistory;
    @FXML private GridPane gridFleet;
    @FXML private Button btnQuit;
    @FXML private TextArea txtGameChat;
    @FXML private TextField txtInput;
    @FXML private Main m;
    @FXML private Text gridHistory00;
    @FXML private Text gridHistory01;
    @FXML private Text gridHistory02;
    @FXML private Text gridHistory03;
    @FXML private Text gridHistory04;
    @FXML private Text gridHistory05;
    @FXML private Text gridHistory10;
    @FXML private Text gridHistory11;
    @FXML private Text gridHistory12;
    @FXML private Text gridHistory13;
    @FXML private Text gridHistory14;
    @FXML private Text gridHistory15;
    @FXML private Text gridHistory20;
    @FXML private Text gridHistory21;
    @FXML private Text gridHistory22;
    @FXML private Text gridHistory23;
    @FXML private Text gridHistory24;
    @FXML private Text gridHistory25;
    @FXML private Text gridHistory30;
    @FXML private Text gridHistory31;
    @FXML private Text gridHistory32;
    @FXML private Text gridHistory33;
    @FXML private Text gridHistory34;
    @FXML private Text gridHistory35;
    @FXML private Text gridHistory40;
    @FXML private Text gridHistory41;
    @FXML private Text gridHistory42;
    @FXML private Text gridHistory43;
    @FXML private Text gridHistory44;
    @FXML private Text gridHistory45;
    @FXML private Text gridHistory50;
    @FXML private Text gridHistory51;
    @FXML private Text gridHistory52;
    @FXML private Text gridHistory53;
    @FXML private Text gridHistory54;
    @FXML private Text gridHistory55;
    private String opponentUsername;


    @FXML
    private void quit() {
        m.showGUI();
    }

    @FXML private void onEnter() {
        sendMessage();
    }

    @FXML
    public void init(Main m, String opponentUsername) {
        this.m = m;
        titledPane.setText("Battleship - Game: " + m.getUsername());
        lblAttackHistory.setText("Attack History vs. " + opponentUsername);
    }

    public void updateHistory(Game.History history) {
        //Todo
    }

    private void sendMessage() {
        String msg = txtInput.getText();
        if (msg.length() > 0) {
            m.send(msg);
            txtInput.setText("");
            updateChat(m.getUsername() + ": " + msg);
        }
    }

    @FXML
    public void updateChat(String msg) {
        txtGameChat.appendText(msg + "\n");
    }

    @FXML
    public void disableChat() {
        txtInput.setEditable(false);
        txtInput.setDisable(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}