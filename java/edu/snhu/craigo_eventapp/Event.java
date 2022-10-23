package edu.snhu.craigo_eventapp;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;

/**
 * Part of the Room database, this is an object that also
 * defines a table schema. So all data relating to events
 * can be stored in these object then sent to database at once.
 */

// define a new table
@Entity(tableName = "events")
public class Event {

    // tag each event by user
    @ColumnInfo(name="user")
    private String mUser;

    @PrimaryKey(autoGenerate = true)
    private int mUniqueID;

    @NonNull
    @ColumnInfo(name="title")
    private String mTitle = "";

    @ColumnInfo(name="date")
    private Date mDate;

    @ColumnInfo(name="desc")
    private String mDesc;

    @ColumnInfo(name="priority")
    private Priority mPriority;

    // now need getters and setters

    public void setUniqueID(int ID) { mUniqueID = ID; }
    public int getUniqueID() {
        return mUniqueID;
    }

    public void setUser(String user) {
        mUser = user;
    }
    public String getUser() { return mUser; }

    public void setTitle(String title) {
        mTitle = title;
    }
    public String getTitle() {
        return mTitle;
    }

    public void setDate(Date date) {
        mDate = date;
    }
    public Date getDate() {
        return mDate;
    }

    public void setDesc(String desc) {
        mDesc = desc;
    }
    public String getDesc() {
        return mDesc;
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }
    public Priority getPriority() { return mPriority; }


}
