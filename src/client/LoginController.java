package client;

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

	@FXML public Label lblMessage;
	@FXML private TextField txtUsername;
	@FXML private PasswordField txtPassword;
	@FXML private Button btnLogin;
	@FXML private Main m;
	
	@FXML
	public void btnLoginAction() throws ClassNotFoundException, IOException {attemptLogin();}
	
	@FXML
	public void onEnter() throws ClassNotFoundException, IOException {attemptLogin();}

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
        txtPassword.setDisable(false);
        txtUsername.setDisable(false);
        txtUsername.setEditable(true);
        btnLogin.setDisable(false);
    }

    @FXML
    void disableLogin() {
        btnLogin.setDisable(true);
        txtPassword.setText("");
        txtUsername.setText("");
        txtUsername.setDisable(true);
        txtPassword.setDisable(true);
    }

    void setLblMessage(String lblTxt) {
        lblMessage.setText(lblTxt);
    }

    void init(Main m) {
		this.m = m;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {}
}