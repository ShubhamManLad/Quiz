package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddCategory extends AppCompatActivity {

    private EditText newCategory_editText;
    private Button addCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        newCategory_editText = findViewById(R.id.category_editText);
        addCategory = findViewById(R.id.cAdd_button);

        Context context = this;

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category_name = newCategory_editText.getText().toString();

                Category category = new Category(category_name);

                QuizDbHelper.getInstance(context).addCategory(category);

                Toast.makeText(context, "Restart the app to see the changes", Toast.LENGTH_SHORT).show();

                finish();
            }
        });



    }
}