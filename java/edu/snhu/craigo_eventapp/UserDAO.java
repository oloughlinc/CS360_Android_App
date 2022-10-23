package edu.snhu.craigo_eventapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;

@Dao
public interface UserDAO {

    // login query
    @Query("SELECT * FROM users WHERE username = :user")
    public User checkUsername(String user);
    @Query("SELECT * FROM users WHERE username = :user AND password = :pwd")
    public User login(String user, String pwd);

    // create new
    @Insert(onConflict = OnConflictStrategy.ABORT)
    public void addUser(User user);

}
