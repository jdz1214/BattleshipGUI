package gui;

import java.net.URL;
import java.util.ResourceBundle;

import client.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class GUIController implements Initializable {

	@FXML private Main m;
	@FXML private MenuItem menuNewGame;
	@FXML private MenuItem menuListPlayers;
	@FXML private MenuItem menuLogout;
	@FXML private TextArea txtChatArea;
	@FXML private GridPane gameGrid;
	@FXML private TextField chatTextField;
	@FXML private Text grid00;
	@FXML private Text grid01;
	@FXML private Text grid02;
	@FXML private Text grid03;
	@FXML private Text grid04;
	@FXML private Text grid05;
	@FXML private Text grid10;
	@FXML private Text grid11;
	@FXML private Text grid12;
	@FXML private Text grid13;
	@FXML private Text grid14;
	@FXML private Text grid15;
	@FXML private Text grid20;
	@FXML private Text grid21;
	@FXML private Text grid22;
	@FXML private Text grid23;
	@FXML private Text grid24;
	@FXML private Text grid25;
	@FXML private Text grid30;
	@FXML private Text grid31;
	@FXML private Text grid32;
	@FXML private Text grid33;
	@FXML private Text grid34;
	@FXML private Text grid35;
	@FXML private Text grid40;
	@FXML private Text grid41;
	@FXML private Text grid42;
	@FXML private Text grid43;
	@FXML private Text grid44;
	@FXML private Text grid45;
	@FXML private Text grid50;
	@FXML private Text grid51;
	@FXML private Text grid52;
	@FXML private Text grid53;
	@FXML private Text grid54;
	@FXML private Text grid55;

	@FXML
    public void updateChat(String msg) {
        txtChatArea.appendText(msg + "\n");
    }
	
	@FXML
	public void newGameActionEvent(ActionEvent e) {
		// TODO
		//Get username
		m.gameRequest();
	}
	
	@FXML
	public void listPlayersActionEvent(ActionEvent e) {
		m.listPlayers();
	}

    @FXML
    private void sendMessage() {
        String msg = chatTextField.getText();
        if (msg.length() > 0) {
            m.send(msg);
            chatTextField.setText("");
        }
    }
	
	@FXML
	public void logoutActionEvent(ActionEvent e) {
		m.logout();
	}
	
	@FXML
	public void onEnter(ActionEvent e) {
		sendMessage();
	}

	@FXML
	public void init(Main m) {
		this.m = m;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

}
