package com.example.win10.movie_iq;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TiersActivity extends AppCompatActivity {

    // Variables.
    private static final String TAG = "TiersActivity";
    private final static int QUESTION_ARR_SIZE = 10;
    private static final int NUM_OF_TIERS = 5;
    private static final int NEXT_TIER_LIMIT = 5;
    private Soundtrack soundtrack;

    private ArrayList<Question> questions;
    private DatabaseReference databaseReference;
    private DatabaseReference userReference;


    private ProgressBar prg;
    private int progress;
    private User theUser;
    private TextView userTextView;
    private TextView rankTextView;

    private Button btTier1;
    private Button btTier2;
    private Button btTier3;
    private Button btTier4;
    private Button btTier5;
    private Button[] tiersBtnArr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiers);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        questions = new ArrayList<>();
        theUser = (User) getIntent().getSerializableExtra(getString(R.string.user));

        getFirebaseAndSoundtrackComponents(); // Calling method to arrange firebase and soundtrack components.
        initializeVariables(); // Calling method to arrange variables.
        showHighscoreAndRank(); //Calling a method to display user name, highscore, and rank.
    }


    public void onClick(View view) {

        //Variables.
        final Intent intent = new Intent(this, QuestionsActivity.class);
        final Button clickedBt = findViewById(view.getId());
        final String chosenTier = clickedBt.getText().toString().toLowerCase().replace(" ", "");

        lockOrEnableAllTiers(tiersBtnArr, false); //Calling a method to disable clicking on other buttons.

        // If the user clicked on any tiers that's not the first, check if it's eligible for opening,
        // and if not, display a message.
        if (!chosenTier.equalsIgnoreCase(getString(R.string.tier_1))) {
            if (!checkEligible(chosenTier, clickedBt)) {
                Toast.makeText(getApplicationContext(), getString(R.string.tier_blocked), Toast.LENGTH_LONG).show();
                lockOrEnableAllTiers(tiersBtnArr, true);
                return;
            }
        }

        questions.clear();
        prg.setVisibility(View.VISIBLE);
        prg.setProgress(0);

        // Get 10 questions for the tier that was chosen, and start the next activity.
        getQuestionsFromDatabaseAndStartNextActivity(chosenTier, intent);
    }

    private void getQuestionsFromDatabaseAndStartNextActivity(final String chosenTier, final Intent intent) {

        // A loop that reaches to the database and adds them to an ArrayList.
        for (int i = 1; i <= QUESTION_ARR_SIZE; i++) {
            databaseReference.child(chosenTier).child(Integer.toString(i - 1)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Question q = new Question();
                    q = dataSnapshot.getValue(Question.class);
                    questions.add(q);

                    progress += 100 / QUESTION_ARR_SIZE;
                    prg.setProgress(progress);


                    if (questions.size() == QUESTION_ARR_SIZE) {
                        intent.putExtra(getString(R.string.questions), questions);


                        // Adding chosen tier information to the user + updating the user entry within the database.
                        UserTierInfo userTierInfo = new UserTierInfo(chosenTier);
                        if (!theUser.isExistUserTierInfo(userTierInfo)) {
                            theUser.addUserTierInfo(userTierInfo);
                            userReference.child(theUser.getUserEmail().replace(getString(R.string.dot), getString(R.string.underscore))).setValue(theUser);
                        }


                        // Starting the next activity with the needed variables and values.
                        intent.putExtra(getString(R.string.user), theUser);
                        intent.putExtra(getString(R.string.chosen_tier), chosenTier);
                        intent.putExtra(getString(R.string.soundtrack), soundtrack);
                        startActivity(intent);
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }


    public void onLogout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        prg.setProgress(0);
        prg.setVisibility(View.INVISIBLE);
        theUser = (User) getIntent().getSerializableExtra(getString(R.string.user));
    }

    @Override
    public void onBackPressed() {

        //If the user returns back, start previous screen again.
        Intent intent = new Intent(this, StartGameActivity.class);
        intent.putExtra(getString(R.string.user), theUser);
        intent.putExtra("TAG", TAG);
        intent.putExtra(getString(R.string.soundtrack), soundtrack);
        startActivity(intent);
        finish();
    }

    // A method that locks all tier buttons.
    private void lockOrEnableAllTiers(Button[] arr, boolean value) {
        for (int i = 0; i < arr.length; i++)
            arr[i].setClickable(value);
    }


    // A method that checks if a certain tier can be opened or not.
    private boolean checkEligible(String chosenTier, Button clickedBt) {
        int t = Character.getNumericValue(chosenTier.charAt(chosenTier.length() - 1)) - 1;
        String prevTier = getString(R.string.tier) + t;
        if (theUser.getUserTierInfos() != null) {
            UserTierInfo uti = theUser.getUserTierInfoByTierName(prevTier);
            if (uti != null) {
                if (uti.getAnsweredQuestions().size() > NEXT_TIER_LIMIT) {
                    return true;

                }
            }
        }
        return false;
    }


    private void getFirebaseAndSoundtrackComponents() {

        //Initialize firebase and soundtrack components.
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.questions));
        userReference = FirebaseDatabase.getInstance().getReference(getString(R.string.users));
        soundtrack = (Soundtrack)getIntent().getSerializableExtra(getString(R.string.soundtrack));
    }

    private void initializeVariables() {

        // Initialize relevant variables.
        prg = findViewById(R.id.progressBarTiers);
        prg.setVisibility(View.INVISIBLE);
        progress = 0;

        btTier1 = findViewById(R.id.tier1);
        btTier2 = findViewById(R.id.tier2);
        btTier3 = findViewById(R.id.tier3);
        btTier4 = findViewById(R.id.tier4);
        btTier5 = findViewById(R.id.tier5);

        tiersBtnArr = new Button[NUM_OF_TIERS];
        tiersBtnArr[0] = btTier1;
        tiersBtnArr[1] = btTier2;
        tiersBtnArr[2] = btTier3;
        tiersBtnArr[3] = btTier4;
        tiersBtnArr[4] = btTier5;
    }

    private void showHighscoreAndRank() {

        // Displayes user name, highscore, and rank.
        userTextView = findViewById(R.id.userTextView);
        rankTextView = findViewById(R.id.rankTextView);
        userTextView.setText(getString(R.string.welcome) + " " + theUser.getName() + getString(R.string.highscore) + " " + theUser.getTotalPoints());
        rankTextView.setText(theUser.getRank());
    }


}
