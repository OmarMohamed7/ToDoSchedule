package com.example.rooot.todolist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rooot.todolist.data.TaskContract;

/**
 * Created by rooot on 1/24/18.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public Adapter(Context context){
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.task_layout , parent , false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        int indexID = mCursor.getColumnIndex(TaskContract.TaskEntry._ID);
        int descriptionID = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION);
        int periortyID = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_PRIORITY);

        mCursor.moveToPosition(position);

        // Determine the values of the wanted data
        final int id = mCursor.getInt(indexID);
        String description = mCursor.getString(descriptionID);
        int priority = mCursor.getInt(periortyID);

        //Set values
        holder.itemView.setTag(id);
        holder.taskText.setText(description);

        // Programmatically set the text and color for the priority TextView
        String priorityString = "" + priority; // converts int to String
        holder.periorityText.setText(priorityString);

        GradientDrawable priorityCircle = (GradientDrawable) holder.periorityText.getBackground();
        // Get the appropriate background color based on the priority
        int priorityColor = getPriorityColor(priority);
        priorityCircle.setColor(priorityColor);

    }

    public Cursor swapCursor (Cursor c){

        // check if this cursor is the same as the previous cursor (mCursor)
        if(mCursor == c)
            return null; //nothing new

        Cursor temp = mCursor;
        this.mCursor = c; //new value assigned

        //check if valid cursors
        if(c != null)
            this.notifyDataSetChanged();

        return temp;
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0 ;
    }

    private int getPriorityColor(int priority) {
        int priorityColor = 0;

        switch(priority) {
            case 1: priorityColor = ContextCompat.getColor(mContext, R.color.materialRed);
                break;
            case 2: priorityColor = ContextCompat.getColor(mContext, R.color.materialOrange);
                break;
            case 3: priorityColor = ContextCompat.getColor(mContext, R.color.materialYellow);
                break;
            default: break;
        }
        return priorityColor;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView taskText , periorityText;


        public ViewHolder(View itemView) {
            super(itemView);

            taskText = itemView.findViewById(R.id.taskDescription);
            periorityText = itemView.findViewById(R.id.priorityTextView);
        }
    }
}
