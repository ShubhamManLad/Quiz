package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.List;

public class AddQuestion extends AppCompatActivity {

    private EditText newQuestion_editText;
    private EditText newOption1_editText;
    private EditText newOption2_editText;
    private EditText newOption3_editText;

    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;

    private Spinner newCategory_spinner;
    private Spinner newDifficulty_spinner;
    private Button addQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        Context context = this;

        newQuestion_editText = findViewById(R.id.question_editText);
        newOption1_editText = findViewById(R.id.option1_editText);
        newOption2_editText = findViewById(R.id.option2_editText);
        newOption3_editText = findViewById(R.id.option3_editText);

        rbGroup = findViewById(R.id.AnswerRadioGroup);
        rb1 = findViewById(R.id.option1_rb);
        rb2 = findViewById(R.id.option2_rb);
        rb3 = findViewById(R.id.option3_rb);

        newCategory_spinner = findViewById(R.id.categorySelect_spinner);
        newDifficulty_spinner = findViewById(R.id.difficultySelect_spinner);

        addQuestion = findViewById(R.id.qAdd_button);

        loadAddCategories();
        loadAddDifficultyLevels();

        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = newQuestion_editText.getText().toString();
                String option1 = newOption1_editText.getText().toString();
                String option2 = newOption2_editText.getText().toString();
                String option3 = newOption3_editText.getText().toString();

                RadioButton rbselected = findViewById(rbGroup.getCheckedRadioButtonId());
                int userAnswer = rbGroup.indexOfChild(rbselected) + 1;

                String solution = "Correct ans "+ userAnswer;

                Category category = (Category) newCategory_spinner.getSelectedItem();
                int categoryID = category.getId();

                String difficulty = newDifficulty_spinner.getSelectedItem().toString();

                Question newQuestion = new Question(question , option1, option2, option3,userAnswer, solution, difficulty, categoryID );

                QuizDbHelper.getInstance(context).addQuestion(newQuestion);

                finish();
            }
        });



    }

    private void loadAddCategories() {

        QuizDbHelper dbHelper = QuizDbHelper.getInstance(this);

        List<Category> categories = dbHelper.getAllCategories();

        ArrayAdapter<Category> adapterCategories = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,categories);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newCategory_spinner.setAdapter(adapterCategories);

    }

    private void loadAddDifficultyLevels() {
        String[] difficultyLevels = Question.getAllDifficultyLevels();

        ArrayAdapter<String> adapterDifficulty = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,difficultyLevels);
        adapterDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newDifficulty_spinner.setAdapter(adapterDifficulty);

    }
}