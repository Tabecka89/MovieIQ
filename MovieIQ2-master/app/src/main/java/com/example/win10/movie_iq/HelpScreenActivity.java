package com.example.win10.movie_iq;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HelpScreenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 5 text views for the tips and help pointers.
        TextView helpTextView = findViewById(R.id.helpTextView);
        TextView tip1TextView = findViewById(R.id.tip1TextView);
        TextView tip2TextView = findViewById(R.id.tip2TextView);
        TextView tip3TextView = findViewById(R.id.tip3TextView);
        TextView tip4TextView = findViewById(R.id.tip4TextView);

        // Assigning values to String variables.
        String howToPlay = " The game is made out of 5 different tiers, designed to be increasingly difficult as you go along. "
                + "You'll be presented with a vague movie description, and all you have to do is figure it out and write it in the text box! A few pointers:";
        String tip1 = "1) There are 3 hints to each movie.If the question is too hard you can always get a hint, but "
                + "bear in mind that the score for that particular question will decrease!";
        String tip2 = "2) Abbreviations are not allowed. For example, let's say a movie description is presented, "
                + "and you figure out the answer is 'The Lord of the Rings', writing 'LOTR' won't do. You need to type the whole name.";
        String tip3 = " 3) Don't write the entire movie name if it's a part of a franchise. If we continue with our 'The Lord of the Rings' example, "
                + "don't write 'The Lord of the Rings - The Return of the King' to get it right. Just writing 'The Lord of the Rings' will do just fine!";
        String tip4 = "4) Lastly, there's no need to include 'The' in the answer. Writing 'Lord of the Rings' instead of 'The Lord of the Rings' is perfectly fine.";

        // Setting text to text views.
        helpTextView.setText(howToPlay);
        tip1TextView.setText(tip1);
        tip2TextView.setText(tip2);
        tip3TextView.setText(tip3);
        tip4TextView.setText(tip4);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
