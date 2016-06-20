package app.com.ninja.android.taskpath.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;


public class TaskProvider extends ContentProvider {

    //defining the Uri for the content provider , also defined in the manifest file
    private static final String AUTHORITY = "app.com.ninja.android.taskpath.database";
    private static final String BASE_PATH = "tasks";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // defining requested operations with constants
    private static int TASKS = 1;
    private static int TASKS_ID = 2;

    //the matcher that will help match URIs in content prividers
    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, TASKS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH +  "/#", TASKS_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        TaskDataHelper helper = new TaskDataHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        //to return a single Task we willcheck if the URI has an id , this means it's for a single task
        if (uriMatcher.match(uri) == TASKS_ID){
            selection = TaskDataHelper.TASK_ID + "=" + uri.getLastPathSegment();
        }

        /* insert additional columns here too if you are adding them to the database
        The last parameter we're passing is the ID in DEC order, to display the last added task at the top
         */

        return database.query(TaskDataHelper.TABLE, TaskDataHelper.ALL_COLUMNS,selection, null, null, null,  TaskDataHelper.TASK_ID + " DESC");
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(TaskDataHelper.TABLE, null, values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(TaskDataHelper.TABLE, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(TaskDataHelper.TABLE, values, selection, selectionArgs);
    }
}
