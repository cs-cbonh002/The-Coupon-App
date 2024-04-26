package edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "emergency_contacts")
public class EmergencyContact implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String firstName;
    public String lastName;
    public String email;
    public String relationship;
    public String address;
    public String phoneNumber;
    public boolean isPrimary;


    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }

    public int getCommunicationPreferences() {
        return communicationPreferences;
    }

    public void setCommunicationPreferences(int communicationPreferences) {
        this.communicationPreferences = communicationPreferences;
    }

    public int communicationPreferences;


    public void setFirstName(String name) {
        this.firstName = name;
    }
    public void setLastName(String lastname) {
        this.lastName = lastname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public EmergencyContact() {
        this.firstName = "";
        this.lastName = "";
        this.email = "";
        this.relationship = "";
        this.address = "";
        this.phoneNumber = "";
        this.isPrimary = false;
        this.communicationPreferences = 0;
    }

    // implement parcelable

    public EmergencyContact(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        relationship = in.readString();
        address = in.readString();
        phoneNumber = in.readString();
        isPrimary = in.readByte() != 0;
        communicationPreferences = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(relationship);
        dest.writeString(address);
        dest.writeString(phoneNumber);
        dest.writeByte((byte) (isPrimary ? 1 : 0));
        dest.writeInt(communicationPreferences);
    }

    public static final Parcelable.Creator<EmergencyContact> CREATOR = new Parcelable.Creator<EmergencyContact>() {
        @Override
        public EmergencyContact createFromParcel(Parcel in) {
            return new EmergencyContact(in);
        }

        @Override
        public EmergencyContact[] newArray(int size) {
            return new EmergencyContact[size];
        }
    };

}
