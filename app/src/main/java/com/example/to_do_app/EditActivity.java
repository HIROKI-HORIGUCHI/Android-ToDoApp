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

//  戻るボタンが押されて、EditActivityはライフサイクルの中でonPauseに該当する。従って、onPauseをオーバーライドしてファイル保存処理と正常に保存できたときのToast処理を記述する。
    @Override
    protected void onPause() {
        super.onPause();

//        ライフサイクルの順序通り、(create,start,Resume,アクティビティ実行中,onPauseの順で流れてきて、それぞれの諸条件によって処理を分けている)
//        この処理は、オーバライドしているそれぞれのクラス階層の一個上で定義している、mNotSave変数にTrueが入っているのかFalseが入っているのかで場合わけしている。
//        そんな中で、以下はmNotSaxe=trueのとき、すなわち、onOptionItemSelectedでボタンが押され、mNotsaveの値が書き換わったとき、returnでonPauseを終了する。
        if (mNotSave) {
            return;
        }
//

//      この処理は、ToDo_Titleという名前がついたEditTextを持つViewを見つけ、そこにあるテキストを変数eTxtTitleに代入している。
//      上の処理と同様。こちらは、ToDo_detailをeTxtDetailに代入している。
//      それぞれの変数からテキストのみを取得し、それを文字列に変換している。ちなみに、この変数の中に何が入っているのかはまだ覗いてない。。。
        EditText eTxtTitle = (EditText) findViewById(R.id.ToDo_Title);
        EditText eTxtDetail = (EditText) findViewById(R.id.ToDo_detail);
        String title = eTxtTitle.getText().toString();
        String detail = eTxtDetail.getText().toString();
//      上記２つの変数のうち、textとdetailが空の場合の処理。Toastでショートメッセージを飛ばしながら、returnでonPauseを終了する。
        if (title.isEmpty() && detail.isEmpty()) {
            Toast.makeText(this, R.string.msg_destruction, Toast.LENGTH_SHORT).show();
            return;
        }

//      これはmFilenameがからの場合の処理。つまり、該当ファイルが存在せず新規作成をする場合の処理。
//      Oncreateで取得したmFilename変数がからの場合は、新しく作成時の日付からミリ秒まで取得してファイルを生成する。
        if (mFilename.isEmpty()) {
//            ファイルを生成するために、DateクラスのSystem.currentTimeMillis()メソッドを用いて、欲しい日時を代入する。
            Date date = new Date(System.currentTimeMillis());
//            SimpleDateFormatクラスにnewメソッドを用いてsdfオブジェクトを生成する。ここで、SimpleDateFormatの引数に日時のデータ型と日時を取得する場所を与えている。
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.JAPAN);
//            ここで変数mfilenameがsdf.txt(日付+.txt)となるように代入する。もちろん、保存を行わないといけないため、ここで処理を終わられないので、returnは記述しない。
            mFilename = sdf.format(date) + ".txt";
        }

//      OutPutStreamとPrintWriterクラスのオブジェクトを生成し、nullを代入する。
        OutputStream out = null;
        PrintWriter writer = null;

//      保存処理を行う。
        try {
//            ここでOutPutStreamクラスのオブジェクトであるout変数に、、、、this.の意味がわからん。これなんだ？？？=>「outのことを示しているのかな？？」
            out = this.openFileOutput(mFilename, Context.MODE_PRIVATE);
//            ここではPrintWriterクラスのオブジェクトであるwriterに、PrintWriterのインスタンスを代入している。
//            つまり、PrintWriterのインスタンスを作成するために、引数にOutputStreamWriter(引数)を与えた。
            writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));
//          writerに、titleとdetailを書き込む。
            writer.println(title);
            writer.print(detail);

//            writerとoutを閉じる。
            writer.close();
            out.close();

//            例外処理。
        }catch (Exception e){
            Toast.makeText(this, "File save error!", Toast.LENGTH_LONG).show();
        }
    }

//    ゴミ箱ボタンを押すためにmenuディレクトリのedit.xmlをここでセットしている。
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit, menu);
        return true;
    }

//  ゴミ箱ボタンを押したときの処理。
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switchで、item.getItemId()でItemIDを取得し、caseで処理を分けていく。
        switch (item.getItemId()) {
//            このケースで、action_delというandroid:idを命名したゴミ箱ボタンが押された場合の処理を行う。
            case R.id.action_del:
//                ここで、mFilenameが空でない、つまり、きちんとファイル名が生成されているものは削除する。
               if (!mFilename.isEmpty()){
//                   やはり、thisの使い方がこのサンプルではよくわからない。これはこのクラスのという意味のthisなのか？
                   if(this.deleteFile(mFilename)){
//                       削除できたらToastを飛ばしてショートメッセージを表示する。
                       Toast.makeText(this,R.string.msg_del, Toast.LENGTH_SHORT).show();
                   }
               }
//               この処理は、deleteが成功しても失敗してもmNotSave変数をtrueに書き換えて、onPauseにて記載されている保存処理を飛ばしてアクティビティを終了する。
               mNotSave = true;
               this.finish();
               break;
//            上記ケース以外。処理をbreakしてEditActivityのライフサイクルを終了させ、MainActivityのonResumeに遷移する。
            default:
                break;
        }
//      　おまじない？？？これはなんだ。。
//        Fragmentを使うときにこの戻り値が何になっているかが重要らしい。これ自体は、メモリを使うのであればtrueを、使わないのであればfalseを返すためのものみたい。
        return super.onOptionsItemSelected(item);
    }
}
