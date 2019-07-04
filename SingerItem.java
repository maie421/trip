package com.example.admin.trip;

import android.graphics.Bitmap;
import android.media.Image;
import android.widget.ImageView;

public class SingerItem {
    String name;
    Bitmap back;
    String personnel;

    public SingerItem(String name,Bitmap back,String personnel){
        this.name=name;
        this.back=back;
        this.personnel=personnel;
    }
    public SingerItem(String name,String personnel){
        this.name=name;
        this.personnel=personnel;
    }

    public String getPersonnel() {
        return personnel;
    }

    public void setPersonnel(String personnel) {
        this.personnel = personnel;
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
