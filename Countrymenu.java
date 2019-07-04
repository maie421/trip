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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ForkJoinPool;

/*나라안에 들어가면 리스트*/
public class Countrymenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ListView listView;
    CountryAdapter countryAdapter;
    private String mJsonString,personnel,room;
    ArrayList<CountryItem> items=new ArrayList<CountryItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countrymenu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Countryadd.class);
                startActivityForResult(intent,1);
            }
        });
        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        personnel = sf.getString("personnel","");
        room=sf.getString("room","");

        Information task=new Information();
        task.execute("http://stu.dothome.co.kr/TripDB/Countrydetailadd.php",personnel,room);

        /////////////리스트뷰///////////////
        listView=(ListView)findViewById(R.id.list);
        countryAdapter=new CountryAdapter(items,getApplicationContext());

        ///////////////////////////////
    }
    /*db 연결*/
    class Information extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s==null)
                Log.d("디비","result:null"+s);
            else {
                mJsonString = s;
                showResult();
            }
        }
        private void showResult() {
            String id = null;
            String stay = null;
            String text = null;
            String number=null;
            Bitmap img;
            String encodedImag;
            String TAG_JSON = "webnautes";
            String TAG_ID = "id";
            String TAG_stay = "stay";
            String TAG_text = "text";
            String TAG_img = "img";
            String TAG_number = "num";
            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                Log.d("디비", "됨?");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    id = item.getString(TAG_ID);
                    stay = item.getString(TAG_stay);
                    text = item.getString(TAG_text);
                    number = item.getString(TAG_number);
                    String encodedImage=item.getString(TAG_img);
                    byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    countryAdapter.addItem(new CountryItem(text,stay,id,decodedByte,number));
                }
                listView.setAdapter(countryAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("디비", "showResult : ", e);
            }
        }
        @Override
        protected String doInBackground(String... strings) {
            String personnel=(String)strings[1];
            String room=(String)strings[2];

            String serverURL=(String)strings[0];
            String postParameters = "personnel="+personnel+"&room="+room;

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            items.clear();
            Log.d("디비ㅇ", String.valueOf(requestCode));
            Information task=new Information();
            try{
                task.execute("http://stu.dothome.co.kr/TripDB/Countrydetailadd.php",personnel,room);
            }catch (UnsupportedOperationException e){
                e.printStackTrace();
            }//DB넣기
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
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
class CountryAdapter extends BaseAdapter {
    ArrayList<CountryItem> items;
    Context context;

    public CountryAdapter(ArrayList<CountryItem > items,Context context){
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

    @Override
    public long getItemId(int position) {
        return position;
    }
    public void addItem(CountryItem  item){
        items.add(item);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CountryItemView  view=new CountryItemView(context);
        CountryItem  item=items.get(position);

        view.setId(item.id);
        view.setName(item.name);
        view.setImg(item.img);
        view.setStay(item.stay);
        view.setNumber(item.number);

        return view;
    }

}
class CountryItemView extends LinearLayout {
    TextView name,stay,id;
    Button select,delete;
    Context mcontext;
    ImageView img;
    CountryItemView (Context context){
        super(context);
        mcontext = context;
        inin(context);
    }
    CountryItemView (Context context, AttributeSet attrs){
        super(context,attrs);
        inin(context);
    }



    public void inin(Context context){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.content_conutrymenu_list_view,this,true);
        id=(TextView)findViewById(R.id.name);
        name=(TextView)findViewById(R.id.textView);
        img=(ImageView)findViewById(R.id.imageView6);
        stay=(TextView)findViewById(R.id.stay);
        select = (Button) findViewById(R.id.button1);//선택
        delete=(Button)findViewById(R.id.button);//삭제
    }
    public void setNumber(final String number) {
        select.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.d("확인","선택"+number);
            }
        });
        delete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Informationdelete task=new Informationdelete();
                task.execute("http://stu.dothome.co.kr/TripDB/countrymenudelte.php",number);
                //items.clear();
            }
        });
    }
    class Informationdelete extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s==null)
                Log.d("디비","result:null"+s);
            else {
                String mJsonString = s;
            }
        }
        @Override
        protected String doInBackground(String... strings) {
            String num=(String)strings[1];

            String serverURL=(String)strings[0];
            String postParameters = "num="+num;

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

    public void setId(String id) {
        this.id.setText(id);
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setImg(Bitmap img) {
        this.img.setImageBitmap(img);
    }
    public void setStay(String stay) {
        this.stay.setText(stay);
    }
}
class CountryItem {
    String name,stay,id,number;
    Bitmap img;

    public CountryItem (String name, String stay,String id,String number){
        this.name=name;
        this.stay=stay;
        this.id=id;
        this.number=number;
    }
    public CountryItem (String name, String stay,String id,Bitmap img,String number){
        this.name=name;
        this.stay=stay;
        this.id=id;
        this.img=img;
        this.number=number;
    }
    public CountryItem (String name,Bitmap img){
        this.name=name;
        this.img=img;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }
    public String getStay() {
        return stay;
    }

    public void setStay(String stay) {
        this.stay = stay;
    }
}

