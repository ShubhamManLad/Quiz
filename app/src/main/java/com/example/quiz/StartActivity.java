package com.example.quiz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class StartActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_QUIZ = 1;
    public static final String EXTRA_CATEGORY_ID = "extraCategoryID";
    public static final String EXTRA_CATEGORY_NAME = "extraCategoryName";
    public static final String EXTRA_DIFFICULTY = "extraDifficulty";

    public static final String SHARED_PREF = "sharedPreference";
    public static final String KEY_HIGHSCORE = "keyHighscore";


    private TextView highScore_text;
    private int highScore;

    private Spinner category_spinner;
    private Spinner difficulty_spinner;

    private Button add_category_button;
    private Button add_question_button;

    Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        highScore_text = findViewById(R.id.highscore_textView);
        loadHighScore();

        category_spinner = findViewById(R.id.category_spinner);
        difficulty_spinner = findViewById(R.id.difficulty_spinner);

        add_category_button = findViewById(R.id.addC_button);
        add_question_button = findViewById(R.id.addQ_button);

        add_category_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, AddCategory.class);
                startActivity(intent);
            }
        });

        add_question_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, AddQuestion.class);
                startActivity(intent);
            }
        });

        loadCategories();
        loadDifficultyLevels();

        start = findViewById(R.id.start_button);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });

    }

    private void loadCategories() {

        QuizDbHelper dbHelper = QuizDbHelper.getInstance(this);

        List<Category> categories = dbHelper.getAllCategories();

        ArrayAdapter<Category> adapterCategories = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,categories);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setAdapter(adapterCategories);

    }

    private void loadDifficultyLevels() {
        String[] difficultyLevels = Question.getAllDifficultyLevels();

        ArrayAdapter<String> adapterDifficulty = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,difficultyLevels);
        adapterDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficulty_spinner.setAdapter(adapterDifficulty);

    }

    private void startQuiz() {

        Category selectedCategory = (Category) category_spinner.getSelectedItem();
        int categoryID =  selectedCategory.getId();
        String categoryName = selectedCategory.getName();
        String difficulty = difficulty_spinner.getSelectedItem().toString();


        Intent intent = new Intent(StartActivity.this, QuizActivity.class);
        intent.putExtra(EXTRA_CATEGORY_ID,categoryID);
        intent.putExtra(EXTRA_CATEGORY_NAME,categoryName);
        intent.putExtra(EXTRA_DIFFICULTY,difficulty);
        startActivityForResult(intent,REQUEST_CODE_QUIZ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_QUIZ){
            if (resultCode == RESULT_OK){
                int score = data.getIntExtra(QuizActivity.EXTRA_SCORE,0);
                if (score>highScore){
                    updateHighscore(score);
                }
            }
        }
    }

    private void loadHighScore(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        highScore = sharedPreferences.getInt(KEY_HIGHSCORE,0);
        highScore_text.setText("HighScore: "+highScore);
    }

    private void updateHighscore(int score) {
        highScore = score;
        highScore_text.setText("HighScore: "+highScore);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_HIGHSCORE,score);
        editor.apply();
    }
}