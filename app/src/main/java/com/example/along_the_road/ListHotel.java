package com.example.along_the_road;

import android.graphics.drawable.Drawable;

public class ListHotel {

    private Drawable Hotelimage;
    private String Hotelname;
    private String CheckIn;
    private String CheckOut;
    private String Parking;

    public ListHotel(Drawable HotelImage, String Hotelname, String CheckIn, String CheckOut, String Parking) {
        this.Hotelimage = Hotelimage;
        this.Hotelname = Hotelname;
        this.CheckIn = CheckIn;
        this.CheckOut = CheckOut;
        this.Parking = Parking;
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

}
