package edu.snhu.craigo_eventapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    EventTracker mEventTracker;

    Button mLoginButton;
    EditText mUsernameField;
    EditText mPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //TODO:
        // if logged in from bundle, move immediately to grid

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // no action bar on login screen
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // get view components
        mLoginButton = findViewById(R.id.buttonLogin);
        mUsernameField = findViewById(R.id.editTextUsername);
        mPasswordField = findViewById(R.id.editTextPassword);

        // load the model
        mEventTracker = EventTracker.getInstance(this);
    }

    public void onLoginClicked(View view) {

        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();

        switch (mEventTracker.Login(username, password)) {

            case LOGIN_OK:
                loginSuccess();
                break;

            default:
            case LOGIN_NO_USER:
                makeSnack(getString(R.string.login_no_user_message));
                break;

            case LOGIN_BAD_PASSWORD:
                makeSnack(getString(R.string.login_incorrect_password));
                break;
        }
    }

    public void onCreateAccountClicked(View view) {

        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();

        // TODO: check for empty or too short password here

        if (mEventTracker.CreateAccount(username, password)) {
            makeSnack(getString(R.string.create_user_successful));

        } else {
            makeSnack(getString(R.string.create_username_taken));
        }
    }

    public void loginSuccess() {
        Intent intent = new Intent(this, EventGridActivity.class);
        startActivity(intent);
    }

    public void makeSnack(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout),
                message, Snackbar.LENGTH_LONG);
        snackbar.show();

    }
}