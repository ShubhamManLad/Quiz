package com.example.quiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class QuizDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Quiz.db";
    public static final int DATABASE_VERSION = 1;

    // Since we are accessing our database from multiple activities,
    // we open multiple data connections which causes memory leak
    // To prevent this we make our class singleton

    private static QuizDbHelper instance;

    private SQLiteDatabase db;  // Hold the reference to actual DataBase

    private QuizDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized QuizDbHelper getInstance(Context context){

        if (instance == null){
            instance = new QuizDbHelper(context.getApplicationContext());
        }
        return instance;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        this.db = db;

        final String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " +
                QuizContract.CategoriesTable.TABLE_NAME + " ( "+
                QuizContract.CategoriesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                QuizContract.CategoriesTable.COLUMN_NAME + " TEXT "+ ")";

        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                QuizContract.QuestionsTable.TABLE_NAME + " ( " +
                QuizContract.QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuizContract.QuestionsTable.COLUMN_QUESTION + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_ANSWER + " INTEGER, " +
                QuizContract.QuestionsTable.COLUMN_SOLUTION + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_DIFFICULTY + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_CATEGORY_ID + " INTEGER, " +
                "FOREIGN KEY(" + QuizContract.QuestionsTable.COLUMN_CATEGORY_ID +
                ") REFERENCES " + QuizContract.CategoriesTable.TABLE_NAME + "("+
                QuizContract.CategoriesTable._ID + ")" + "ON DELETE CASCADE" + ")";

        db.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);

        fillCategories();
        fillQuestions();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // If any updates are made in the table schema we need to update
        // the database version and run the onUpgrade method
        // or we can uninstall and reinstall the app
        db.execSQL("DROP TABLE IF EXISTS "+ QuizContract.CategoriesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ QuizContract.QuestionsTable.TABLE_NAME);
        onCreate(db);

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    public void addCategory(Category category){

        db = getWritableDatabase();
        insertCategory(category);

    }

    private void insertCategory(Category category) {

        ContentValues cv = new ContentValues();
        cv.put(QuizContract.CategoriesTable.COLUMN_NAME,category.getName());

        db.insert(QuizContract.CategoriesTable.TABLE_NAME,null,cv);
    }

    private void fillCategories() {
        Category category1 = new Category("Programming");
        insertCategory(category1);
        Category category2 = new Category("Geography");
        insertCategory(category2);
        Category category3 = new Category("Maths");
        insertCategory(category3);

    }

    public void addQuestion(Question question){

        db = getWritableDatabase();
        insertQuestion(question);

    }



    private void insertQuestion(Question question){

        ContentValues cv = new ContentValues();
        cv.put(QuizContract.QuestionsTable.COLUMN_QUESTION, question.getQuestion());
        cv.put(QuizContract.QuestionsTable.COLUMN_OPTION1, question.getOption1());
        cv.put(QuizContract.QuestionsTable.COLUMN_OPTION2, question.getOption2());
        cv.put(QuizContract.QuestionsTable.COLUMN_OPTION3, question.getOption3());
        cv.put(QuizContract.QuestionsTable.COLUMN_ANSWER, question.getAnswer());
        cv.put(QuizContract.QuestionsTable.COLUMN_SOLUTION, question.getSolution());
        cv.put(QuizContract.QuestionsTable.COLUMN_DIFFICULTY, question.getDifficulty());
        cv.put(QuizContract.QuestionsTable.COLUMN_CATEGORY_ID, question.getCategoryID());

        // No need to insert the id in the database cause the value of
        // id will increment automatically
        db.insert(QuizContract.QuestionsTable.TABLE_NAME,null,cv);

    }

    private void fillQuestions() {
        Question question1 = new Question("Programming Easy: A is correct","A","B","C",1,"Correct ans A",Question.DIFFICULTY_EASY,Category.PROGRAMMING);
        insertQuestion(question1);
        Question question2 = new Question("Maths Medium: B is correct","A","B","C",2,"Correct ans B",Question.DIFFICULTY_MEDIUM,Category.MATH);
        insertQuestion(question2);
        Question question3 = new Question("Geography Hard: C is correct","A","B","C",3,"Correct ans C",Question.DIFFICULTY_HARD,Category.GEOGRAPHY);
        insertQuestion(question3);
        Question question4 = new Question("Programming Hard: B is correct","A","B","C",2,"Correct ans B",Question.DIFFICULTY_HARD,Category.PROGRAMMING);
        insertQuestion(question4);
        Question question5 = new Question("Programming Medium: A is correct","A","B","C",1,"Correct ans A",Question.DIFFICULTY_MEDIUM,Category.PROGRAMMING);
        insertQuestion(question5);
        Question question6 = new Question("None Medium: A is correct","A","B","C",1,"Correct ans A",Question.DIFFICULTY_MEDIUM,4);
        insertQuestion(question6);

    }

    public List <Category> getAllCategories(){

        List<Category> categoryList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM "+ QuizContract.CategoriesTable.TABLE_NAME,null);

        if (c.moveToFirst()){
            do{
                Category category = new Category();
                category.setId(c.getInt(c.getColumnIndex(QuizContract.CategoriesTable._ID)));
                category.setName(c.getString((c.getColumnIndex(QuizContract.CategoriesTable.COLUMN_NAME))));

                categoryList.add(category);
            }while (c.moveToNext());
        }

        c.close();
        return categoryList;
    }


    public ArrayList<Question> getAllQuestions() {

        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase(); // if this method is called for the first time it calls onCreate method also
        Cursor c = db.rawQuery("SELECT * FROM "+ QuizContract.QuestionsTable.TABLE_NAME, null);

        if (c.moveToFirst()){
            do{
                Question question = new Question();
                question.setId(c.getInt(c.getColumnIndex(QuizContract.QuestionsTable._ID)));
                question.setQuestion(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION3)));
                question.setAnswer(c.getInt(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_ANSWER)));
                question.setSolution(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_SOLUTION)));
                question.setDifficulty(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_DIFFICULTY)));
                question.setCategoryID(c.getInt(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_CATEGORY_ID)));

                questionList.add(question);

            }while (c.moveToNext());
        }

        c.close();
        return questionList;

    }

    public ArrayList<Question> getQuestions(int category_id,String difficulty) {

        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase(); // if this method is called for the first time
                                    // it calls onCreate method also

//        String[] selectionArgs = new String[]{difficulty};
//
//        Cursor c = db.rawQuery("SELECT * FROM "+ QuizContract.QuestionsTable.TABLE_NAME +
//                " WHERE " + QuizContract.QuestionsTable.COLUMN_DIFFICULTY + " = ?", selectionArgs);

        String selection = QuizContract.QuestionsTable.COLUMN_CATEGORY_ID + " = ? " +
                " AND " + QuizContract.QuestionsTable.COLUMN_DIFFICULTY + " = ? ";
        String[] selectionArgs = new String[]{ String.valueOf(category_id), difficulty};

        Cursor c = db.query(QuizContract.QuestionsTable.TABLE_NAME, null,
                selection,selectionArgs,null,null,null);

        if (c.moveToFirst()){
            do{
                Question question = new Question();
                question.setId(c.getInt(c.getColumnIndex(QuizContract.QuestionsTable._ID)));
                question.setQuestion(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION3)));
                question.setAnswer(c.getInt(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_ANSWER)));
                question.setSolution(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_SOLUTION)));
                question.setDifficulty(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_DIFFICULTY)));
                question.setCategoryID(c.getInt(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_CATEGORY_ID)));

                questionList.add(question);

            }while (c.moveToNext());
        }

        c.close();
        return questionList;

    }
}
