package com.example.cookbook.model;

public class LoginUser extends User{
    private String login;
    private String password;
    private String userID;

    public LoginUser(String login,String password,String firstName, String lastName, String userID) {
        super(firstName,lastName);
        this.login = login;
        this.password = password;
        this.userID = userID;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
