package june.second.lunchmatchmaker.Activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import june.second.lunchmatchmaker.Etc.GMailSender;
import june.second.lunchmatchmaker.R;

public class RegisterEmailCheckActivity extends AppCompatActivity {
    private String here = "RegisterEmailCheckActivity";

    private TextInputLayout editEmailLayout;
    private TextInputEditText editEmail;
    private TextInputLayout editCheckLayout;
    private TextInputEditText editCheck;


    private boolean checkCondition;
    private boolean countdownCondition;

    TextView checkCount;

    MainHandler handler;
    LinearLayout checkLayout;
    GMailSender gMailSender;
    int countDown;
    Button emailRegisterButton;

    BackgroundThread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email_check);


        //액티비티내의 뷰 연동 및 클릭시의 동작 설정------------------------------------------------
        editEmailLayout = findViewById(R.id.editEmailLayout);
        editEmail = findViewById(R.id.editEmail);
        editCheckLayout = findViewById(R.id.editCheckLayout);
        editCheck = findViewById(R.id.editCheck);
        emailRegisterButton = findViewById(R.id.emailRegisterButton);
        checkCount =findViewById(R.id.checkCount); //인증 시간

        //인증 번호 전송되었다는 텍스트 + 인증 시간이 들어가 있는 레이아웃
        //뷰 연동한건 사용자가 전송 눌렀을때 레이아웃 자체로 보여지게 하기 위해서
        //원래 안보이는 상태임
        checkLayout = findViewById(R.id.checkLayout);






        //인터넷 사용을 위한 권한
        tedPermission();
        //삭제 보류
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());





        thread = new BackgroundThread();

        emailRegisterButton.setOnClickListener(view -> {

            try {


                gMailSender = new GMailSender(getResources().getString(R.string.my_mail_id), getResources().getString(R.string.my_mail_pw));

                //GMailSender.sendMail(제목, 본문내용, 받는사람);
                gMailSender.sendMail("런치매치메이커 가입 인증 메일 입니다.", "어플에 아래의 인증번호를 입력해주세요\n\n"+"\t\t\t" +  gMailSender.emailCode , editEmail.getText().toString());
                Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
                //인증번호가 전송되면 인증번호 전송되었다는 텍스트와 인증시간 보여지게 설정
                checkLayout.setVisibility(View.VISIBLE);


                handler = new MainHandler();
                if(countDown == 0 ){
                    //300 => 1 당 1초로 => 300 = 60*5 =>5분
                    //인증 제한 시간 5분임
                    //이미 countDown 이  0 으로 되어있으면 쓰레드가 재시작을 안해서
                    //countDown 하고 쓰레드 재시작해주기
                    countDown = 300;
                    thread = new BackgroundThread();
                    thread.start();
                }
                else{
                    countDown = 300;
                    thread.start();
                }

                //전송 후에는 재전송으로 버튼 텍스트 바꾸어주기
                emailRegisterButton.setText("재전송");
            } catch (SendFailedException e) {
                Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
            } catch (MessagingException e) {
                Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        });



        findViewById(R.id.emailRegisterBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(  RegisterEmailCheckActivity.this, LoginActivity.class);

                startActivity(intent);

            }
        });

        findViewById(R.id.emailRegisterCheckButton).setOnClickListener(v ->{
            Intent intent = new Intent(  RegisterEmailCheckActivity.this, RegisterEmailActivity.class);

            //제한시간내에 입력(countdownCondition) 하고
            //인증번호가 맞으면(checkCondition) 인증되고
            //다음 액티비티(RegisterEmailActivity) 로 넘어감
            if(countdownCondition){
                if(checkCondition){
                    intent.putExtra("id", editEmail.getText().toString());
                    Toast.makeText(this, "인증 되었습니다", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
                else{

                    Toast.makeText(this, "인증번호가 틀렸습니다", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(this, "인증시간이 만료되었습니다. 다시 보내주세요", Toast.LENGTH_LONG).show();

            }


        });

        //인증번호 맞는지 체크하는 리스너
        editCheck.addTextChangedListener(numberCheckWatcher);

    }

    class BackgroundThread extends Thread {
        //300초 => 5분


        public void run() {
            while (countDown != 0){
                try {
                    Thread.sleep(1000);
                } catch(Exception e) {}

                countDown -= 1;
                Log.d("Thread", "countDown : " + countDown);

                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("value", countDown);
                message.setData(bundle);

                handler.sendMessage(message);

            }
        }
    }

    class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();
            int countDown = bundle.getInt("value");
            int timeMin = countDown/60;
            int timeSec = countDown%60;

            if (countDown >0){
                checkCount.setText( timeMin + "분 " + timeSec + "초");
                countdownCondition =true;
            }
            else{
                checkCount.setText( "인증시간이 만료되었습니다");
                countdownCondition =false;

            }



        }
    }








    TextWatcher numberCheckWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {



        }

        @Override
        public void afterTextChanged(Editable editable) {
            try{
                    if(editCheck.getText().toString().equals(gMailSender.getEmailCode())){
                        //이메일로 보낸 인증번호와 사용자가 입력한 인증번호가 일치하면
                        //일치 상태(checkCondition) true 로
                        editCheckLayout.setError(null);
                        checkCondition = true;
                    }
                    else{
                        editCheckLayout.setError(null);
                        checkCondition = false;

                        Log.w(here, "afterTextChanged - editCheck.getText().toString() :  "+editCheck.getText().toString() );
                        Log.w(here, "afterTextChanged - gMailSender.getEmailCode()  :  "+gMailSender.getEmailCode() );
                    }

            }
            catch (NullPointerException e){
                Log.w(here, "afterTextChanged - NullPointerException " );

            }


        }
    };





    //권한 허용 메소드
    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.INTERNET)
                .check();

    }
}
