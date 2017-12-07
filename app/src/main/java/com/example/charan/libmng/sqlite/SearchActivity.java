package com.example.charan.libmng.sqlite;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.charan.libmng.R;


public class SearchActivity extends AppCompatActivity {
    private SqliteHelper mDB;

    private EditText mEditWordView;
    private TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mDB = new SqliteHelper(this);

        mEditWordView = ((EditText) findViewById(R.id.search_word));
        mTextView = ((TextView) findViewById(R.id.search_result));
    }
    public void showResult(View view) {
        String word = mEditWordView.getText().toString();
        mTextView.setText("Result for " + word + ":\n\n");
        Cursor cursor = mDB.search(word);
        cursor.moveToFirst();
        if (cursor != null & cursor.getCount() > 0) {
            int index;
            String result;
            do {
                index = cursor.getColumnIndex(SqliteHelper.Col_2);
                result = cursor.getString(index);
                mTextView.append(result + "\n");
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            mTextView.append(getString(R.string.no_result));
        }
    }

}
