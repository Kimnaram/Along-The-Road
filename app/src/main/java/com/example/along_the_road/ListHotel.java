package com.example.along_the_road;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

public class ListHotel {

    private int contentID;
    private @Nullable Drawable Hotelimage;
    private String Hotelname;
    private String CheckIn;
    private String CheckOut;
    private String Parking;
    private int listNumber;

    public ListHotel(int contentID, Drawable Hotelimage, String Hotelname,
                     String CheckIn, String CheckOut, String Parking, int listNumber) {
        this.contentID = contentID;
        this.Hotelimage = Hotelimage;
        this.Hotelname = Hotelname;
        this.CheckIn = CheckIn;
        this.CheckOut = CheckOut;
        this.Parking = Parking;
        this.listNumber = listNumber;
    }

    public ListHotel(int contentID, String Hotelname, String CheckIn,
                     String CheckOut, String Parking, int listNumber) {
        this.contentID = contentID;
        this.Hotelname = Hotelname;
        this.CheckIn = CheckIn;
        this.CheckOut = CheckOut;
        this.Parking = Parking;
        this.listNumber = listNumber;
    }

    public int getContentID() {
        return contentID;
    }

    public void setContentID(int contentID) {
        this.contentID = contentID;
    }

    public Drawable getHotelimage() {
        return Hotelimage;
    }

    public void setHotelimage(Drawable hotelimage) {
        Hotelimage = hotelimage;
    }

    public String getHotelname() {
        return Hotelname;
    }

    public void setHotelname(String hotelname) {
        Hotelname = hotelname;
    }

    public String getCheckIn() {
        return CheckIn;
    }

    public void setCheckIn(String checkIn) {
        CheckIn = checkIn;
    }

    public String getCheckOut() {
        return CheckOut;
    }

    public void setCheckOut(String checkOut) {
        CheckOut = checkOut;
    }

    public String getParking() {
        return Parking;
    }

    public void setParking(String parking) {
        Parking = parking;
    }

    public int getListNumber() {
        return listNumber;
    }

    public void setListNumber(int listNumber) {
        this.listNumber = listNumber;
    }

}
