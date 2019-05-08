package com.example.admin.trip;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
import java.util.ArrayList;
/*세부사항 나라안에 들어가면*/
public class menu1 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SingerAdapter adapter;
    ListView listView;
    String mJsonString;
    String text;
    ArrayList<SingerItem> items = new ArrayList<SingerItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Countryadd 추가
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Plus.class);
                startActivityForResult(intent,1);
            }
        });//Countryadd 추가

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listView=(ListView)findViewById(R.id.list);
        adapter=new SingerAdapter(items,getApplicationContext());
        //DB넣기
        //헌재 id불러오기
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        text = sf.getString("text","");//헌재 id불러오기

        /*SharedPreference 사용*/
        menulist add=new menulist();
        try{
            add.execute("http://stu.dothome.co.kr/TripDB/countrymenu.php",text);
        }catch (UnsupportedOperationException e){
            e.printStackTrace();
        }//DB넣기

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                SingerItem item=(SingerItem)adapter.getItem(position);
                SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);

                //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String room = item.name; // 사용자가 입력한 저장할 데이터
                editor.putString("room",room); // key, value를 이용하여 저장하는 형태
                editor.commit();

                Toast.makeText(menu1.this,item.name, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(),Countrymenu.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            items.clear();
            Log.d("디비ㅇ", String.valueOf(requestCode));
            menulist add=new menulist();
            try{
                add.execute("http://stu.dothome.co.kr/TripDB/countrymenu.php",text);
            }catch (UnsupportedOperationException e){
                e.printStackTrace();
            }//DB넣기
        }
    }
    class menulist extends AsyncTask<String, Void, String> {
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

            String serverURL = (String) strings[0];
            String postParameters = null;

            postParameters = "id=" + id ;

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
        private void showResult() {
            String result = null;
            String TAG_JSON = "webnautes";
            String TAG_Room = "room";
            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    result = item.getString(TAG_Room);
                    adapter.addItem(new SingerItem(result));
                }
                listView.setAdapter(adapter);
            } catch (JSONException e) {
                Log.d("디비", "showResult : ", e);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;
    }
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_manage) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
class SingerAdapter extends BaseAdapter{

    ArrayList<SingerItem> items;
    Context context;

    public SingerAdapter(ArrayList<SingerItem> items,Context context){
        this.items=items;
        this.context=context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    public void addItem(SingerItem item){
        items.add(item);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SingerItemView view=new SingerItemView(context);
        SingerItem item=items.get(position);
        view.setBack(item.back);
        view.setName(item.name);
        return view;
    }
}
