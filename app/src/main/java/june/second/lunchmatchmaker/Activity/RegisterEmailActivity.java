package june.second.lunchmatchmaker.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import june.second.lunchmatchmaker.R;

public class RegisterEmailActivity extends AppCompatActivity {


    private TextView textId;
    private TextInputLayout editTextPwLayout;
    private TextInputEditText editTextPw;
    private TextInputLayout etPwAgainLayout;
    private TextInputEditText etPwAgain;

    private boolean pwCondition;
    private boolean pwAgainCondition;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);



        textId = findViewById(R.id.textId);
        editTextPwLayout = findViewById(R.id.editTextPwLayout);
        editTextPw = findViewById(R.id.editTextPw);
        etPwAgainLayout = findViewById(R.id.etPwAgainLayout);
        etPwAgain = findViewById(R.id.etPwAgain);

        //앞에서 이메일인증한 이메일 값 받아오기
        Intent getIntent = getIntent();
        textId.setText(getIntent.getStringExtra("id"));



        findViewById(R.id.emailRegisterBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(  RegisterEmailActivity.this, RegisterEmailCheckActivity.class);

                startActivity(intent);

            }
        });


        findViewById(R.id.emailRegisterNextButton).setOnClickListener(v ->{
            Intent intent = new Intent(  RegisterEmailActivity.this, RegisterProfileActivity.class);

            Log.w("RegisterEmailActivity", "pwCondition  "+pwCondition );
            Log.w("RegisterEmailActivity", "pwAgainCondition  "+pwAgainCondition );
            //비밀번호 8자리 이상인지 + 비밀번호 재입력하고 비밀번호 일치하는지 상태값 체크
            //둘다 true 이여야지 통과
            //+이때 인텐트로 통과한 아이디하고 비번값 담아서 다음 액티비티로 넘김
            //왜냐면 다음 액티비티에서 아이디 비번 + 프로필정보 까지해서 한번에 유저 객체로 만들어서
            if( pwCondition && pwAgainCondition){

                intent.putExtra("id", textId.getText().toString());
                intent.putExtra("pw", editTextPw.getText().toString());

                startActivity(intent);
            }
            else{
                Toast.makeText(this, "가입정보를 다시 확인해주세요", Toast.LENGTH_LONG).show();
            }

        });


        //비밀번호 8자리 이상인지 + 비밀번호 재입력하고 비밀번호 일치하는지 체크
        editTextPw.addTextChangedListener(pwTextWatcher);
        etPwAgain.addTextChangedListener(pwAgainTextWatcher);




    }




    TextWatcher pwTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length() <8){
                editTextPwLayout.setError("여덟자리 이상 입력해주세요");
                pwCondition = false;

            }
            else{
                editTextPwLayout.setError(null);
                pwCondition = true;

            }

        }
    };


    TextWatcher pwAgainTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(etPwAgain.getText().toString().equals(editTextPw.getText().toString())){
                etPwAgainLayout.setError(null);
                pwAgainCondition = true;
            }
            else{
                etPwAgainLayout.setError("비밀번호가 일치하지 않습니다");
                pwAgainCondition = false;

                Log.w("RegisterEmailActivity", "onTextChanged: etPwAgain.getText().toString()"+etPwAgain.getText().toString() );
                Log.w("RegisterEmailActivity", "onTextChanged: editTextPw.getText().toString()"+editTextPw.getText().toString() );
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {


        }
    };



}
