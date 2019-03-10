package com.example.admin.trip;

import android.graphics.Bitmap;
import android.media.Image;
import android.widget.ImageView;

public class SingerItem {
    String name;
    Bitmap back;

    public SingerItem(String name,Bitmap back){
        this.name=name;
        this.back=back;
    }
    public SingerItem(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getBack() {
        return back;
    }

    public void setBack(Bitmap back) {
        this.back = back;
    }
}
