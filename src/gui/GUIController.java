package gui;

import client.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class GUIController implements Initializable {

	@FXML private Main m;
    @FXML private TitledPane titlePane;
    @FXML private Label lblInfo;
    @FXML private ListView<String> lstUsers;
	@FXML private TextArea txtChatArea;
	@FXML private TextField chatTextField;

	@FXML
    public void updateChat(String msg) {
        txtChatArea.appendText(msg);
    }
	
	@FXML
	public void startNewGame() {
	    String selected = lstUsers.getSelectionModel().getSelectedItem();
        if(selected == null) {
            lblInfo.setText("Please select a user from the 'Users' list first.");
        } else {
            if(lblInfo.getText().equals("Please select a user from the 'Users' list first.")) {
                lblInfo.setText("");
            }
            if(!selected.equals(m.getUsername())){
                m.gameRequest(selected);
            } else {
                lblInfo.setText("You cannot start a game with yourself!");
            }

        }
	}

	@FXML
    public void startSpectate() {
	    //TODO
    }

	@FXML
	public void listPlayersActionEvent() {
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
	public void logoutActionEvent() {
		m.logout();
	}
	
	@FXML
	public void onEnter() {
		sendMessage();
	}

	@FXML
    public void aboutClick() {
        if (lblInfo.getText().equals("Battleship © 2016 Atlas Innovation LLC All Rights Reserved.")) {
            lblInfo.setText("");
        } else {
            lblInfo.setText("Battleship © 2016 Atlas Innovation LLC All Rights Reserved.");
        }
    }
	@FXML
	public void init(Main m) {
		this.m = m;
        lstUsers.itemsProperty().bind(m.slp);
        titlePane.setText("BATTLESHIP - Lobby: " + m.getUsername());
    }
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

}
