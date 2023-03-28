package com.example.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE = "extraScore";

    private static final long COUNTDOWN_IN_MILLIS = 30000;

    // For restoring from Saved Instances
    private static final String KEY_SCORE = "keyScore";
    private static final String KEY_QUESTION_COUNT = "keyQuestionCount";
    private static final String KEY_TIME_LEFT = "keyTimeLeft";
    private static final String KEY_ANSWERED = "keyAnswered";
    private static final String KEY_QUESTION_LIST = "keyQuestionList";


    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewCategory;
    private TextView textViewDifficulty;
    private TextView textViewTimer;
    private TextView textViewSolution;

    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;

    private Button confirmNext;

    private ColorStateList textColorDefault;
    private ColorStateList timerColorDefault;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private ArrayList<Question> questionList;
    private int questionCounter = 0;
    private int questionTotal;
    private Question currentQuestion;

    private int score;
    private boolean answered;

    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        textViewQuestion = findViewById(R.id.question_textView);
        textViewScore = findViewById(R.id.score_textView);
        textViewQuestionCount = findViewById(R.id.count_textView);
        textViewCategory = findViewById(R.id.category_textView);
        textViewDifficulty = findViewById(R.id.difficulty_textView);
        textViewTimer = findViewById(R.id.timer_textView);
        textViewSolution = findViewById(R.id.solution_textView);

        rbGroup = findViewById(R.id.radioGroup);
        rb1 = findViewById(R.id.option1_button);
        rb2 = findViewById(R.id.option2_button);
        rb3 = findViewById(R.id.option3_button);

        confirmNext = findViewById(R.id.confirm_button);

        textColorDefault = rb1.getTextColors();
        timerColorDefault = textViewTimer.getTextColors();

        Intent intent = getIntent();
        int categoryID = intent.getIntExtra(StartActivity.EXTRA_CATEGORY_ID,0);
        String categoryName = intent.getStringExtra(StartActivity.EXTRA_CATEGORY_NAME);
        String difficulty = intent.getStringExtra(StartActivity.EXTRA_DIFFICULTY);

        textViewCategory.setText("Category : "+categoryName);
        textViewDifficulty.setText("Difficulty : "+ difficulty);

        if (savedInstanceState == null) {
            //QuizDbHelper dbHelper = new QuizDbHelper(this);
            QuizDbHelper dbHelper = QuizDbHelper.getInstance(this);

            questionList = dbHelper.getQuestions(categoryID, difficulty);
            questionTotal = questionList.size();
            //Collections.shuffle(questionList);
            showNextQuestion();
        }
        else{
            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);
            questionTotal = questionList.size();
            questionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNT);
            currentQuestion = questionList.get(questionCounter -1);
            score = savedInstanceState.getInt(KEY_SCORE);
            timeLeftInMillis = savedInstanceState.getLong(KEY_TIME_LEFT);
            answered = savedInstanceState.getBoolean(KEY_ANSWERED);

            if (!answered) {
                startCounter();
            }
            else{
                updateTimer();
                showSolution();
            }

        }

        confirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answered) {
                    if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked()) {
                        checkAnswer();
                    } else {
                        showNextQuestion();
                    }
                } else {
                    showNextQuestion();
                }
            }
        });


    }

    private void showNextQuestion() {

        rb1.setTextColor(textColorDefault);
        rb2.setTextColor(textColorDefault);
        rb3.setTextColor(textColorDefault);
        rbGroup.clearCheck();

        if (questionCounter < questionTotal) {
            currentQuestion = questionList.get(questionCounter);
            textViewQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());

            questionCounter++;
            textViewQuestionCount.setText("Question : " + questionCounter + " / " + questionTotal);
            confirmNext.setText("Confirm");
            answered = false;
            textViewSolution.setVisibility(View.INVISIBLE);

            timeLeftInMillis = COUNTDOWN_IN_MILLIS;
            startCounter();
        } else {
            finishQuiz();
        }

    }

    private void startCounter() {

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();

            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateTimer();
                checkAnswer();

            }
        }.start();


    }

    private void updateTimer() {
        int mins = (int) (timeLeftInMillis / 1000) / 60;
        int secs = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);

        textViewTimer.setText(timeFormatted);

        if (timeLeftInMillis < 10000) {
            textViewTimer.setTextColor(Color.RED);
        } else {
            textViewTimer.setTextColor(timerColorDefault);
        }
    }

    private void checkAnswer() {

        answered = true;

        countDownTimer.cancel();

        RadioButton rbselected = findViewById(rbGroup.getCheckedRadioButtonId());
        int userAnswer = rbGroup.indexOfChild(rbselected) + 1;
        if (userAnswer == currentQuestion.getAnswer()) {
            score++;
            textViewScore.setText("Score : " + score);
        }

        showSolution();

    }

    private void showSolution() {
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);

        textViewSolution.setVisibility(View.VISIBLE);
        textViewSolution.setText(currentQuestion.getSolution());

        switch (currentQuestion.getAnswer()) {
            case 1:
                rb1.setTextColor(Color.GREEN);
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                break;

        }

        if (questionCounter < questionTotal) {
            confirmNext.setText("Next");
        } else {
            confirmNext.setText("Finish");
        }
    }

    private void finishQuiz() {

        Intent resultIntent = new Intent();

        resultIntent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, resultIntent);

        finish();

    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishQuiz();
        } else {
            Toast.makeText(this, "Press Back Again to Exit the Quiz", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCORE, score);
        outState.putInt(KEY_QUESTION_COUNT, questionCounter);
        outState.putLong(KEY_TIME_LEFT, timeLeftInMillis);
        outState.putBoolean(KEY_ANSWERED, answered);
        outState.putParcelableArrayList(KEY_QUESTION_COUNT, questionList);
    }
}