package app.com.ninja.android.taskpath.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class TaskDataHelper extends SQLiteOpenHelper {


    public static final String DB_NAME = "app.com.ninja.android.taskpath.database.todo.db";

    public static final int DB_VERSION = 1;

    public static final String TABLE = "tasks";
    public static final String TASK_ID = "_id";
    public static final String COL_TASK_TITLE = "task";
    public static final String TASK_CREATED = "taskCreated";

    public static final String[] ALL_COLUMNS =
            {TASK_ID, COL_TASK_TITLE, TASK_CREATED};

    public TaskDataHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE + " ( " +
                TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TASK_TITLE + " TEXT NOT NULL," +
                TASK_CREATED + " TEXT );";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}
