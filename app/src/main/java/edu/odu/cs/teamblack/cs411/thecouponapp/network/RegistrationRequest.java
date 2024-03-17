package edu.odu.cs.teamblack.cs411.thecouponapp.network;

public class RegistrationRequest {
    private String username;
    private String password;

    public RegistrationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}

