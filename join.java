package com.example.admin.trip;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.MediaStore.Images;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
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

public class join extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1; //메모리할당 한번만 final 값변경 X
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_IMAGE_CROP = 3;
    EditText name,pass,id,pass_chek;
    ImageView img;
    Bitmap photo;
    private String mJsonString;
    String mCurrentPhotoPath,image;
    Uri photoURI,albumURI=null;
    Boolean album = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        final Button join = (Button) findViewById(R.id.button);
        name = (EditText) findViewById(R.id.name);
        pass = (EditText) findViewById(R.id.pass);
        pass_chek=(EditText)findViewById(R.id.pass1);
        id = (EditText) findViewById(R.id.id);
        img = (ImageView) findViewById(R.id.imageView);

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //////빈칸 체크//////
                if (name.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(join.this, "이름을 적어주세요", Toast.LENGTH_SHORT).show();
                } else if (id.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(join.this, "아이디를 적어주세요", Toast.LENGTH_SHORT).show();
                } else if (pass.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(join.this, "비밀번호를 적어주세요", Toast.LENGTH_SHORT).show();
                }else if(pass_chek.getText().toString().replace(" ","").equals(" ")){
                    Toast.makeText(join.this,"비밀번호를 적어주세요",Toast.LENGTH_SHORT).show();
                }else if(!(pass.getText().toString().equals(pass_chek.getText().toString()))){
                    Toast.makeText(join.this, "비밀번호가 다릅니다", Toast.LENGTH_SHORT).show();
                } else {
                    //BitMapToString(photo);
                    InsertData task = new InsertData();
                    try {
                        task.execute("http://stu.dothome.co.kr/TripDB/member.php",name.getText().toString(),id.getText().toString(),pass.getText().toString(),URLEncoder.encode(image,"utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                ////////////////////빈칸체크
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"갤러리", "사진찍기"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(join.this);

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

    }
    class InsertData extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d("디비", "response - " + result);

            if (result == null){
                Log.d("디비", "result:null " + result);
            }
            else {
                mJsonString = result;
                showResult();
            }
        }
        @Override
        protected String doInBackground(String... params) {

            String name = (String)params[1];
            String id = (String)params[2];
            String pass=(String)params[3];
            String img=(String)params[4];

            String serverURL = (String)params[0];
            String postParameters = null;

            postParameters = "name=" + name + "&id=" + id+"&pass="+pass+"&img="+img;

            try {
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
                Log.d("디비", "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString();
            } catch (Exception e) {

                Log.d("디비", "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    private void showResult() {
        String result = null;
        String TAG_JSON = "webnautes";
        String TAG_ID = "result";
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            Log.d("디비", "됨?");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                result = item.getString(TAG_ID);
                Log.d("디비",result);
                if(result.equals("1")) {//아이디가 존재 안할때
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    finish();
                }else
                    Toast.makeText(join.this, "중복된 아이디 입니다.", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.d("디비", "showResult : ", e);
        }
    }
    private File createImageFile() throws IOException{
        String imageFileName="tmp_"+String.valueOf(System.currentTimeMillis())+".jpg";
        File storageDir=new File(Environment.getExternalStorageDirectory(),imageFileName);
        mCurrentPhotoPath=storageDir.getAbsolutePath();//실행시킨 위치 정보도 함께 반환
        return storageDir;
    }
    private void AlbumAction(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType(Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,REQUEST_TAKE_PHOTO );
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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
    /* public void BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);    //bitmap compress
        byte [] arr=baos.toByteArray();
        image= Base64.encodeToString(arr, Base64.DEFAULT);
        String temp="";
        try{
            temp="&imagedevice="+ URLEncoder.encode(image,"utf-8");
        }catch (Exception e){
            Log.e("exception",e.toString());
        }
    }*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            img.setImageBitmap(imageBitmap);
        }
    }
    /*@Override
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
                    photo= Images.Media.getBitmap(getContentResolver(), photoURI);
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
        }*/
    }


