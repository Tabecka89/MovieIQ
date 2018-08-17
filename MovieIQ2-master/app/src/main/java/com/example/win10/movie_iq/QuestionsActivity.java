package com.example.win10.movie_iq;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QuestionsActivity extends AppCompatActivity {

    // Variables.
    private final int COL_SIZE = 1;
    private GridLayout questionsActivityGrid;
    private ArrayList<Question> questions;
    private User theUser;
    private Soundtrack soundtrack;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        String chosenTier = getIntent().getExtras().getString(getString(R.string.chosen_tier));
        theUser = (User) getIntent().getSerializableExtra(getString(R.string.user));
        questions = (ArrayList<Question>) getIntent().getSerializableExtra(getString(R.string.questions));


        setupGrid(); //Setting up the questions grid.
        getFirebaseAndSoundtrackComponents(); //Calling a method to get firebase and soundtrack components.
        // New Intent with needed elements for the next activity.
        final Intent questionIntent = new Intent(this, GameScreenActivity.class);
        questionIntent.putExtra(getString(R.string.user), theUser);
        questionIntent.putExtra(getString(R.string.soundtrack), soundtrack);
        questionIntent.putExtra(getString(R.string.questions), questions);
        UserTierInfo userTierInfo = theUser.getUserTierInfoByTierName(chosenTier);


        buildGridAndStartNextActivity(userTierInfo, questionIntent);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onBackPressed() {

        //If the user returns back, start previous screen again.
        Intent intent = new Intent(this, TiersActivity.class);
        intent.putExtra(getString(R.string.user), theUser);
        intent.putExtra(getString(R.string.soundtrack), soundtrack);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    private void setupGrid() {

        // Setting up questions grid.
        questionsActivityGrid = findViewById(R.id.questionsActivityGrid);
        questionsActivityGrid.setColumnCount(COL_SIZE);
        questionsActivityGrid.setRowCount(questions.size());

    }

    private void getFirebaseAndSoundtrackComponents() {

        //Initialize firebase and soundtrack components.
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.users));
        soundtrack = (Soundtrack) getIntent().getSerializableExtra(getString(R.string.soundtrack));
    }

    private void buildGridAndStartNextActivity(UserTierInfo userTierInfo, final Intent questionIntent) {
        for (int i = 0; i < questions.size(); i++) {
            final Button bt = new Button(this);
            bt.setText("Question " + (i + 1));

            // If question is answered, show in blue to indicate it.
            if (userTierInfo.checkIfTheQuestionIsAnsweredByAnswer(questions.get(i).getAnswer()))
                bt.setTextColor(Color.BLUE);

            questionIntent.putExtra(getString(R.string.question) + (i + 1), questions.get(i)); // Putting each question in the Intent.

            questionsActivityGrid.addView(bt); // Adding to grid.

            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String chosenBt = bt.getText().toString().toLowerCase().replace(" ", "");
                    questionIntent.putExtra(getString(R.string.chosen_bt), chosenBt);


                    String transMail = theUser.getUserEmail().replace(".", "_");
                    databaseReference.child(transMail).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);// Getting the user from database.
                            if (user.getUserTierInfos() != null)
                                questionIntent.putExtra(getString(R.string.user), user); // Placing the user inside the Intent for the next activity.

                            startActivity(questionIntent); // Starting next activity.

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    }

}
