package com.example.admin.trip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.net.MalformedURLException;
import java.net.URL;

public class join extends AppCompatActivity {
    EditText name,pass,id,pass_chek;
    String nameInput,passInput,idInput,pass_chekInput;
    private String mJsonString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        final Button join = (Button) findViewById(R.id.button);
        name = (EditText) findViewById(R.id.name);
        pass = (EditText) findViewById(R.id.pass);
        pass_chek=(EditText)findViewById(R.id.pass1);
        id = (EditText) findViewById(R.id.id);

        nameInput = name.getText().toString();
        passInput = pass.getText().toString();
        pass_chekInput=pass_chek.getText().toString();
        idInput = id.getText().toString();

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
                    InsertData task = new InsertData();
                    task.execute("http://stu.dothome.co.kr/TripDB/member.php",name.getText().toString(),id.getText().toString(),pass.getText().toString());
                }
                ////////////////////빈칸체크
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

            String serverURL = (String)params[0];
            String postParameters = "name=" + name + "&id=" + id+"&pass"+pass;

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
                    Toast.makeText(join.this, "중복된 아이디 입니다"+result, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.d("디비", "showResult : ", e);
        }
    }
}
