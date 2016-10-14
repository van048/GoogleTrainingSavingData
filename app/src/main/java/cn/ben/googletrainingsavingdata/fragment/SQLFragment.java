package cn.ben.googletrainingsavingdata.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.ben.googletrainingsavingdata.R;
import cn.ben.googletrainingsavingdata.sql.FeedReaderDbHelper;
import cn.ben.googletrainingsavingdata.sql.FeedReaderContract.FeedEntry;

public class SQLFragment extends Fragment implements View.OnClickListener {

    private FeedReaderDbHelper mDbHelper;
    private SQLiteDatabase writableDatabase;
    private SQLiteDatabase readableDatabase;
    private TextView textView;

    public SQLFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mDbHelper = new FeedReaderDbHelper(getContext());
        new Thread() {
            @Override
            public void run() {
                // Gets the data repository in write mode
                writableDatabase = mDbHelper.getWritableDatabase();
                readableDatabase = mDbHelper.getReadableDatabase();
            }
        }.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sql, container, false);

        Button btnPutInfo = (Button) view.findViewById(R.id.btn_put_info);
        btnPutInfo.setOnClickListener(this);
        Button btnReadInfo = (Button) view.findViewById(R.id.btn_read_info);
        btnReadInfo.setOnClickListener(this);
        Button btnDeleteMyTitle = (Button) view.findViewById(R.id.btn_delete_my_title);
        btnDeleteMyTitle.setOnClickListener(this);
        Button btnDeleteAll = (Button) view.findViewById(R.id.btn_delete_all);
        btnDeleteAll.setOnClickListener(this);
        Button btnUpdate = (Button) view.findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(this);
        textView = (TextView) view.findViewById(R.id.tv_show_info);

        return view;
    }

    @SuppressWarnings("UnusedReturnValue")
    private long putInformation(String title, String subtitle) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);

        // Insert the new row, returning the primary key value of the new row
        return writableDatabase.insert(FeedEntry.TABLE_NAME, null, values);
    }

    private Cursor readPartInformation() {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_NAME_SUBTITLE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = FeedEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = {"My Title"};

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";

        return readableDatabase.query(
                FeedEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups??
                sortOrder                                 // The sort order
        );
    }

    private Cursor readTotalInformation() {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_NAME_SUBTITLE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedEntry._ID + " ASC";

        return readableDatabase.query(
                FeedEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups??
                sortOrder                                 // The sort order
        );
    }

    private void deleteMyTitle() {
        // Define 'where' part of query.
        String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {"My Title"};
        // Issue SQL statement.
        writableDatabase.delete(FeedEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_put_info:
                putInformation("My Title", "Z");
                putInformation("My Title", "B");
                putInformation("My Title", "E");
                putInformation("My Title", "A");
                putInformation("My Title", "D");
                putInformation("Your Title", "P");
                putInformation("Your Title", "Q");
                putInformation("Your Title", "D");
                Cursor cursor = readTotalInformation();
                showEntries(cursor);
                break;
            case R.id.btn_read_info:
                cursor = readPartInformation();
                showEntries(cursor);
                break;
            case R.id.btn_delete_my_title:
                deleteMyTitle();
                cursor = readTotalInformation();
                showEntries(cursor);
                break;
            case R.id.btn_delete_all:
                deleteAll();
                cursor = readTotalInformation();
                showEntries(cursor);
                break;
            case R.id.btn_update:
                String newTitle = "haha";
                int affected = update(newTitle);
                Toast.makeText(getContext(), affected + " modified.", Toast.LENGTH_SHORT).show();
                cursor = readTotalInformation();
                showEntries(cursor);
                break;
            default:
                break;
        }
    }

    private int update(String title) {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, title);

        // Which row to update, based on the title
        String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
        String[] selectionArgs = {"My Title"};

        return readableDatabase.update(
                FeedEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    private void deleteAll() {
        writableDatabase.delete(FeedEntry.TABLE_NAME, null, null);
    }

    private void showEntries(Cursor cursor) {
        StringBuilder sb = new StringBuilder();
        boolean hasFirst = cursor.moveToFirst();
        if (!hasFirst) {
            textView.setText(R.string.no_entries);
            return;
        }
        do {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(FeedEntry._ID)
            );
            String title = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE)
            );
            String subTitle = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SUBTITLE)
            );
            sb.append(itemId).append(" ").append(title).append(" ").append(subTitle).append(" \n");
        } while (cursor.moveToNext());
        textView.setText(sb.toString());
    }
}
