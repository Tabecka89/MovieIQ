package com.example.win10.movie_iq;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    // Variables.
    private EditText inputEmail, inputPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private Intent intent;
    private final int MIN_PASS_LENGTH = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);

        getFirebaseComponents(); // Calling method to arrange firebase components.
        initializeVariables(); // Calling method to arrange variables.
        intent = new Intent(LoginActivity.this, StartGameActivity.class);


    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE); //If activity is resumed, make the progress bar invisible.
    }

    public void loginButtonClicked(View view) {

        // Variables extracted from the edit text.
        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        // Prompt for the user if input is lackluster.
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), R.string.enter_email, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), R.string.enter_password, Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE); // Setting progress bar to visible.
        authenticateUser(email, password); // Calling a method to authenticate the user.

    }

    private void getFirebaseComponents() {
        mAuth = FirebaseAuth.getInstance(); // Getting an instance of firebase authorization.
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.users)); // Getting a reference to one of our sub-trees within the database.
    }

    private void initializeVariables() {

        // Variables initialization.
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void authenticateUser(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user.
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < MIN_PASS_LENGTH) {
                                inputPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else { //If sign in succeeds, get the user from the database and put it in Intent for the next activity.

                            String transMail = email.replace(getString(R.string.dot), getString(R.string.underscore));
                            databaseReference.child(transMail).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User theUser = dataSnapshot.getValue(User.class);

                                    intent.putExtra(getString(R.string.user), theUser);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
