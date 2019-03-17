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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

public class Plus extends AppCompatActivity {
    ListView listView;
    PlusAdapter plusAdapter;
    private String mJsonString;
    ArrayList<PlusItem> items=new ArrayList<PlusItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plus);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /////////////리스트뷰///////////////
        listView=(ListView)findViewById(R.id.list);
        plusAdapter=new PlusAdapter(items,getApplicationContext());
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        String text = sf.getString("text","");

        List task=new List();
        task.execute("http://stu.dothome.co.kr/TripDB/addressbook.php",text);

        ///////////////////////////////

        Button result1=(Button)findViewById(R.id.button);//확인
        result1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Countrysetting.class);
                startActivityForResult(intent,1);
            }
        });
    }
    class List extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            String id = (String)strings[1];

            String serverURL = (String)strings[0];
            String postParameters = null;

            postParameters = "id="+id;

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

        private void showResult() {
            String result = null;
            String TAG_JSON = "webnautes";
            String TAG_ID = "id";
            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                Log.d("디비", "됨?");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    result = item.getString(TAG_ID);
                    plusAdapter.addItem(new PlusItem(result));
                    listView.setAdapter(plusAdapter);
                }
            } catch (JSONException e) {
                Log.d("디비", "showResult : ", e);
            }
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if(requestCode==1){
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;
    }

}
class PlusAdapter extends BaseAdapter {
    ArrayList<PlusItem> items;
    Context context;

    public PlusAdapter(ArrayList<PlusItem> items,Context context){
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
    public void addItem(PlusItem item){
        items.add(item);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlusItemView view=new PlusItemView(context);
        PlusItem item=items.get(position);

        view.setName(item.name);
        view.setImg(item.img);

        return view;
    }


}
class PlusItemView extends LinearLayout {
    TextView name;
    ImageView img;
    PlusItemView(Context context){
        super(context);
        inin(context);
    }
    PlusItemView(Context context, AttributeSet attrs){
        super(context,attrs);
        inin(context);
    }
    public void inin(Context context){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.add_friend_list_view,this,true);
        name=(TextView)findViewById(R.id.textView);
        img=(ImageView)findViewById(R.id.imageView);
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setImg(Bitmap img) {
        this.img.setImageBitmap(img);
    }
}
class PlusItem {
    String name;
    Bitmap img;

    public PlusItem(String name){
        this.name=name;
    }
    public PlusItem(String name,Bitmap img){
        this.name=name;
        this.img=img;
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
}

