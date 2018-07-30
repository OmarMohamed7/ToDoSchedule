/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.rooot.todolist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import static com.example.rooot.todolist.data.TaskContract.TaskEntry.TABLE_NAME;

public class TaskContentProvider extends ContentProvider {

    private TaskDbHelper mTaskDbHelper;

    public static final int TASKS = 100;
    public static final int TASK_WITH_ID = 101;

    /* onCreate() is where you should initialize anything you’ll need to setup
    your underlying data source.
    In this case, you’re working with a SQLite database, so you’ll need to
    initialize a DbHelper to gain access to it.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //for directory
        uriMatcher.addURI(TaskContract.AUTHORITY , TaskContract.PATH_TASKS , TASKS);

        //for a single row
        uriMatcher.addURI(TaskContract.AUTHORITY , TaskContract.PATH_TASKS + "/#" , TASK_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mTaskDbHelper = new TaskDbHelper(context);

        return true;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();
        int matcher = sUriMatcher.match(uri);

        Uri returnUri = null;

        switch (matcher) {
            case TASKS:
                long id = db.insert(TABLE_NAME, null, values);

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Unkown Uri : " + uri);
                }
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;

    }

    Cursor retCursor;

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        //Access to database
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

        int matcher = sUriMatcher.match(uri);
        switch (matcher){
            case TASKS :
                retCursor = db.query(TABLE_NAME,
                                    projection,
                                    selection,
                                    selectionArgs,
                                    null,
                                    null,
                                     sortOrder);
                break;

            case TASK_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[] {id};

                retCursor = db.query(TABLE_NAME,
                                    projection,
                                    mSelection,
                                    mSelectionArgs,
                                    null,
                                    null,
                                    sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");

        }

        retCursor.setNotificationUri(getContext().getContentResolver() , uri);

        return retCursor;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

        int matcher = sUriMatcher.match(uri);
        int taskDeleted;

        switch(matcher){
            case TASK_WITH_ID :
                String id = uri.getPathSegments().get(1);

                String mSelection = "_id=?";
                taskDeleted = db.delete(TABLE_NAME, mSelection , new String[]{id});
                break;

            default:
                throw new SQLException("Error");
        }
        if(taskDeleted != 0)
            getContext().getContentResolver().notifyChange(uri , null);

        return taskDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        final SQLiteDatabase database = mTaskDbHelper.getWritableDatabase();
        int taskUpdated;
        int matcher = sUriMatcher.match(uri);
        switch (matcher){
            case TASK_WITH_ID:
                String id = uri.getPathSegments().get(1);
                taskUpdated = database.update(TABLE_NAME , values , "_id=?" , new String[]{id});
                break;

            default:
                throw new UnsupportedOperationException("Unkown uri : " + uri.toString());
        }
        if(taskUpdated != 0)
            getContext().getContentResolver().notifyChange(uri , null);

        return taskUpdated;
    }


    @Override
    public String getType(@NonNull Uri uri) {

        int match = sUriMatcher.match(uri);

        switch (match) {
            case TASKS:
                // directory
                return "vnd.android.cursor.dir" + "/" + TaskContract.AUTHORITY + "/" + TaskContract.PATH_TASKS;
            case TASK_WITH_ID:
                // single item type
                return "vnd.android.cursor.item" + "/" + TaskContract.AUTHORITY + "/" + TaskContract.PATH_TASKS;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

}
