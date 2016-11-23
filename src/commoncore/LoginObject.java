package commoncore;

import java.io.Serializable;

public class LoginObject implements Serializable {
	private String username;
	private String password;
	private Boolean loginSuccess;
    private  Type type;
	
	//Constructors
	public LoginObject () {}
	public LoginObject (Boolean loginSuccess, String username) {this.type = Type.LOGINRESULT; this.loginSuccess = loginSuccess; this.username = username;}
	public LoginObject (String username, String password) {
        assert username.length() > 0 && password.length() > 0;
		this.type = Type.LOGINREQUEST;
		this.username = username;
		this.password = password;
	}
	public LoginObject (Type type) {
        this.type = type;
    }

	public enum Type {
		LOGINREQUEST,
        LOGINRESULT,
        LOGOUTREQUEST,
		KICK
	}
	
	//Methods
    public Type getType() {
        return type;
    }

	public String getUsername() {
        assert username != null;
		return username;
	}
	
	public String getPassword() {
		assert password != null; return password;
	}
	
	public Boolean getLoginSuccess() {
		assert loginSuccess != null;
        return loginSuccess;
	}
	
	public void setType() {
        this.type = Type.LOGOUTREQUEST;
    }

}
