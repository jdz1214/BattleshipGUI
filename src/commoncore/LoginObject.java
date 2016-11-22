package commoncore;

import java.io.Serializable;

public class LoginObject implements Serializable {
	private static final long serialVersionUID = -8422066942231726832L;
	private String username;
	private String password;
	private Boolean loginSuccess;
    private Type type;
	
	//Constructors
	public LoginObject () {}
	public LoginObject (Boolean loginSuccess, String username) {this.type = Type.LOGIN; this.loginSuccess = loginSuccess; this.username = username;}
	public LoginObject (Boolean requestingLogin, String username, String password) {
		super();
		if (requestingLogin) {this.type = Type.LOGIN;}
		this.username = username;
		this.password = password;
	}
	public LoginObject (Type t) {
	    super();
        this.type = t;
    }

	public enum Type {
		LOGIN,
		LOGOUT,
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
	
	public void setLoginSuccess(Boolean wasLoginSuccessful) {
		loginSuccess = wasLoginSuccessful;
	}

	public void setType(Type type) {
        this.type = type;
    }

}
