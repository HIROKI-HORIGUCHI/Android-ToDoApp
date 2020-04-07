package com.example.to_do_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    SimpleAdapter mAdapter = null;
    List<Map<String, String>> mList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mList = new ArrayList<Map<String,String>>();

        mAdapter = new SimpleAdapter(
                MainActivity.this,
                mList,
                android.R.layout.simple_list_item_2,
                new String[]{"title", "detail"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        ListView list = findViewById(R.id.listView);
        list.setAdapter(mAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int posision, long id) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("NAME", mList.get(posision).get("filename"));
                intent.putExtra("TITLE", mList.get(posision).get("title"));
                intent.putExtra("DETAIL", mList.get(posision).get("detail"));
                startActivity(intent);
            }
        });

        registerForContextMenu(list);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mList.clear();

        String savePath = this.getFilesDir().getPath().toString();
        File[] files = new File(savePath).listFiles();

        Arrays.sort(files, Collections.reverseOrder());

        for (int i=0;i<files.length;i++) {
            String fileName = files[i].getName();
            if (files[i].isFile() && fileName.endsWith(".txt")) {
                String title = null;
                String detail = null;
                try {
                    InputStream in = this.openFileInput(fileName);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                    title = reader.readLine();

                    char[] buf = new char[(int)files[i].length()];
                    int num = reader.read(buf);
                    detail = new String(buf, 0, num);
                    reader.close();
                    in.close();
                } catch (Exception e) {
                    Toast.makeText(this, "File read error!!", Toast.LENGTH_LONG).show();
                }

                Map<String,String> map = new HashMap<String, String>();
                map.put("filename", fileName);
                map.put("title", title);
                map.put("detail", detail);
                mList.add(map);
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(this, EditActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
}
