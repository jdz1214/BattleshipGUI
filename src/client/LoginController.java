package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

	@FXML private Label lblMessage;
	@FXML private TextField txtUsername;
	@FXML private PasswordField txtPassword;
	@FXML private Button btnLogin;
	@FXML private Main m;
	
	@FXML
	public void btnLoginAction(ActionEvent event) throws ClassNotFoundException, IOException {
	    attemptLogin();
	}
	
	@FXML
	public void onEnter(ActionEvent event) throws ClassNotFoundException, IOException {
		attemptLogin();
	}

    private void attemptLogin() {
        String u = txtUsername.getText();
        String p = txtPassword.getText();
        if (u.length() > 0 && p.length() > 0) {
            m.login(u, p);
        }
    }

    @FXML
    void enableLogin() {
        txtPassword.setEditable(true);
        txtUsername.setEditable(true);
    }

    @FXML
    void disableLogin() {
        txtPassword.setEditable(false);
        txtPassword.setEditable(false);
    }

    @FXML
    public void setTxtUsername(String txt) {
        txtUsername.setText(txt);
    }

    @FXML
    void setLblMessage(String lblTxt) {
        lblMessage.setText(lblTxt);
    }

    @FXML
    public String getTxtUsername() {
        return txtUsername.getText();
    }

    @FXML
    public String getTxtPassword() {
        return txtPassword.getText();
    }

	void init(Main m) {
		this.m = m;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
        txtPassword.setEditable(false);
        txtUsername.setEditable(false);
    }
}