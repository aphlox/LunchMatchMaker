package june.second.lunchmatchmaker.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import june.second.lunchmatchmaker.Item.User;
import june.second.lunchmatchmaker.R;

public class StoryWriteActivity extends AppCompatActivity {
    //디버깅을 위한 값
    String here = "StoryWriteActivity";


    User nowUser;


    //카메라 + 저장---------------------------------------------------------------------------------
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;

    private Boolean isCamera = false;
    private File tempFile;
    private File sendFile;
    private String storyKey;
    private String imageToString;
    private EditText storyContentEditText;
    //----------------------------------------------------------------------------------------------


    //기타------------------------------------------------------------------------------------------
    private ImageView back;             //뒤로 가는 이미지뷰
    private ImageView check;            //스토리 추가를 확인하는 이미지뷰
    private LinearLayout imageAddBar;   //이미지 추가 버튼(레이아웃 자체가 버튼처럼 클릭되게 설정)
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_write);




        //액티비티내의 뷰 연동 및 클릭시의 동작 설정------------------------------------------------
        //스토리 내용을 편집하는 editText
        storyContentEditText= findViewById(R.id.editContentStoryEdit);

        //뒤로 가는 버튼
        //다시 StoryActivity 로 이동
        back = findViewById(R.id.storyWriteBack);
        back.setOnClickListener(v ->{
            Intent intent = new Intent(this, StoryActivity.class);
            //플래그로 저장 임시 구현
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            startActivity(intent);
        });

        //스토리 추가하는 체크 표시 이미지뷰
        check = findViewById(R.id.storyWriteCheck);
        check.setOnClickListener(v ->{

            //현재 변경된 값으로 새로운 스토리를 만들고
            //변경하기 위해 받은 위치값에 새로 스토리 객체를 바꾸어 넣어준다
            Intent intent = new Intent(this, StoryActivity.class);

//            //새로운 스토리(클래스) 만들기
//            Story story = new Story("entry.getKey()", storyContentEditText.getText().toString() , imageToString);
//
//            //새로운 스토리를 처음에 넣어주기
//            storyArrayList.add(0,story);



            //현재 유저 불러오기
            //로그인 될때 해당 유저를 prefNowUser 에 접속 유저로 저장해놓은것
            SharedPreferences prefNowUser = getSharedPreferences("prefNowUser", MODE_PRIVATE);
            SharedPreferences.Editor prefNowUserEditor = prefNowUser.edit();

            try {
                JSONObject nowUserJsonObject = new JSONObject(prefNowUser.getString("nowUser", "    "));
                nowUser = new User(nowUserJsonObject.getBoolean("userApproval"), nowUserJsonObject.getString("userId"), nowUserJsonObject.getString("userPw"), nowUserJsonObject.getString("userName")
                        , nowUserJsonObject.getString("userGender"), nowUserJsonObject.getString("userBirthday"), nowUserJsonObject.getString("userNickName")
                        , nowUserJsonObject.getString("userComment"), nowUserJsonObject.getString("userProfilePath"));

            } catch (
                    JSONException e) {
                e.printStackTrace();
            }



            //shared 데이터 저장
            //갤러리 이미지들 데이터 저장 구현
            SharedPreferences prefGallery = getSharedPreferences("imageGallery", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefGallery.edit();


            //스토리 데이터를 쉐어드에 한키로 저장하는데
            //이미지 문자열(이미지를 스트링으로 변환한)과 스토리 내용을 담은 string 을 저장하는데
            //가운데 구분자로 "|"를 넣어서 구분한다
            //이미지 문자열에서는 | 가 안들어가고
            //나중에 데이터를 받아서 구분자 | 로 나눌때 처음 나오는 | 를 기준으로
            //2등분만 하기 때문에 스토리 내용에서 구분자인 | 를 아무리 입력해도
            //데이터 저장하고 불러올때 문제가 없다
            String storyMakerProfileImage = nowUser.getUserProfilePath();
            String storyMakerNickname = nowUser.getUserNickName();
            String storyMaker =  storyMakerProfileImage.concat("|"+storyMakerNickname);
            String StoryImage = imageToString;
            String StoryContent = storyContentEditText.getText().toString();
            String StoryImageNContent = StoryImage.concat("|"+StoryContent);
            String storyItem = storyMaker.concat("|"+StoryImageNContent);
            Log.w("StoryWriteActivity", "storyItem  :  " + storyItem);


            //키값은 저장될때마다 마지막으로 저장된 키값+1 해줘서 중복안되게 하기
            storyKey =  Integer.toString(prefGallery.getInt("lastIndex",0) +1);

            Log.w(here, "storyKey: " + storyKey);
            editor.putInt("lastIndex", Integer.parseInt(storyKey) );
            editor.putString(storyKey, storyItem);
            editor.commit();

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            startActivity(intent);
        });

        //이미지 추가하는 버튼(레이아웃)
        imageAddBar = findViewById(R.id.imageEditBar);
        imageAddBar.setOnClickListener(v ->{
            //사진 가져오는 다이얼로그 생성하는 메소드 실행
            makeDialog();

         });

        //권한 허용 메소드
        tedPermission();

        //------------------------------------------------------------------------------------------


    }

    private void makeDialog(){

        //다이얼로그 설정
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(StoryWriteActivity.this , R.style.Dialog);
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
        AlertDialog alert = dialogBuilder.create();
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
                        Log.e(here, tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }

            return;
        }

        switch (requestCode) {
            case PICK_FROM_ALBUM: {

                Uri photoUri = data.getData();
                Log.d(here, "PICK_FROM_ALBUM photoUri : " + photoUri);

                cropImage(photoUri);

                break;
            }
            case PICK_FROM_CAMERA: {

                Uri photoUri = Uri.fromFile(tempFile);
                Log.d(here, "takePhoto photoUri : " + photoUri);

                cropImage(photoUri);

                break;
            }
            case Crop.REQUEST_CROP: {
                //File cropFile = new File(Crop.getOutput(data).getPath());
                setImage();
            }
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
        isCamera = true;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {

            /**
             *  안드로이드 OS 누가 버전 이후부터는 file:// URI 의 노출을 금지로 FileUriExposedException 발생
             *  Uri 를 FileProvider 도 감싸 주어야 합니다.
             *
             *  참고 자료 http://programmar.tistory.com/4 , http://programmar.tistory.com/5
             */
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                Uri photoUri = FileProvider.getUriForFile(this,
                        "june.second.lunchmatchmaker.provider", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            } else {

                Uri photoUri = Uri.fromFile(tempFile);
                Log.d(here, "takePhoto photoUri : " + photoUri);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            }
        }
    }



    /**
     *  Crop 기능
     */
    private void cropImage(Uri photoUri) {

        Log.d(here, "tempFile : " + tempFile);

        /**
         *  갤러리에서 선택한 경우에는 tempFile 이 없으므로 새로 생성해줍니다.
         */
        if(tempFile == null) {
            try {
                tempFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                finish();
                e.printStackTrace();
            }
        }

        //크롭 후 저장할 Uri
        Uri savingUri = Uri.fromFile(tempFile);

        Crop.of(photoUri, savingUri).asSquare().start(this);
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
        ImageView imageView = findViewById(R.id.imageViewStoryEdit);

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

}
