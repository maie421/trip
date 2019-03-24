package com.example.admin.trip;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String mJsonString;
    TextView JoinId,Joinpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button join=(Button)findViewById(R.id.join);
        Button menu=(Button)findViewById(R.id.button);
        JoinId=(TextView)findViewById(R.id.id);
        Joinpass=(TextView)findViewById(R.id.pass);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),join.class);
                startActivity(intent);
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinCheck task=new JoinCheck();
                try{
                    task.execute("http://stu.dothome.co.kr/TripDB/LoginCheck.php",JoinId.getText().toString(),Joinpass.getText().toString());
                }catch (UnsupportedOperationException e){
                    e.printStackTrace();
                }
            }
        });
    }
    class JoinCheck extends AsyncTask<String, Void, String> {
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
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            String id = (String) strings[1];
            String pass = (String) strings[2];

            String serverURL = (String) strings[0];
            String postParameters = null;

            postParameters = "id=" + id +"&pass="+ pass;

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
                if(result.equals("1")) {//아이디가 존재 할때
                    SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);

                    //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String text = JoinId.getText().toString(); // 사용자가 입력한 저장할 데이터
                    editor.putString("text",text); // key, value를 이용하여 저장하는 형태
                    //다양한 형태의 변수값을 저장할 수 있다.
                    //editor.putString();
                    //editor.putBoolean();
                    //editor.putFloat();
                    //editor.putLong();
                    //editor.putInt();
                    //editor.putStringSet();
                    //최종 커밋
                    editor.commit();
                    Intent intent=new Intent(getApplicationContext(),menu1.class);
                    startActivity(intent);
                }else
                    Toast.makeText(MainActivity.this, "아이디와 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("디비", "showResult : ", e);
        }
    }
}
