package edu.snhu.craigo_eventapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * The main controller of the event app. From this controller most app features
 * are accessed. This controller glues the xml activity to the Event Tracker model. In this case
 * we can change either the view without effecting the model or vice versa.
 */

public class EventGridActivity extends AppCompatActivity
                               implements AdapterView.OnItemSelectedListener
{

    /*
    Controller private variables
     */

    private final int SMS_REQUEST_CODE = 0;
    private final String TAG = "EVENT_APP";

    // model instance
    private EventTracker mEventTracker;

    // view components
    private TextView mNotificationStatus;
    private TextView mWelcomeText;
    private GridLayout mGrid;
    private Spinner mSortSpinner;

    // keep track of selected event title
    private Event lastSelectedEvent;

    // track sort order for events
    private EventOrder mEventOrder;

    /*
    Activity lifecycle method overrides
     */

    /**
     * Handle startup tasks.
     * -get the model instance
     * -populate the grid
     * -set notification status and other misc.
     * @param savedStateInstance
     *         comes in null from a fresh start, else
     *         has some data
     *         (currently used to hold sort order)
     */
    @Override
    protected void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        setContentView(R.layout.activity_event_grid);

        // return action bar as login screen hides this
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }

        // set default order
        if (savedStateInstance != null
                && savedStateInstance.getBoolean("prioritySort")) {
            mEventOrder = EventOrder.byPriority;
        } else {
            mEventOrder = EventOrder.byDate;
        }

        populateSortSpinner();

        // get the model
        mEventTracker = EventTracker.getInstance(this);

        // get view components
        mWelcomeText = findViewById(R.id.textLabelWelcome);
        mGrid = findViewById(R.id.eventGrid);

        // set welcome text
        mWelcomeText.setText(getString(R.string.welcome_text,
                                        mEventTracker.getCurrentUser()));

        // set default notification status
        mNotificationStatus = findViewById(R.id.textLabelNotificationStatus);
        if (hasSMSPermissions()) {
            mNotificationStatus.setText(R.string.notification_status_enable);
        } else {
            mNotificationStatus.setText(R.string.notification_status_disabled);
        }

        // reset last selected event
        lastSelectedEvent = new Event();

        // build event view
        populateEventGrid();
    }

    /**
     * onStop, start the background notification service
     * design choice: user would not need notification while in the app
     */
    @Override
    protected void onStop() {
        super.onStop();

        if (hasSMSPermissions()) {
            // start notification service
            Intent intent = new Intent(this, EventNotifierIntentService.class);
            startService(intent);
        }
    }

    /**
     * save sort order
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("prioritySort", mEventOrder == EventOrder.byPriority);
    }

    /*
    Controller Callbacks
     */

    public void onEventTitleClicked(View view) {
        TextView titleText = (TextView) view;
        try {
            populateEventGrid(EventOrder.byDate, titleText.getId());
        } catch (Exception e) {
            Log.e(TAG, "@onEventTitleClicked: converting generic View to TextView failed");
        }
    }

    //TODO: can add onDescClicked and onDateClicked too,
    // just add tag ids to the textviews and set their listener to onEventTitleClicked

    public void onDeleteClicked(View view) {
        mEventTracker.deleteEvent(lastSelectedEvent);
        lastSelectedEvent = new Event(); // reset selection
        populateEventGrid();
    }

    public void onEditClicked(View view) {

        // we will send a bundle with the event data to edit
        Bundle bundle = new Bundle();
        bundle.putString("title", lastSelectedEvent.getTitle());
        bundle.putString("desc", lastSelectedEvent.getDesc());
        bundle.putInt("priority", lastSelectedEvent.getPriority().value());

        // convert date to string
        // TODO: event class translates date to string instead
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String date = dateFormat.format(lastSelectedEvent.getDate());
        bundle.putString("date", date);

        launchEventFragment(bundle);
    }

    public void onPlusFABClicked(View view) {
        launchEventFragment();
    }

    public void onCancelClicked(View view) {

        // remove addEvent fragment from view
        findViewById(R.id.fragment_container_view)
                .setVisibility(view.INVISIBLE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .remove(fragmentManager.findFragmentById(R.id.fragment_container_view))
                .commit();
    }

    public void onAddEvent(View view) {

        Event event = getEventInfoFromScreen();

        if (event == null) {
            //something went wrong getting data. fragment stays up.
            // getEventInfo... handles the UI alerts
            return;
        }

        mEventTracker.addEvent(event);
        onCancelClicked(view);
        populateEventGrid();
    }

    public void onUpdateEvent(View view) {

        Event event = getEventInfoFromScreen();

        if (event == null) {
            //something went wrong getting data. fragment stays up.
            // getEventInfo... handles the UI alerts
            return;
        }

        mEventTracker.updateEvent(lastSelectedEvent, getEventInfoFromScreen());
        onCancelClicked(view);
        populateEventGrid();
    }

    // SORT SPINNER CALLBACK
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String[] mSpinnerChoices = getResources().getStringArray(R.array.sort_by_array);
        if (adapterView.getItemAtPosition(i).toString()
                .equals(mSpinnerChoices[0])) {
            mEventOrder = EventOrder.byDate;
        } else if (adapterView.getItemAtPosition(i).toString()
                .equals(mSpinnerChoices[1]))  {
            mEventOrder = EventOrder.byPriority;
        } else {
            mEventOrder = EventOrder.byDate;
        }
        populateEventGrid();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // select nothing, do nothing..
    }

    // action for hitting the settings button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //onActionSettingsClicked();
                //will eventually go to settings menu
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // to settings menu. not yet implemented
    public void onActionSettingsClicked() {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }

    /*
    private methods to fill data to screen. This is where
    the controller maps the data from the model to the view.
     */

    private void populateSortSpinner() {
        mSortSpinner = findViewById(R.id.spinnerSortBy);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.sort_by_array,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
                );
        mSortSpinner.setAdapter(adapter);
        mSortSpinner.setOnItemSelectedListener(this);
    }

    private void populateEventGrid() {
        populateEventGrid(mEventOrder);
    }
    private void populateEventGrid(EventOrder order) {
        populateEventGrid(order, 0);
    }
    private void populateEventGrid(EventOrder order, int selectedId) {

        // clear grid
        mGrid.removeAllViews();

        // set 6 equal divisions by weight for formatting
        for (int i = 0; i < 6; i++) {
            TextView spacer = (TextView) getLayoutInflater()
                    .inflate(R.layout.template_grid_spacing, mGrid, false);
            mGrid.addView(spacer);
        }

        // request events from database
        List<Event> events = mEventTracker.getEvents(order);

        // if no events, special message
        if (events == null || events.size() < 1) {
            displayNoEventsMessage();
            return;
        }

        // fill events
        for (Event event : events) {

            // title
            TextView eventName = (TextView) getLayoutInflater()
                            .inflate(R.layout.template_event_title, mGrid, false);
            eventName.setText(event.getTitle());
            eventName.setId(event.getUniqueID()); // tag each title with a unique id, for selection
            mGrid.addView(eventName);

            // date
            TextView eventDate = (TextView) getLayoutInflater()
                    .inflate(R.layout.template_event_date, mGrid, false);
            eventDate.setText(
                    event.getDate() == null ? "date" : event.getDate().toString());
            mGrid.addView(eventDate);

            // description
            TextView eventDesc = (TextView) getLayoutInflater()
                    .inflate(R.layout.template_event_desc, mGrid, false);
            eventDesc.setText(event.getDesc());
            mGrid.addView(eventDesc);

            // toggle if the last event is selected again
            if (event.getUniqueID() == lastSelectedEvent.getUniqueID()) {
                lastSelectedEvent = new Event();
                continue; // skip adding buttons
            }

            // if this is the selected event, add edit and delete buttons under it
            if (event.getUniqueID() == selectedId) {

                lastSelectedEvent = event;

                TextView spacer = (TextView) getLayoutInflater()
                        .inflate(R.layout.template_event_spacer, mGrid, false);
                Button editButton = (Button) getLayoutInflater()
                        .inflate(R.layout.template_edit_button, mGrid, false);
                mGrid.addView(editButton);
                Button deleteButton = (Button) getLayoutInflater()
                        .inflate(R.layout.template_del_button, mGrid, false);
                mGrid.addView(deleteButton);

                // set event title underlined for clarity
                eventName.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            }
        }
    }

    private void displayNoEventsMessage() {

        // title
        TextView eventName = (TextView) getLayoutInflater()
                .inflate(R.layout.template_event_title, mGrid, false);
        eventName.setText(R.string.no_event_title);
        eventName.setClickable(false);
        mGrid.addView(eventName);

        // date
        TextView eventDate = (TextView) getLayoutInflater()
                .inflate(R.layout.template_event_date, mGrid, false);
        eventDate.setText(R.string.no_event_date);
        mGrid.addView(eventDate);

        // description
        TextView eventDesc = (TextView) getLayoutInflater()
                .inflate(R.layout.template_event_desc, mGrid, false);
        eventDesc.setText(R.string.no_event_desc);
        mGrid.addView(eventDesc);

    }

    private Event getEventInfoFromScreen() {

        Event newEvent = new Event();

        EditText eventTitle = findViewById(R.id.editTextNewEventTitle);
        EditText eventDate = findViewById(R.id.editTextDate);
        EditText eventDesc = findViewById(R.id.editTextNewEventDesc);

        Chip lowPriorityChip = findViewById(R.id.chipPriorityLow);
        Chip medPriorityChip = findViewById(R.id.chipPriorityMed);
        Chip highPriorityChip = findViewById(R.id.chipPriorityHigh);

        // convert the date from edit text to a date object
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date(0);
        try {
            date = new Date(dateFormat.parse(eventDate.getText().toString()).getTime());
        } catch (ParseException e) {
            //not expected date format or no date entered
            eventDate.setBackgroundColor(getColor(R.color.light_red));
            eventDate.setText("MM/DD/YYYY");
            return null;
        }

        // check for blank title
        if (eventTitle.getText().toString().equals("")) {
            eventTitle.setBackgroundColor(getColor(R.color.light_red));
            return null;
        }

        // convert the checked priority chip to a Priority object
        Priority priority = Priority.MED;
        if (lowPriorityChip.isChecked()) priority = Priority.LOW;
        if (medPriorityChip.isChecked()) priority = Priority.MED;
        if (highPriorityChip.isChecked()) priority = Priority.HIGH;

        newEvent.setTitle(eventTitle.getText().toString());
        newEvent.setDate(date);
        newEvent.setDesc(eventDesc.getText().toString());
        newEvent.setPriority(priority);

        return newEvent;
    }

    private void launchEventFragment() {
        launchEventFragment(null);
    }
    private void launchEventFragment(Bundle bundle) {

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, NewEventFragment.class, bundle)
                .commit();
        findViewById(R.id.fragment_container_view)
                .setBackgroundColor(Color.parseColor("#EEEEEE"));
        findViewById(R.id.fragment_container_view)
                .setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return true;
    }

    /*
    Permission handling for SMS
     */

    private boolean getSMSPermissions() {
        String smsPermission = Manifest.permission.SEND_SMS;
        if (ContextCompat.checkSelfPermission(this, smsPermission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, smsPermission)) {
            showAccessNotification();
            return false;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{smsPermission}, SMS_REQUEST_CODE);
            return false;
        }
    }

    private boolean hasSMSPermissions() {
        String smsPermission = Manifest.permission.SEND_SMS;
        if (ContextCompat.checkSelfPermission(this, smsPermission)
                != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        return true;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case SMS_REQUEST_CODE: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mNotificationStatus.setText(R.string.notification_status_enable);
                } else {
                }
                return;
            }
            default:
                return;
        }
    }

    public void showAccessNotification() {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }

    public void onNotificationClicked(View view) {
        if (getSMSPermissions()) {
            mNotificationStatus.setText(R.string.notification_status_enable);
        } else {
            mNotificationStatus.setText(R.string.notification_status_disabled);
        }
    }
}
