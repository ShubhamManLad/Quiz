package com.example.quiz;

import android.provider.BaseColumns;

// This is a just container for these constants

public class QuizContract {

    private QuizContract(){
        // So that we can't create an object of this class by accident
    }


    public static class CategoriesTable implements BaseColumns{
        public static final String TABLE_NAME = "quiz_categories";
        public static final String COLUMN_NAME = "name";
    }

    // BaseColumn adds IDs and Count to the table which auto increments
    public static class QuestionsTable implements BaseColumns{
        // public -> access outside of this class
        // static -> access without needing an instance of this table
        // final  -> we don't want to change them
        public static final String  TABLE_NAME = "quiz_questions";
        public static final String  COLUMN_QUESTION = "question";
        public static final String  COLUMN_OPTION1 = "option1";
        public static final String  COLUMN_OPTION2 = "option2";
        public static final String  COLUMN_OPTION3 = "option3";
        public static final String  COLUMN_ANSWER = "answer";
        public static final String  COLUMN_SOLUTION = "solution";
        public static final String  COLUMN_DIFFICULTY = "difficulty";
        public static final String  COLUMN_CATEGORY_ID = "category_id";

    }



}
