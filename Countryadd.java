/*package com.example.admin.trip;

import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import static com.example.admin.trip.menu.dlDrawer;

public class Plus extends AppCompatActivity {

//사이드 탭메뉴에 표시될 텍스트 배열

    //private ListView lvNavList;

    Toolbar toolbar;
    private ActionBarDrawerToggle dtToggle;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_plus);

        menu.lvNavList = (ListView)findViewById(R.id.lv_activity_main_nav_list);

        menu.flContainer = (FrameLayout)findViewById(R.id.container);

        menu.lvNavList.setAdapter(
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menu.navItems));
        menu.lvNavList.setOnItemClickListener(new menu.DrawerItemClickListener(getApplicationContext()));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name);
        dlDrawer.setDrawerListener(dtToggle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //api 22 에서부터 쓰기시작.
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;
    }*/
/*
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        dtToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dtToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (dtToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
*/
package com.example.admin.trip;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
;import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Countryadd extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1; //메모리할당 한번만 final 값변경 X
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_IMAGE_CROP = 3;

    String mCurrentPhotoPath,image;
    Uri photoURI,albumURI=null;
    Boolean album = false;
    ImageView img;
    Bitmap photo;
    String mJsonString,text,room,personnel;
    EditText stay,story;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countryadd);

        img=(ImageView) findViewById(R.id.imageView);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"갤러리", "사진찍기"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Countryadd.this);

                // 제목셋팅
                alertDialogBuilder.setTitle("선택 목록 대화 상자");
                alertDialogBuilder.setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i){
                                // 프로그램을 종료한다
                                switch (i){
                                    case 0:
                                        AlbumAction();
                                        break;
                                    case 1:
                                        dispatchTakePictureIntent();
                                        break;
                                }
                                Toast.makeText(getApplicationContext(), items[i] + " 선택했습니다.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                // 다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();

                // 다이얼로그 보여주기
                alertDialog.show();

            }
        });
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        text = sf.getString("text","");//헌재 id불러오기
        room=sf.getString("room","");//방
        personnel=sf.getString("personnel","");//모임

        Button button=(Button)findViewById(R.id.button);
        stay=(EditText)findViewById(R.id.editText);
        story=(EditText)findViewById(R.id.editText1);//내용

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitMapToString(photo);
                //BitMapToString1(photo);
                CountryAdd add=new CountryAdd();
                try {
                    add.execute("http://stu.dothome.co.kr/TripDB/Countrydetail.php",text,stay.getText().toString(),story.getText().toString(),URLEncoder.encode(image,"utf-8"),room,personnel);
                    Toast.makeText(Countryadd.this,personnel, Toast.LENGTH_SHORT).show();
                    finish();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private File createImageFile() throws IOException{
        String imageFileName="tmp_"+String.valueOf(System.currentTimeMillis())+".jpg";
        File storageDir=new File(Environment.getExternalStorageDirectory(),imageFileName);
        mCurrentPhotoPath=storageDir.getAbsolutePath();//실행시킨 위치 정보도 함께 반환
        return storageDir;
    }
    private void AlbumAction(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,REQUEST_TAKE_PHOTO );
    }
    private void dispatchTakePictureIntent(){
        Intent takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE,null);
        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            File photoFile=null;
            try{
                photoFile=createImageFile();
            }catch (IOException e) {
                Toast.makeText(getApplicationContext(), "createImageFile Failed", Toast.LENGTH_SHORT).show();
            }
            if(photoFile !=null){
                photoURI=Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    private void cropImage(){
        Log.d("리스트","crop");
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setDataAndType(photoURI,"image/*");
        cropIntent.putExtra("scale",true);
        cropIntent.putExtra( "aspectX",1);
        cropIntent.putExtra( "aspectY", 1);
        cropIntent.putExtra( "outputX", 200);
        cropIntent.putExtra( "outputY", 200);

        Log.d("들어왔다","자르기");
        if(album==false){
            cropIntent.putExtra("output",photoURI);
        }else if(album==true){
            cropIntent.putExtra("output",albumURI);
        }
        startActivityForResult(cropIntent,REQUEST_IMAGE_CROP);
    }
    ////비트맵 bool로 변경//////
    public void BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,50, baos);    //bitmap compress
        byte [] arr=baos.toByteArray();
        image= Base64.encodeToString(arr, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                album = true;
                File albumFile = null;
                try {
                    albumFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (albumFile != null) {
                    albumURI = Uri.fromFile(albumFile);
                }
                photoURI = data.getData();
                photo = null;
                try {
                    photo= MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                img.setImageBitmap(photo);
                break;
            case REQUEST_IMAGE_CAPTURE:
                cropImage();
                break;
            case REQUEST_IMAGE_CROP:
                photo=BitmapFactory.decodeFile(photoURI.getPath());
                img.setImageBitmap(photo);
                Intent mediaScanIntent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                if(album==false){
                    mediaScanIntent.setData(photoURI);
                }else if(album==true){
                    album=false;
                    mediaScanIntent.setData(albumURI);
                    Log.d("오류","albumURI");
                }
                this.sendBroadcast(mediaScanIntent); //송신
                break;
        }
    }
    class CountryAdd extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d("디비", "response - " + result);

            if (result == null) {
                Log.d("디비", "result:null " + result);
            } else {
                mJsonString = result;
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            String id = (String) strings[1];
            String stay= (String) strings[2];
            String text=(String) strings[3];
            String img=(String)strings[4];
            String room=(String)strings[5];
            String personnel=(String)strings[6];

            String serverURL = (String) strings[0];
            String postParameters = null;

            postParameters = "id=" + id +"&stay="+ stay+"&text="+ text+"&img="+ img+"&country="+ room+"&personnel="+personnel;

            try {
                //FileInputStream mFileInputStream = new FileInputStream(img);
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));

                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;

                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuffer sb = new StringBuffer();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString();
            } catch (IOException e) {
                Log.d("디비", "InsertData: Error ", e);

                return new String("ERROR:" + e.getMessage());
            }
        }

    }
}



