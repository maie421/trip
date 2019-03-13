package com.example.admin.trip;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
                    task.execute("stu.dothome.co.kr/TripDB/LoginCheck.php",JoinId.getText().toString());
                }catch (UnsupportedOperationException e){
                    e.printStackTrace();
                }
                Intent intent=new Intent(getApplicationContext(),menu1.class);
                startActivity(intent);
            }
        });


    }
    class JoinCheck extends AsyncTask<String, Void, String>{
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
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            String id=(String)strings[1];

            String serverURL=(String)strings[0];
            String postParameters=null;

            postParameters="id="+id;
            return null;
            //////2019.03.14
        }
    }
}
