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

//    最終的にListViewに全てのファイル名を参照させ、その内容を表示するために、SimpleAdapterを使用した。
    SimpleAdapter mAdapter = null;
    List<Map<String, String>> mList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        activity_mainの画面を用意
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//       なぜかはわからないけど、ListViewに対して投稿を紐づけるためにはどうやらAdapterなるものを生成しないといけないらしい。
//       従って、Adapterの引数にするために、ここでListクラスのオブジェクトとしてmListを定義。
        mList = new ArrayList<Map<String,String>>();
//       ここでは、SimpleAdapterクラスをインスタンス 化してmAdapterに代入している。
//       その際に、引数として、mListが使用されている。リストとしての役割を持っているmListを第二引数として入れている。
        mAdapter = new SimpleAdapter(
                MainActivity.this,
                mList,
//                ここのsimple_list_itemについては他にもいくつかの種類があることがわかっている。
                android.R.layout.simple_list_item_2,
//                ここでは新しくstringのリストを作っている。これは、第二引数のMapのキーの配列。
                new String[]{"title", "detail"},
//                ここでは、新しくintのリストを作っている。引数はtext1とtext2。。。。この引数ってなんのために入れるんだっけ。。
//                =>第三引数のレイアウトXMLファイル内のViewのid番号の配列。これについては、第三引数がどうなっているかをその都度確認する必要がありそう！
                new int[]{android.R.id.text1, android.R.id.text2}
        );

//        ここでListViewをセット。その下でmAdapterをlistにセットした。
        ListView list = findViewById(R.id.listView);
        list.setAdapter(mAdapter);
//      ここでは、listのそれぞれの要素がタップされたときの処置を記述している。
//      従って、タップが起こったときの処理をインデントを下げて記述する必要がある。
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int posision, long id) {
//                Intentを用いて、タップされた投稿の編集画面へと遷移させる。IntentはいつものIntent.
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
//                既に作成されている投稿の編集なので、intentにputExtraメソッドを用いてファイル名、タイトル、詳細の文字列を渡してあげる。
//                その時に、第一引数がキーで第二引数がmListの中に入っているfilename,title,detailがキーとなっている文字列を渡す。
                intent.putExtra("NAME", mList.get(posision).get("filename"));
                intent.putExtra("TITLE", mList.get(posision).get("title"));
                intent.putExtra("DETAIL", mList.get(posision).get("detail"));
//                いつものstartActivity!!
                startActivity(intent);
            }
        });

//          この処理は長押し用の処理です↓
//        registerForContextMenu(list);
    }

//    ライフサイクル的に、EditActivityから戻ってきた時も、登録されたtodoを表示しないといけないので、その処理を書く。ライフサイクル的に、Resumeの位置なのでこれをオーバーライド。
    @Override
    protected void onResume() {
        super.onResume();
//      mListにクリアメソッドをかけた。oncreateから更新されていないので、EditActivityで行った変更を反映するため。
        mList.clear();

//        savePathに、アプリのファイル保存先ディレクトリから取得できたパスを代入する。
//        ちなみに、thisはこの場合、Contextを表しており、MainActivityのこと。MainActivityがある階層から保存先ディレクトリまでの絶対パスを取得。
        String savePath = this.getFilesDir().getPath();
//        その後に、そのディレクトリの中にあるファイル名一覧を全て取得してfilesに代入する。
        File[] files = new File(savePath).listFiles();
//        filesに入っているデータを全て新しい順にソート。
        Arrays.sort(files, Collections.reverseOrder());
//        これらfilesの中から、保存されているfilenameとtitleとdetailを全て取り出して、mlistの中に主キーと共にmapメソッドを用いてぶち込んでいく。
//        そのとき、以下のif文を用いて場合わけし、try,catchの例外構文を用いてそれぞれのファイルからtitleとdetailを取得する。
//        これらをmapクラスをインスタンス化したmap変数に代入し、mListにぶち込んでいく。
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
//        ここで、mAdapterに変更を加えたことをお知らせすると、新しいAdapterがListViewに渡されることになる。
        mAdapter.notifyDataSetChanged();
    }


//  menuフォルダで記述したアイコンをタップしたときの処理を記述する。この時に、intentを利用して画面遷移をする。
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//      switchでitemに対してgetItemIdメソッドを適用し、IDが取得できた場合のみ、caseでif文を指定して処理を分ける。IDがaction_addのIDの場合、画面遷移をするように処理する。
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


//    menuフォルダでmainのなかにアイコンをitemタグで作成したので、そのメニューがタップされたときの処理を記述する。戻り値にreturnが戻る。
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
}
