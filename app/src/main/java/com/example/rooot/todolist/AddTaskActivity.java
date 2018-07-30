package com.example.rooot.todolist;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.rooot.todolist.data.TaskContract;

import java.util.regex.Pattern;

public class AddTaskActivity extends AppCompatActivity {

    EditText discribtion_text;
    private int mPriority = 0;
    private Button addNewTaskButton;

    //String pattern = "^\\w\\.";
    //Pattern p = Pattern.compile(pattern);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        discribtion_text = findViewById(R.id.description_text);
        addNewTaskButton = findViewById(R.id.addButton);
        addNewTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((discribtion_text.getText().toString().equals(String.valueOf(EditorInfo.IME_ACTION_DONE)) ||
                        discribtion_text.getText().toString().equals(" ") ||
                        discribtion_text.getText().toString().length() != 0 )
                                && (mPriority == 1 || mPriority == 2 || mPriority == 3)) {

                    if(Integer.valueOf(mPriority) == null) {
                        Toast.makeText(getApplicationContext(), getString(R.string.Error), Toast.LENGTH_LONG).show();
                        return;
                    }



                    ContentValues cv = new ContentValues();
                    cv.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, discribtion_text.getText().toString());
                    cv.put(TaskContract.TaskEntry.COLUMN_PRIORITY, mPriority);

                    // insert via content resolver
                    Uri uri = getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, cv);

                    if (uri == null)
                        Toast.makeText(AddTaskActivity.this, "Error empty url ", Toast.LENGTH_SHORT).show();

                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.Error) , Toast.LENGTH_LONG).show();
                }
            }
        });

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    public void onPrioritySelected(View view){
        if(((RadioButton) findViewById(R.id.radButton1)).isChecked())
            mPriority = 1;
        else if (((RadioButton) findViewById(R.id.radButton2)).isChecked())
            mPriority = 2;
        else if (((RadioButton) findViewById(R.id.radButton3)).isChecked())
            mPriority = 3;
    }

    public void onClickAddTask(View view){
        if(discribtion_text.getText().toString().length() == 0)
            return;

        ContentValues cv = new ContentValues();
        cv.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION , discribtion_text.getText().toString());
        cv.put(TaskContract.TaskEntry.COLUMN_PRIORITY , mPriority);

        // insert via content resolver
        Uri uri = getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI , cv);

        if(uri != null)
            Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();

        finish();
    }
}
