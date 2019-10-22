package june.second.lunchmatchmaker.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import june.second.lunchmatchmaker.R;

import static june.second.lunchmatchmaker.Activity.StoryActivity.storyArrayList;

public class  StoryEditActivity extends AppCompatActivity {

    private static final String TAG = "StoryEditActivity";


    //카메라----------------------------------------------------------------------------------------
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;

    private Boolean isCamera = false;
    private File tempFile;
    private String imageToString;
    private EditText storyContentEditText;
    private ImageView imageViewStoryEdit;
    //----------------------------------------------------------------------------------------------


    //기타------------------------------------------------------------------------------------------
    private ImageView back;             //뒤로 가는 이미지뷰
    private ImageView check;            //스토리 수정을 확인하는 이미지뷰
    private LinearLayout imageEditBar;   //이미지 변경 버튼(레이아웃 자체가 버튼처럼 클릭되게 설정)
    private long mLastClickTime = 0;
    //----------------------------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_edit);





        //스토리 편집-------------------------------------------------------------------------------
        //인텐트에서 해당 아이템 위치 받아와서
        Intent getIntent = getIntent();
        int storyPosition = getIntent.getIntExtra("position",0);
        String storyKey = getIntent.getStringExtra("key");

        storyContentEditText= findViewById(R.id.editContentStoryEdit);  //스토리 내용을 편집하는 editText
        imageViewStoryEdit = findViewById(R.id.imageViewStoryEdit);     //스토리 이미지를 나타내는 imageView

        //편집을 위해 편집하려는 아이템의 위치값을 받아아고
        //해당 위치의 아이템의 내용을 셋팅해주기
        //해당 스토리의 내용과 이미지를 지금 StoryEditActivity 에 넣어주기
        //편집하려는 스토리의 내용으로 초기화
        storyContentEditText.setText(storyArrayList.get(storyPosition).getStoryContent());
        //편집하려는 스토리의 이미지로 초기화
        imageViewStoryEdit.setImageBitmap(StringToBitMap(storyArrayList.get(storyPosition).getStoryValue()));
        imageToString = storyArrayList.get(storyPosition).getStoryValue();
        //------------------------------------------------------------------------------------------



        //액티비티내의 뷰 연동 및 클릭시의 동작 설정------------------------------------------------
        //뒤로 가는 버튼
        //다시 StoryActivity 로 이동
        back = findViewById(R.id.storyWriteBack);
        back.setOnClickListener(v ->{
            Intent intent = new Intent(this, StoryActivity.class);
            //플래그로 저장 임시 구현
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            startActivity(intent);
        });

        //스토리 수정을 확인하는 체크 표시 이미지뷰
        check = findViewById(R.id.storyWriteCheck);
        check.setOnClickListener(v ->{
            Intent intent = new Intent(this, StoryActivity.class);

            //잘못해서 두번 눌리는 경우 방지
            // 1초 이내에 두번눌리는 경우는 오동작으로 인식해서 무시함
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();



            //현재 변경된 값으로 새로운 스토리를 만들고
            //변경하기 위해 받은 위치값에 새로 스토리 객체를 바꾸어 넣어준다





            //shared 객체 불러오기
            SharedPreferences prefGallery = getSharedPreferences("imageGallery", MODE_PRIVATE);
            SharedPreferences.Editor prefGalleryEditor = prefGallery.edit();
            String StoryImage = imageToString;
            String StoryContent = storyContentEditText.getText().toString();
            String StoryImageNContent = StoryImage.concat("|"+StoryContent);
            Log.w("StoryWriteActivity", "StoryContentNImage  :  " + StoryImageNContent);

            prefGalleryEditor.putString(storyKey, StoryImageNContent);
            prefGalleryEditor.commit();


            startActivity(intent);

        });

        //이미지 변경하는 버튼(레이아웃)
        imageEditBar = findViewById(R.id.imageEditBar);
        imageEditBar.setOnClickListener(v ->{
            //사진 가져오는 다이얼로그 생성하는 메소드 실행
            makeDialog();
         });



        //------------------------------------------------------------------------------------------

    }

    //사진 가져오는 다이얼로그
    private void makeDialog(){

        //다이얼로그 설정
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(StoryEditActivity.this , R.style.Dialog);
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
                        Log.w(TAG, tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    } else {
                        Log.w(TAG, "tempFile 삭제 실패");
                    }

                } else {
                    Log.w(TAG, "tempFile 존재하지 않음");
                }
            } else {
                Log.w(TAG, "tempFile is null");
            }

            return;
        }

        if (requestCode == PICK_FROM_ALBUM) {

            Uri photoUri = data.getData();
            Log.w(TAG, "PICK_FROM_ALBUM photoUri : " + photoUri);

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

                Log.w(TAG, "tempFile Uri : " + Uri.fromFile(tempFile));

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
        Log.w(TAG, "createImageFile : " + image.getAbsolutePath());

        return image;
    }

    /**
     *  tempFile 을 bitmap 으로 변환 후 ImageView 에 설정한다.
     */
    private void setImage() {

        //이미지 크기 조절
        ImageResizeUtils.resizeFile(tempFile, tempFile, 1280, isCamera);

        //비트맵으로 이미지 설정하기
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        Log.w(TAG, "setImage : " + tempFile.getAbsolutePath());

        imageViewStoryEdit.setImageBitmap(originalBm);

        //인텐트로 전송하거나
        //후에 저장을 위해 스트링으로 변환하기
        imageToString = BitMapToString(originalBm);
        Log.w("BitmapString", imageToString);


        /**
         *  tempFile 사용 후 null 처리를 해줘야 합니다.
         *  (resultCode != RESULT_OK) 일 때 tempFile 을 삭제하기 때문에
         *  기존에 데이터가 남아 있게 되면 원치 않은 삭제가 이뤄집니다.
         */
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


    //스트링을 비트맵으로 바꾸어주는 메소드
    public Bitmap StringToBitMap(String encodedString){

        try{

            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);

            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

            return bitmap;

        }catch(Exception e){

            e.getMessage();

            return null;

        }

    }
}
