package com.example.to_do_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import android.view.MenuItem;
import android.content.Intent;


public class EditActivity extends AppCompatActivity {
    String mFilename = "";

    boolean mNotSave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        EditText eTxtTitle = (EditText)findViewById(R.id.ToDo_Title);
        EditText etxtdetail = (EditText)findViewById(R.id.ToDo_detail);

        Intent intent = getIntent();
        String name = intent.getStringExtra("NAME");
        if(name != null) {
            mFilename = name;
            eTxtTitle.setText(intent.getStringExtra("TITLE"));
            etxtdetail.setText(intent.getStringExtra("DETAIL"));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mNotSave) {
            return;
        }

//
//
        EditText eTxtTitle = (EditText) findViewById(R.id.ToDo_Title);
        EditText eTxtDetail = (EditText) findViewById(R.id.ToDo_detail);
        String title = eTxtTitle.getText().toString();
        String detail = eTxtDetail.getText().toString();
//
        if (title.isEmpty() && detail.isEmpty()) {
            Toast.makeText(this, R.string.msg_destruction, Toast.LENGTH_SHORT).show();
            return;
        }

//
        if (mFilename.isEmpty()) {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.JAPAN);
            mFilename = sdf.format(date) + ".txt";
        }

//
//
        OutputStream out = null;
        PrintWriter writer = null;

//
        try {
            out = this.openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));

            writer.println(title);
            writer.print(detail);

            writer.close();
            out.close();
        }catch (Exception e){
            Toast.makeText(this, "File save error!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_del:
               if (!mFilename.isEmpty()){
                   if(this.deleteFile(mFilename)){
                       Toast.makeText(this,R.string.msg_del, Toast.LENGTH_SHORT).show();
                   }
               }
               mNotSave = true;
               this.finish();
               break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
