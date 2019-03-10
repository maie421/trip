package com.example.admin.trip;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

public class menu extends AppCompatActivity {
    public static String[] navItems = {"Hot Red","Cadet Blue","Dark OLive Green", "Dark Orange","Golden rod"};

//사이드 탭메뉴에 표시될 텍스트 배열

    public static ListView lvNavList;
    public static FrameLayout flContainer; // 탭마다 표시될 프레임레이아웃
    Toolbar toolbar;
    public static DrawerLayout dlDrawer;
    private ActionBarDrawerToggle dtToggle;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        lvNavList = (ListView)findViewById(R.id.lv_activity_main_nav_list);

        flContainer = (FrameLayout)findViewById(R.id.container);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Plus.class);
                startActivity(intent);
            }
        });

        lvNavList.setAdapter(
         new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navItems){
             @Override
             public View getView(int position, View convertView, ViewGroup parent) {
                 View view = super.getView(position, convertView, parent);
                 TextView text = (TextView) view.findViewById(android.R.id.text1);
                 text.setTextColor(Color.BLACK);
                 return view;
             }
         });
        lvNavList.setOnItemClickListener(new DrawerItemClickListener(getApplicationContext()));
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
    static class DrawerItemClickListener implements ListView.OnItemClickListener{
        public Context context;
        public DrawerItemClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            switch (position) {
                case 0:
                    Toast.makeText(context, "Hot Red", Toast.LENGTH_SHORT).show();
                    flContainer.setBackgroundColor(Color.parseColor("#A52A2A"));
                    break;
                case 1:
                    Toast.makeText(context, "Cadet Blue", Toast.LENGTH_SHORT).show();
                    flContainer.setBackgroundColor(Color.parseColor("#5F9EA0"));
                    break;
                case 2:
                    Toast.makeText(context, "Dark Olive Green", Toast.LENGTH_SHORT).show();
                    flContainer.setBackgroundColor(Color.parseColor("#556B2F"));
                    break;

                case 3:
                    Toast.makeText(context, "Dark Orange", Toast.LENGTH_SHORT).show();
                    flContainer.setBackgroundColor(Color.parseColor("#FF8C00"));
                    break;
                case 4:
                    Toast.makeText(context, "Golden rod", Toast.LENGTH_SHORT).show();
                    flContainer.setBackgroundColor(Color.parseColor("#DAA520"));
                    break;
            }
            dlDrawer.closeDrawer(lvNavList);
        }
    }
}
