package edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.responses;

public class UserResponse {
    private String username;
    private String email;
    private String address;
    private String firstName;
    private String lastName;
    private String pets;
    private String familyInfo;
    private String additionalInfo;

    // Constructors
    public UserResponse() {
    }

    public UserResponse(String username, String email, String address, String firstName, String lastName, String pets, String familyInfo, String additionalInfo) {
        this.username = username;
        this.email = email;
        this.address = address;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pets = pets;
        this.familyInfo = familyInfo;
        this.additionalInfo = additionalInfo;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPets() {
        return pets;
    }

    public void setPets(String pets) {
        this.pets = pets;
    }

    public String getFamilyInfo() {
        return familyInfo;
    }

    public void setFamilyInfo(String familyInfo) {
        this.familyInfo = familyInfo;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
