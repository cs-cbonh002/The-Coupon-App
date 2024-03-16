package edu.odu.cs.teamblack.cs411.thecouponapp.network;

public class JwtResponse {
    private String access;
    private String refresh;

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getRefresh() {
        return refresh;
    }

    public void setRefresh(String refresh) {
        this.refresh = refresh;
    }
}

