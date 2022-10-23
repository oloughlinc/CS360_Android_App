package edu.snhu.craigo_eventapp;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import java.util.List;

/**
 * Part of Room database. This interface defines how
 * an Event object should interact with the database.
 */

@Dao
public interface EventDAO {

    // get all events by date
    @Query("SELECT * FROM events WHERE user = :user ORDER BY date")
    public List<Event> getEvents(String user);

    // get all events by priority
    @Query("SELECT * FROM events WHERE user = :user ORDER BY priority DESC")
    public List<Event> getEventsByPriority(String user);

    // insert event
    @Insert(onConflict = OnConflictStrategy.ABORT)
    public void insertEvent(Event event);

    // delete event
    @Delete
    public void deleteEvent(Event event);

    // update
    @Update
    public void updateEvent(Event event);

}
