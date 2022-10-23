package edu.snhu.craigo_eventapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;


public class NewEventFragment extends Fragment {

    // may not need these
    private ChipGroup mPriorityGroup;
    private Chip mPriorityLow;
    private Chip mPriorityMed;
    private Chip mPriorityHigh;

    private EditText eventTitle;
    private EditText eventDate;
    private EditText eventDesc;

    private Button addButton;

    public NewEventFragment() {
        super(R.layout.fragment_add_event);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        // set up toggle chips
        mPriorityGroup = view.findViewById(R.id.chipGroupPriority);
        mPriorityLow = view.findViewById(R.id.chipPriorityLow);
        mPriorityMed = view.findViewById(R.id.chipPriorityMed);
        mPriorityHigh = view.findViewById(R.id.chipPriorityHigh);

        mPriorityGroup.setSingleSelection(true);
        mPriorityMed.setChecked(true);

        Bundle eventData = getArguments();
        // prepopulate data if coming from the edit button
        if (eventData != null) {

            TextView header = view.findViewById(R.id.textNewEventLabel);
            header.setText("Edit Event"); //TODO: ref strings.xml

            eventTitle = view.findViewById(R.id.editTextNewEventTitle);
            eventDate = view.findViewById(R.id.editTextDate);
            eventDesc = view.findViewById(R.id.editTextNewEventDesc);

            String title = eventData.getString("title");
            String date = eventData.getString("date");
            String desc = eventData.getString("desc");
            Integer intPriority = eventData.getInt("priority");
            Priority priority = Priority.fromInt(intPriority);

            eventTitle.setText(title);
            eventDate.setText(date);
            eventDesc.setText(desc);

            // set which chip is checked based on priority
            switch (priority) {
                case LOW:
                    mPriorityLow.setChecked(true);
                    break;
                default:
                case MED:
                    mPriorityMed.setChecked(true);
                    break;
                case HIGH:
                    mPriorityHigh.setChecked(true);
                    break;
            }

            // switch add button for update button
            // i wonder if it is better to make a new nearly identical fragment for update?
            ConstraintLayout eventLayout = (ConstraintLayout) view.findViewById(R.id.newEventConstraint);
            eventLayout.removeView(view.findViewById(R.id.buttonNewEventAdd));

            Button updateButton = (Button) getLayoutInflater()
                    .inflate(R.layout.template_update_button, eventLayout, false);
            eventLayout.addView(updateButton);

        }

        return view;
    }

}
