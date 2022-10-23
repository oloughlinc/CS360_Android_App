package edu.snhu.craigo_eventapp;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name="username")
    private String mUsername = "";

    // should be hashed and definitely should not be stored here
    // 'demonstration only'
    @ColumnInfo(name="password")
    @NonNull
    private String mPassword = "";

    public void setUsername(String username) {
        mUsername = username;
    }
    public String getUsername() {
        return mUsername;
    }

    public void setPassword(String password) {
        mPassword = password;
    }
    public String getPassword() {
        return mPassword;
    }

}
