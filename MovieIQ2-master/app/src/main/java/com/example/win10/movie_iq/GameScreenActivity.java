package com.example.win10.movie_iq;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GameScreenActivity extends AppCompatActivity {

    private static final String TAG = "GameScreenActivity";
    private static final int REDUCED_POINTS = 2;

    private TextView questionText;
    private TextView hintText1;
    private TextView hintText2;
    private TextView hintText3;
    private TextView pointsText;
    private TextView tierText;
    private EditText answerEditText;
    private Question theQuestion;
    private Button hintBtn;
    private Button submitBtn;
    private Button factsBtn;
    private Button clipBtn;
    private boolean[] isHint = new boolean[3];
    private ArrayList<String> solutions;
    private User theUser;
    private UserTierInfo userTierInfo;
    private int numOfHintsTaken;
    private ArrayList<Question> questions;
    private Integer currentPoints;
    private Soundtrack soundtrack;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initializing different elements that are needed in the activity.
        theUser = (User) getIntent().getSerializableExtra(getString(R.string.user));
        questions = (ArrayList<Question>) getIntent().getSerializableExtra(getString(R.string.questions));
        final String email = theUser.getUserEmail().replace(getString(R.string.dot), getString(R.string.underscore));
        String chosenBt = getIntent().getExtras().getString(getString(R.string.chosen_bt));
        theQuestion = (Question) getIntent().getSerializableExtra(chosenBt);
        solutions = theQuestion.getSolutions();


        getFirebaseAndSoundtrackComponents(); // Getting firebase and soundtrack components.
        initializeVariables();

        userTierInfo = theUser.getUserTierInfoByTierName(tierText.getText().toString().toLowerCase().replace(" ", "")); // Getting user tier info by the tier that the user is currently on.
        setCurrentPoints(userTierInfo); // Setting the current points based on data in the user tier info object.
        numOfHintsTaken = userTierInfo.getNumOfHintsTaken(theQuestion.getAnswer()); // Getting number of hints taken from the user tier info.
        exposeHints(numOfHintsTaken); // Exposing hints based on number of hints taken.
        checkAnsweredQuestion(userTierInfo); // Calling a method to check if the current question was answered.


        // Listeners for each button.
        clipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ClipIntent = new Intent(getApplicationContext(), ClipActivity.class);
                ClipIntent.putExtra(getString(R.string.question), theQuestion);
                ClipIntent.putExtra(getString(R.string.soundtrack), soundtrack);
                startActivity(ClipIntent);
            }
        });

        hintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = numOfHintsTaken; i < isHint.length; i++) {
                    String hint = theQuestion.getHints().get(i); // Getting hint.
                    if (isHint[i] == false) { // If hint wasn't used prior.
                        currentPoints -= REDUCED_POINTS; // Reduce 2 points.
                        userTierInfo.getCurrentPointsForQuestion().put(theQuestion.getAnswer(), currentPoints); // Update points value in the user object.


                        userTierInfo.addHintTakedIndexed(theQuestion.getAnswer()); // Add number of hints taken.
                        databaseReference.child(email).setValue(theUser); // Update database with the relevant number of hints and current points for the question.


                        // Setting the text in one of the text views.
                        pointsText.setText(getString(R.string.points) + Integer.toString(currentPoints));
                        if (i == 0)
                            hintText1.setText(hint);
                        else if (i == 1)
                            hintText2.setText(hint);
                        else if (i == 2)
                            hintText3.setText(hint);
                        isHint[i] = true;
                        break;
                    }
                }
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answer = answerEditText.getText().toString();
                for (int i = 0; i < solutions.size(); i++) {
                    if (answer.equalsIgnoreCase(solutions.get(i))) { //If answer is correct.

                        Toast.makeText(getApplicationContext(), getString(R.string.well_done), Toast.LENGTH_SHORT).show(); // Success message for the user.
                        theUser.setTotalPoints(currentPoints); // Updating total points.
                        determineRank(theUser); // Determining rank based on total points.
                        userTierInfo.addAnsweredQuestion(theQuestion); // Adding question that was answered to the user.
                        databaseReference.child(email).setValue(theUser); // Updating database.
                        submitBtn.setEnabled(false); // Disabling submit button.
                        hintBtn.setEnabled(false); // Disabling hint button.
                        factsBtn.setEnabled(true); // Enabling facts button as reward..
                        clipBtn.setEnabled(true); // Enabling clip button as reward.

                        break;
                    }
                    else
                        Toast.makeText(getApplicationContext(), getString(R.string.failure), Toast.LENGTH_SHORT).show(); // If failure, show failure prompt.
                }

            }
        });

        factsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent factsIntent = new Intent(getApplicationContext(), FactsActivity.class);
                factsIntent.putExtra(getString(R.string.question), theQuestion);
                startActivity(factsIntent);
            }
        });


    }

    private void initializeVariables() {

        // Initialize and setup variables.
        hintBtn = findViewById(R.id.hintButton);
        submitBtn = findViewById(R.id.submitButton);
        factsBtn = findViewById(R.id.raiseIQButton);
        answerEditText = findViewById(R.id.answerEditText);
        tierText = findViewById(R.id.tierTextView);
        questionText = findViewById(R.id.questionTextView);
        hintText1 = findViewById(R.id.hintTextView1);
        hintText2 = findViewById(R.id.hintTextView2);
        hintText3 = findViewById(R.id.hintTextView3);
        pointsText = findViewById(R.id.pointTextView);
        clipBtn = findViewById(R.id.clipButton);


        factsBtn.setEnabled(false);
        clipBtn.setEnabled(false);

        questionText.setText(theQuestion.getQuestion());
        tierText.setText(getString(R.string.tier) + " " + Integer.toString(theQuestion.getTier()));

    }


    private void setCurrentPoints(UserTierInfo userTierInfo) {
        currentPoints = userTierInfo.getCurrentPointsForQuestion().get(theQuestion.getAnswer());
        if (currentPoints == null)
            currentPoints = theQuestion.getCurrentPoints();
        pointsText.setText(getString(R.string.points) + " " + currentPoints);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, QuestionsActivity.class);
        String chosenTier = tierText.getText().toString().toLowerCase().replace(" ", "");
        intent.putExtra(getString(R.string.chosen_tier), chosenTier);
        intent.putExtra(getString(R.string.user), theUser);
        intent.putExtra(getString(R.string.questions), questions);
        intent.putExtra(getString(R.string.soundtrack), soundtrack);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        exposeHints(numOfHintsTaken);
    }

    public void exposeHints(int limit) {
        for (int k = 0; k < limit; k++)
            isHint[k] = true;
        int i = 0;
        while (i < isHint.length && isHint[i] == true) {
            String hint = theQuestion.getHints().get(i);
            pointsText.setText(getString(R.string.points) + " " + Integer.toString(currentPoints));
            if (i == 0)
                hintText1.setText(hint);
            else if (i == 1)
                hintText2.setText(hint);
            else if (i == 2)
                hintText3.setText(hint);
            i++;
        }
    }

    private void checkAnsweredQuestion(UserTierInfo userTierInfo) {
        if (userTierInfo.getAnsweredQuestions() != null) {
            for (int i = 0; i < userTierInfo.getAnsweredQuestions().size(); i++) {
                if (theQuestion.getAnswer().equalsIgnoreCase(userTierInfo.getAnsweredQuestions().get(i).getAnswer())) {
                    answerEditText.setFocusable(false);
                    questionText.setText(getString(R.string.answer_prompt) + "\n\n" + theQuestion.getAnswer() + "\n\n" + getString(R.string.well_done));
                    hintBtn.setEnabled(false);
                    submitBtn.setEnabled(false);
                    factsBtn.setEnabled(true);
                    clipBtn.setEnabled(true);

                    break;
                }
            }
        }
    }

    // A method to determine rank based on total points collected.
    private void determineRank(User theUser) {
        if (theUser.getTotalPoints() >= 50 && theUser.getTotalPoints() < 100)
            theUser.setRank(getString(R.string.rank_2));
        else if (theUser.getTotalPoints() >= 100 && theUser.getTotalPoints() < 150)
            theUser.setRank(getString(R.string.rank_3));
        else if (theUser.getTotalPoints() >= 150 && theUser.getTotalPoints() < 200)
            theUser.setRank(getString(R.string.rank_4));
        else if (theUser.getTotalPoints() >= 200 && theUser.getTotalPoints() < 250)
            theUser.setRank(getString(R.string.rank_5));
        else if (theUser.getTotalPoints() >= 250 && theUser.getTotalPoints() < 300)
            theUser.setRank(getString(R.string.rank_6));
        else if (theUser.getTotalPoints() >= 300 && theUser.getTotalPoints() < 350)
            theUser.setRank(getString(R.string.rank_7));
        else if (theUser.getTotalPoints() >= 350 && theUser.getTotalPoints() < 400)
            theUser.setRank(getString(R.string.rank_8));
        else if (theUser.getTotalPoints() >= 400 && theUser.getTotalPoints() < 450)
            theUser.setRank(getString(R.string.rank_9));
        else if (theUser.getTotalPoints() >= 450 && theUser.getTotalPoints() <= 500)
            theUser.setRank(getString(R.string.rank_10));

    }


    private void getFirebaseAndSoundtrackComponents() {

        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.users));
        soundtrack = (Soundtrack)getIntent().getSerializableExtra(getString(R.string.soundtrack));

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}


