package com.example.admin.trip;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Countrysetting extends AppCompatActivity {
    String mJsonString;
    ArrayList<String> personnel;
    EditText edittext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countrysetting);

        edittext=(EditText)findViewById(R.id.editText);
        Button button=(Button)findViewById(R.id.button);

        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        final String text = sf.getString("text","");

        Intent intent = getIntent() ;
        personnel=new ArrayList<String>();
        personnel=intent.getStringArrayListExtra("personnel");
        personnel.add(text+",");//마지막 본인 아이디 ,추가

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountryAdd add=new CountryAdd();
                try{
                    add.execute("http://stu.dothome.co.kr/TripDB/countrymenuadd.php",String.valueOf(personnel),edittext.getText().toString());
                }catch (UnsupportedOperationException e){
                    e.printStackTrace();
                }

                Log.d("디비", String.valueOf(personnel)+"최종");
                Intent intent=new Intent(getApplicationContext(),menu1.class);
                /*finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
                intent.putExtra("room",edittext.getText().toString());
                finish();
                //startActivity(intent);
            }
        });
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

            String personnel = (String) strings[1];
            String room = (String) strings[2];

            String serverURL = (String) strings[0];
            String postParameters = null;

            postParameters = "personnel=" + personnel +"&room="+ room;

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
}

