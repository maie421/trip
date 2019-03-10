package com.example.admin.trip;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SingerItemView extends LinearLayout {
    TextView name;
    ImageView back;

    public SingerItemView(Context context){
        super(context);
        init(context);
    }
    public SingerItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.main_list_view, this,true);
        back=(ImageView)findViewById(R.id.back);
        name=(TextView)findViewById(R.id.name);
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setBack(Bitmap back) {
        this.back.setImageBitmap(back);
    }
}
