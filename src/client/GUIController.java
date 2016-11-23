package client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class GUIController implements Initializable {

    @FXML private Button btnNewGame;
    @FXML private Button btnSpectate;
    @FXML private Button btnLogout;
    @FXML private Button btnAbout;
    @FXML private Main m;
    @FXML private TitledPane titlePane;
    @FXML private Label lblInfo;
    @FXML private ListView<String> lstUsers;
	@FXML private TextArea txtChatArea;
	@FXML private TextField chatTextField;
    @FXML private Stage stage;

    public GUIController() {
        lblInfo = new Label();
        lstUsers = new ListView<>();
        txtChatArea = new TextArea();
        chatTextField = new TextField();
        titlePane = new TitledPane();
    }

    @FXML
    void updateChat(String msg) {
        txtChatArea.appendText(msg);
    }

    @FXML
    void updateInfoLabel(String newText) {
        lblInfo.setText(newText);
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
                System.out.println("Started Main gamerequest with " + selected);
                lblInfo.setText("");
            } else {
                lblInfo.setText("You cannot start a game with yourself!");
            }

        }
	}

	@SuppressWarnings("EmptyMethod")
    @FXML
    public void startSpectate() {
	    //TODO
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
    void confirmRequest(String opponentUsername) {
        System.out.println("Opened confirmRequest dialog.");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(this.stage);
        alert.setTitle("New Game Request!");
        alert.setHeaderText(opponentUsername + " has requested to battle you!");
        alert.setContentText("Do you accept this challenge?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            m.newGame(opponentUsername);
            System.out.println("Client Main accepted game request from dialog.");
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
    void init(Main m) {
		this.m = m;
        lstUsers.itemsProperty().bind(m.slp);
        titlePane.setText("BATTLESHIP - Lobby: " + m.getUsername());
        this.stage = m.getStage();
    }
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {}

}
