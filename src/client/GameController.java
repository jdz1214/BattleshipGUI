package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;


public class GameController implements Initializable {
    @FXML private TitledPane titledPane;
    @FXML private Label lblAttackHistory;
    @FXML private Label lblYourFleet;
    @FXML private GridPane gridAttackHistory;
    @FXML private GridPane gridFleet;
    @FXML private TextArea txtGameChat;
    @FXML private TextField txtInput;
    @FXML private Button btnQuit;
    @FXML private Button btnAttack;
    @FXML private Button gridHistory00;
    @FXML private Button gridHistory01;
    @FXML private Button gridHistory02;
    @FXML private Button gridHistory03;
    @FXML private Button gridHistory04;
    @FXML private Button gridHistory05;
    @FXML private Button gridHistory10;
    @FXML private Button gridHistory11;
    @FXML private Button gridHistory12;
    @FXML private Button gridHistory13;
    @FXML private Button gridHistory14;
    @FXML private Button gridHistory15;
    @FXML private Button gridHistory20;
    @FXML private Button gridHistory21;
    @FXML private Button gridHistory22;
    @FXML private Button gridHistory23;
    @FXML private Button gridHistory24;
    @FXML private Button gridHistory25;
    @FXML private Button gridHistory30;
    @FXML private Button gridHistory31;
    @FXML private Button gridHistory32;
    @FXML private Button gridHistory33;
    @FXML private Button gridHistory34;
    @FXML private Button gridHistory35;
    @FXML private Button gridHistory40;
    @FXML private Button gridHistory41;
    @FXML private Button gridHistory42;
    @FXML private Button gridHistory43;
    @FXML private Button gridHistory44;
    @FXML private Button gridHistory45;
    @FXML private Button gridHistory50;
    @FXML private Button gridHistory51;
    @FXML private Button gridHistory52;
    @FXML private Button gridHistory53;
    @FXML private Button gridHistory54;
    @FXML private Button gridHistory55;
    private Main m;
    private String opponentUsername;


    @FXML
    private void quit() {
        m.showGUI();
    }

    @FXML
    private void onEnter() {
        sendMessage();
    }

    @FXML
    void init(Main m, String opponentUsername) {
        this.m = m;
        titledPane.setText("Battleship - Game: " + m.getUsername());
        lblAttackHistory.setText("Attack History vs. " + opponentUsername);
        btnAttack.setDisable(true);
    }

    @FXML
    public void enableAttack() {
        btnAttack.setDisable(false);
    }

    public void updateHistory(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String btnName = btn.getId();
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
    void updateChat(String msg) {
        txtGameChat.appendText(msg + "\n");
    }

    @FXML
    void disableChat() {
        txtInput.setEditable(false);
        txtInput.setDisable(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @FXML public void attack(ActionEvent actionEvent) {
    }
}