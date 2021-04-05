package com.example.android.keepnotes;

import android.graphics.Bitmap;
import android.net.Uri;

import java.net.URI;

public class DataModel {
    private String titlename, notecontact;
    Uri img;
    Uri img2;

    public DataModel(String titlename, String notecontact, Uri img,Uri img2) {
        this.titlename = titlename;
        this.notecontact = notecontact;
        this.img = img;
        this.img2 = img2;
    }

    public String getTitlename() {
        return titlename;
    }

    public void setTitlename(String titlename) {
        this.titlename = titlename;
    }

    public String getNotecontact() {
        return notecontact;
    }

    public void setNotecontact(String notecontact) {
        this.notecontact = notecontact;
    }

    public Uri getImg() {
        return img;
    }

    public void setImg(Uri img) {
        this.img = img;
    }

    public Uri getImg2() {
        return img2;
    }

    public void setImg2(Uri img) {
        this.img2 = img2;
    }
}
