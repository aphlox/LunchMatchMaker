package june.second.lunchmatchmaker.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import june.second.lunchmatchmaker.Item.User;
import june.second.lunchmatchmaker.R;
import june.second.lunchmatchmaker.Receiver.JoinApprovalReceiver;
import june.second.lunchmatchmaker.Service.LunchMatchService;

public class RegisterProfileActivity extends AppCompatActivity {

    String here = "RegisterProfileActivity";

    private TextView textGenderSelect;
    private TextView textBirthdaySelect;
    private Button registerFinishButton;
    private String textId;
    private String textPw;

    private TextInputLayout userNameLayout;
    private TextInputEditText userName;
    private TextInputLayout editUserNicknameLayout;
    private TextInputEditText editUserNickname;
    private TextInputEditText editUserComment;
    private boolean userNameCondition;
    private boolean userNickNameCondition;

    //카메라 + 저장---------------------------------------------------------------------------------
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;

    private Boolean isCamera = false;
    private File tempFile;
    private File sendFile;
    private String imageToString;
    private ImageView profileImageView;

    //데이트 피커 선언-------------------------------------------------------------------------------
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile);


        //앞에서 아이디와 비번 받아오기
        Intent getIntent = getIntent();
        textId = getIntent.getStringExtra("id");
        textPw = getIntent.getStringExtra("pw");


        //액티비티내의 뷰 연동 및 클릭시의 동작 설정------------------------------------------------
        userNameLayout = findViewById(R.id.userNameLayout);
        userName = findViewById(R.id.userName);
        editUserNicknameLayout = findViewById(R.id.editUserNicknameLayout);
        editUserNickname = findViewById(R.id.editUserNickname);
        editUserComment = findViewById(R.id.editUserComment);
        userName.addTextChangedListener(userNameTextWatcher);
        editUserNickname.addTextChangedListener(userNickNameTextWatcher);


        findViewById(R.id.profileRegisterBackButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterEmailActivity.class);

            startActivity(intent);

        });

        textGenderSelect = findViewById(R.id.textGenderSelect);
        textGenderSelect.setOnClickListener(v -> {

            final CharSequence[] items = {"남자", "여자"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this
            // 여기서 부터는 알림창의 속성 설정
            builder.setTitle("당신의 성별은?")        // 제목 설정
                    .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                        // 목록 클릭시 설정
                        public void onClick(DialogInterface dialog, int index) {
                            textGenderSelect.setText(items[index]);
                            textGenderSelect.setTextColor(Color.parseColor("#000000"));
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();    // 알림창 객체 생성
            dialog.show();    // 알림창 띄우기


        });

        textBirthdaySelect = findViewById(R.id.textBirthdaySelect);
        textBirthdaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegisterProfileActivity.this, myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        profileImageView = findViewById(R.id.imageViewProfile);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사진 가져오는 다이얼로그 생성하는 메소드 실행
                makeDialog();
            }
        });

        registerFinishButton = findViewById(R.id.profileRegisterNextButton);
        registerFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //유저 데이터를 저장한 쉐어드 불러오기
                SharedPreferences prefUser = getSharedPreferences("prefUser", MODE_PRIVATE);
                SharedPreferences.Editor prefUserEditor = prefUser.edit();

                //현재 대기 유저 데이터 쉐어드 가져오기
                SharedPreferences prefWaitUser = getSharedPreferences("prefWaitUser", MODE_PRIVATE);
                SharedPreferences.Editor prefWaitUserEditor = prefWaitUser.edit();

                String profileImage = imageToString;
                TextView textViewUserGender = findViewById(R.id.textGenderSelect);
                TextView textViewUserBirthday = findViewById(R.id.textBirthdaySelect);


                Intent intent = new Intent(RegisterProfileActivity.this, RegisterResultActivity.class);


                //추가 되는 유저 json object 로 만들고 문자열로 내보내기
                User user = new User(false,textId,  textPw ,userName.getText().toString(), textViewUserGender.getText().toString(),
                        textViewUserBirthday.getText().toString(),editUserNickname.getText().toString(),
                        editUserComment.getText().toString(), sendFile.getAbsolutePath());
                JSONObject userJsonObject = new JSONObject();
                userDataToJson(userJsonObject, user);
                Log.w(here, "userDataToJson  @"+  userJsonObject.toString());

                prefUserEditor.putString(user.getUserId(), userJsonObject.toString()); // 키값으로 매치 객체 스트링으로 저장
                prefUserEditor.commit();


                prefWaitUserEditor.putString("waitUser", userJsonObject.toString());
                prefWaitUserEditor.commit();


                //Todo 서비스 관리
                Intent startServiceIntent = new Intent(getApplicationContext(), LunchMatchService.class);
                startService(startServiceIntent);

                //관리자 어플로 회원가입 승인 요청 신호 보내기
                // (브로드캐스트로 - 회원정보를 인텐트에 json구조로 담아서
                sendMyBroadcast(userJsonObject.toString());
                startActivity(intent);


            }
        });


        //권한 허용 메소드
        tedPermission();
    }

    //관리자 어플로 회원가입 승인 요청 신호 보내기
    // (브로드캐스트로 - 회원정보를 인텐트에 json구조로 담아서
    public void sendMyBroadcast(String userData) {
        Intent intent = new Intent(JoinApprovalReceiver.JOIN_APPROVAL);
        intent.putExtra("userData",userData);
        sendBroadcast(intent);
    }


    //날짜 양식 맞추어 주는 메소드
    //캘린더에서 시간 받아서 자동으로 자신이 설정한 양식으로 맞추어준다
    private void updateLabel() {
        String myFormat = "yyyy/MM/dd";    // 출력형식   2018/11/28
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        textBirthdaySelect = findViewById(R.id.textBirthdaySelect);
        textBirthdaySelect.setText(sdf.format(myCalendar.getTime()));
        textBirthdaySelect.setTextColor(Color.parseColor("#000000"));

    }


    TextWatcher userNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length() <1){
                userNameLayout.setError("공백입니다");
                userNameCondition = false;

            }
            else{
                userNameLayout.setError(null);
                userNameCondition = true;


            }

        }
    };

    TextWatcher userNickNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length() <1){
                editUserNicknameLayout.setError("공백입니다");
                userNickNameCondition = false;

            }
            else{
                editUserNicknameLayout.setError(null);
                userNickNameCondition = true;


            }

        }
    };



    private void makeDialog(){

        //다이얼로그 설정
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(RegisterProfileActivity.this , R.style.Dialog);
        dialogBuilder.setTitle("사진 업로드").setIcon(R.drawable.camera_check).setCancelable(

                false).setPositiveButton("사진촬영",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 사진 촬영 클릭
                        Log.w("알림", "다이얼로그 > 사진촬영 선택");
                        takePhoto();
                    }

                }).setNegativeButton("앨범선택",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        Log.w("알림", "다이얼로그 > 앨범선택 선택");
                        //앨범에서 선택
                        goToAlbum();
                    }
                }).setNeutralButton("취소   ",

                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.w("알림", "다이얼로그 > 취소 선택");
                        // 취소 클릭. dialog 닫기.
                        dialog.cancel();
                    }
                });

        //다이얼로그 보여주기
        androidx.appcompat.app.AlertDialog alert = dialogBuilder.create();
        alert.show();


        // AlertDialog의 크기 수정
        WindowManager.LayoutParams params = alert.getWindow().getAttributes();
        params.width = 1000;
        params.height = 370;
        alert.getWindow().setAttributes(params);

    }


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
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

            if (tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.w(here, tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    } else {
                        Log.w(here, "tempFile 삭제 실패");
                    }

                } else {
                    Log.w(here, "tempFile 존재하지 않음");
                }
            } else {
                Log.w(here, "tempFile is null");
            }

            return;
        }

        if (requestCode == PICK_FROM_ALBUM) {

            Uri photoUri = data.getData();
            Log.w(here, "PICK_FROM_ALBUM photoUri : " + photoUri);

            Cursor cursor = null;

            try {

                /*
                 *  Uri 스키마를
                 *  content:/// 에서 file:/// 로  변경한다.
                 */
                String[] proj = {MediaStore.Images.Media.DATA};

                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                tempFile = new File(cursor.getString(column_index));

                Log.w(here, "tempFile Uri : " + Uri.fromFile(tempFile));

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            setImage();

        } else if (requestCode == PICK_FROM_CAMERA) {

            setImage();

        }
    }

    /**
     *  앨범에서 이미지 가져오기
     */
    private void goToAlbum() {
        isCamera = false;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }


    /**
     *  카메라에서 이미지 가져오기
     */
    private void takePhoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                Uri photoUri = FileProvider.getUriForFile(this,
                        "june.second.lunchmatchmaker.provider", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            } else {

                Uri photoUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            }
        }
    }

    /**
     *  폴더 및 파일 만들기
     */
    private File createImageFile() throws IOException {

        // 이미지 파일 이름 ( blackJin_{시간}_ )
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "blackJin_" + timeStamp + "_";

        // 이미지가 저장될 파일 주소 ( blackJin )
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/blackJin/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.w(here, "createImageFile : " + image.getAbsolutePath());

        return image;
    }

    /**
     *  tempFile 을 bitmap 으로 변환 후 ImageView 에 설정한다.
     */
    private void setImage() {

        //이미지 크기 조절
        ImageView imageView = findViewById(R.id.imageViewProfile);

        //비트맵으로 이미지 설정하기
        ImageResizeUtils.resizeFile(tempFile, tempFile, 1280, isCamera);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        Log.w(here, "setImage : " + tempFile.getAbsolutePath());

        imageView.setImageBitmap(originalBm);

        //인텐트로 전송하거나
        //후에 저장을 위해 스트링으로 변환하기
        imageToString = BitMapToString(originalBm);
        Log.w("BitmapString", imageToString);

        /**
         *  tempFile 사용 후 null 처리를 해줘야 합니다.
         *  (resultCode != RESULT_OK) 일 때 tempFile 을 삭제하기 때문에
         *  기존에 데이터가 남아 있게 되면 원치 않은 삭제가 이뤄집니다.
         */
        sendFile = tempFile;
        Log.w(here, "setImage: tempFile"+tempFile.toString() );

        tempFile = null;

    }

    //비트맵을 스트링으로 바꾸어준는 메소드
    public String BitMapToString(Bitmap bitmap){

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);

        byte [] b=baos.toByteArray();

        String temp= Base64.encodeToString(b, Base64.DEFAULT);

        return temp;

    }


    private void userDataToJson(JSONObject jsonObject, User user) {


        try {
            jsonObject.put("userApproval", user.isUserApproval());
            jsonObject.put("userId", user.getUserId());
            jsonObject.put("userPw", user.getUserPw());
            jsonObject.put("userName", user.getUserName() );
            jsonObject.put("userGender", user.getUserGender());
            jsonObject.put("userBirthday", user.getUserBirthday());
            jsonObject.put("userNickName", user.getUserNickName() );
            jsonObject.put("userComment", user.getUserComment());
            jsonObject.put("userProfilePath", user.getUserProfilePath());



        } catch (JSONException e) {
            e.printStackTrace();
        }
//        receiveObject(jsonObject);
    }
}
