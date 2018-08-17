package com.example.win10.movie_iq;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {


    // Variables
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputUsername;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private final int MIN_PASS_LENGTH = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getFirebaseComponents(); // Calling method to arrange firebase components.
        initializeVariables(); // Calling method to arrange variables.

    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE); //If activity is resumed, make the progress bar invisible.
    }

    public void onRegisterClicked(View view) {
        // Variables needed for user creation.
        final String emailInput = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        final String username = inputUsername.getText().toString().trim();

        // Prompts for the user if input is lackluster.
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), R.string.enter_username, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(emailInput)) {
            Toast.makeText(getApplicationContext(), R.string.enter_email, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), R.string.enter_password, Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < MIN_PASS_LENGTH) {
            Toast.makeText(getApplicationContext(), R.string.minimum_password, Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE); // Setting progress bar to visible.

        createUser(emailInput, password, username); // Calling method to create a new user.
    }

    private void getFirebaseComponents() {
        mAuth = FirebaseAuth.getInstance(); // Getting an instance of firebase authorization.
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.users)); // Getting a reference to one of our sub-trees within the database.
    }

    private void initializeVariables() {

        // Variables initialization.
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputUsername = (EditText) findViewById(R.id.username);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void createUser(final String emailInput, String password, final String username) {
        mAuth.createUserWithEmailAndPassword(emailInput, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, R.string.auth_failed + "" + task.getException(),
                                    Toast.LENGTH_LONG).show();
                        } else { // If sign in succeeds, create a new user and add it to the database.


                            Intent intent = new Intent(RegisterActivity.this, StartGameActivity.class);


                            String userID = databaseReference.push().getKey();
                            User theUser = new User(emailInput, username, userID);
                            String transMail = theUser.getUserEmail().replace(".", "_");
                            databaseReference.child(transMail).setValue(theUser);
                            intent.putExtra(getString(R.string.user), theUser);

                            startActivity(intent);
                            finish();

                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        String TAG = getIntent().getExtras().getString("TAG");
        if (TAG != null && TAG.equals("StartGameActivity"))
            return;
        else
            finish();
    }

    public void onLoginClicked(View view) {
        startActivity(new Intent(this, LoginActivity.class)); // If user is already logged in, navigate to the login page.
    }
}
