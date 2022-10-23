package edu.snhu.craigo_eventapp;

import android.content.Context;
import android.util.Log;

import java.sql.Date;
import java.util.List;

/**
 * The front of the model for the Event App. Manipulates the database which is SQLlite
 * accessed through Room.
 */

public class EventTracker {

    private EventDatabase mEvents;
    private Context appContext;
    private String mActiveUser;

    private static EventTracker mInstance;
    private EventTracker(Context context) {
        appContext = context;
        mEvents = EventDatabase.getInstance(appContext);
    }

    public static EventTracker getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new EventTracker(context);
        }
        return mInstance;
    }

    public LoginStatus Login(String username, String password) {

        if (mEvents.userDAO().checkUsername(username) == null)
            return LoginStatus.LOGIN_NO_USER;

        User user = new User();
        user = mEvents.userDAO().login(username, password);

        if (user == null)
            return LoginStatus.LOGIN_BAD_PASSWORD;

        mActiveUser = user.getUsername();
        return LoginStatus.LOGIN_OK;

    }

    private boolean usernameAvailable(String username) {
        if (mEvents.userDAO().checkUsername(username) == null)
            return true;
        else {
            return false;
        }
    }

    public boolean CreateAccount(String username, String password) {

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        if (usernameAvailable(username)) {
            mEvents.userDAO().addUser(user);
            return true;
        }
        return false;
    }

    public String getCurrentUser() { return mActiveUser; }

    public List<Event> getEvents(EventOrder order) {

        switch (order) {
            default:
            case byDate:
                return mEvents.eventDAO().getEvents(mActiveUser);

            case byPriority:
                return mEvents.eventDAO().getEventsByPriority(mActiveUser);

        }
    }

    public boolean addEvent(Event event) {
        addEvent(
                event.getTitle(),
                event.getDate(),
                event.getDesc(),
                event.getPriority()
        );
        return true;
    }

    public boolean addEvent(String title, Date date, String desc, Priority priority) {

        Event event = new Event();
        event.setUser(mActiveUser);
        event.setTitle(title);
        event.setDate(date);
        event.setDesc(desc);
        event.setPriority(priority);

        mEvents.eventDAO().insertEvent(event);
        Log.d("TRACKER", "inserted..");
        // TODO: if fail return false

        return true;
    }

    public boolean updateEvent(Event eventToUpdate, Event eventNewFields) {

        eventToUpdate.setTitle(eventNewFields.getTitle());
        eventToUpdate.setDate(eventNewFields.getDate());
        eventToUpdate.setDesc(eventNewFields.getDesc());
        eventToUpdate.setPriority(eventNewFields.getPriority());

        mEvents.eventDAO().updateEvent(eventToUpdate);

        return true;
    }

    public boolean deleteEvent(Event event) {
        mEvents.eventDAO().deleteEvent(event);
        // TODO: false if no exist
        return true;
    }

}
