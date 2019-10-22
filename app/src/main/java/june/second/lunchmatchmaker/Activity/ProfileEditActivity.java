package june.second.lunchmatchmaker.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import june.second.lunchmatchmaker.R;

public class ProfileEditActivity extends AppCompatActivity {


    //디버깅을 위한 string 값
    String here = "ProfileEditActivity";

    EditText editTextNickName;
    EditText editTextComment;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        Log.w(here , here +  "  onCreate");

        editTextNickName = findViewById(R.id.nickNameEdit);
        editTextComment = findViewById(R.id.etComment);
        textView = findViewById(R.id.textCount);

        //뒤로 가는 버튼
        ImageView imageToBack =  findViewById(R.id.profileEditBackButton);
        imageToBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), ProfileNTimelineActivity.class);
                startActivity(intent1);
            }
        });


        //완료 버튼인데
        ImageView imageToFinish =  findViewById(R.id.write_Finish_Button);
        imageToFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplicationContext(), ProfileNTimelineActivity.class);

                //Todo 데이터 구현하면서 => 프로필 유저 데이터에서 뽑아와서 설정하기
                intent2.putExtra("nickname", editTextNickName.getText().toString());
                intent2.putExtra("content", editTextComment.getText().toString());
                intent2.putExtra("order", "edit");
                //플래그로 저장 임시 구현
//                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );

                //편집 완료 후 창에 있는 데이터 없애기
                editTextNickName.setText("");
                editTextComment.setText("");
                startActivity(intent2);
            }
        });


        editTextComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = editTextComment.getText().toString();
                textView.setText(input.length()+"자");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();

        Log.w(here+"  onStart", "onStart");

        long startTime = System.currentTimeMillis();saveState();
        long endTime = System.currentTimeMillis();
        Log.w(here+":  timecheck", "startTime:" + (startTime)/1000.0);
        Log.w(here+":  timecheck", "endTime:  " + (endTime)/1000.0);
        Log.w(here+":  timecheck", "onStop:" + (endTime - startTime)/1000.0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(here+"  onStop", "onStop");

        long startTime = System.currentTimeMillis();
       restoreState();
        long endTime = System.currentTimeMillis();
        Log.w(here+":  timecheck", "startTime:" + (startTime)/1000.0);
        Log.w(here+":  timecheck", "endTime:  " + (endTime)/1000.0);
        Log.w(here+":  timecheck", "onStop:" + (endTime - startTime)/1000.0);

    }


    protected void restoreState(){
        Log.w(here+"  restoreState", "restoreState");

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        if((pref != null) && (pref.contains("nickname"))){
            String nickname = pref.getString("nickname","");
            String content = pref.getString("content","");
            editTextNickName.setText(nickname);
            editTextComment.setText(content);

        }
    }


    protected void saveState(){
        Log.w(here+"  saveState", "saveState");

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("nickname",editTextNickName.getText().toString() );
        editor.putString("content", editTextComment.getText().toString() );

        editor.commit();
    }



        @Override
    protected void onRestart() {
        super.onRestart();
        Log.w(here , here +  "  onRestart");
    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.w(here , here +  "  onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(here , here +  "  onPause");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(here , here +  "  onDestroy");
    }
}
