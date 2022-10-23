package edu.snhu.craigo_eventapp;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * Room database manager. Attach entities, type converters for
 * complex data types to store, and data access objects.
 * The manager also keeps track of schema changes with a version
 * number, so that updating an active app's database can be managed.
 */

@Database(entities = {Event.class, User.class}, version = 1)
@TypeConverters({DateConverter.class, PriorityConverter.class})
public abstract class EventDatabase extends RoomDatabase{

    private static final String DATABASE_NAME = "events.db";

    // there should only ever be one database
    private static EventDatabase mEventDatabase;
    public static EventDatabase getInstance(Context context) {
        if (mEventDatabase == null) {
            mEventDatabase = Room
                            .databaseBuilder(
                                context, EventDatabase.class, DATABASE_NAME)
                            .allowMainThreadQueries()
                            .build();
        }
        return mEventDatabase;
        }

    // DAOs are abstract here. when extending this they must implement some functionality
    // which is handled by Room. The significance here is tying a certain set of actions
    // to this database, promising the tables, columns exist etc.
    public abstract EventDAO eventDAO();
    public abstract UserDAO userDAO();
}
