/*
created by Ninjayoto:

TODO
Handle Configuration change
Implement DialogFragment to replace current current dialogs
Implement due date with a date picker
Support for selecting priority

Improve Style and UI:
Create custom list items
 */

package app.com.ninja.android.taskpath;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import app.com.ninja.android.taskpath.database.TaskDataHelper;
import app.com.ninja.android.taskpath.database.TaskProvider;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AddFragment.AddTaskDialogListener , AddFragment.OnFragmentInteractionListener {

    private CursorAdapter cursorAdapter;
    private static final String TAG = "MainActivity ";
    String taskFilter;
    String existTaskText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView lv = (ListView) findViewById(R.id.list);

        //Get data FROM data table column -> array
        String[] from = {TaskDataHelper.COL_TASK_TITLE};
        //Data TO list items
        int[] to = {R.id.i_task};
        cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.task_item, null, from, to, 0) ;


        lv.setAdapter(cursorAdapter);
        getLoaderManager().initLoader(0, null, this);


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                Uri uri = Uri.parse(TaskProvider.CONTENT_URI+ "/" +id);
                taskFilter = TaskDataHelper.TASK_ID + "=" + uri.getLastPathSegment();
                Cursor cursor = getContentResolver().query(uri, TaskDataHelper.ALL_COLUMNS, taskFilter, null, null);
                cursor.moveToFirst();
                existTaskText = cursor.getString(cursor.getColumnIndex(TaskDataHelper.COL_TASK_TITLE));

                openEditDeleteDialog();
                return true;
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                fireAddTaskDialog();

            }
        });


    }

    private void openEditDeleteDialog() {

        final String[] actions = {"Edit", "Delete"};
        //final String editDeleteFilter  = filter;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Pick an Action");
        builder.setItems(actions, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Edit method
                if (item == 0) {

                    editTask();
                }

                //Delete method
                if (item==1) {

                    deleteTask();
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void editTask() {

        final EditText addTask = new EditText(this);
        addTask.setText(existTaskText);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit task")
                .setMessage("Edit an Existing task then tap OK")
                .setView(addTask)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String task = String.valueOf(addTask.getText());
                        updateTask(task);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

        }

    private void updateTask(String task) {
        ContentValues values = new ContentValues();
        values.put(TaskDataHelper.COL_TASK_TITLE, task);
        getContentResolver().update(TaskProvider.CONTENT_URI, values, taskFilter, null);

        restartLoader();
        Toast.makeText(this, "Task updated successfully", Toast.LENGTH_LONG).show();
    }


    private void deleteTask() {
        getContentResolver().delete(TaskProvider.CONTENT_URI,taskFilter, null);
        restartLoader();
        Toast.makeText(this, "Task Deleted" , Toast.LENGTH_LONG).show();
    }


    private void fireAddTaskDialog() {
        final EditText addTask = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add a new task")
                .setMessage("Type in the task and click add")
                .setView(addTask)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(addTask.getText());
                        if (task.length()!=0){
                        insertTaskToData(task);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void insertTaskToData(String task){
        ContentValues values = new ContentValues();
        values.put(TaskDataHelper.COL_TASK_TITLE, task);
        //Uri taskUri =
                getContentResolver().insert(TaskProvider.CONTENT_URI, values);

        restartLoader();
        Toast.makeText(this, "To edit or delete long press a task", Toast.LENGTH_LONG).show();
    }


    private void restartLoader(){
        getLoaderManager().restartLoader(0,null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, TaskProvider.CONTENT_URI, null, null,null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        cursorAdapter.swapCursor(null);

    }

    
    //implemented DialogFragment methods

    @Override
    public void onFinishAddDialog(String inputText) {
        Toast.makeText(this, "Save to database " + inputText, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
