package com.example.tripreminder2021.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class TripModel implements Parcelable,Serializable{


    protected TripModel(Parcel in) {
        startloc = in.readString();
        endloc = in.readString();
        date = in.readString();
        time = in.readString();
        status = in.readString();
        tripname = in.readString();
        dateTime = in.readString();
        trip_id = in.readString();
        include_in = in.readString();
        notes = in.createStringArrayList();
    }

    public static final Creator<TripModel> CREATOR = new Creator<TripModel>() {
        @Override
        public TripModel createFromParcel(Parcel in) {
            return new TripModel(in);
        }

        @Override
        public TripModel[] newArray(int size) {
            return new TripModel[size];
        }
    };

    public String getInclude_in() {
        return include_in;
    }

    public void setInclude_in(String include_in) {
        this.include_in = include_in;
    }

    public String startloc,endloc,date,time,status,tripname,dateTime;

    private String trip_id;
    private String include_in;
    private List<String> notes = new ArrayList<>();

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }



    public String  getDateTime() {
        return dateTime;
    }

    public void setDateTime(String  dateTime) {
        this.dateTime = dateTime;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public TripModel(String trip_id,String startloc, String endloc, String date,
                     String time, String tripname, String status,
                     List<String> notes, String dateTime,String include_in) {

        this.trip_id=trip_id;
        this.startloc = startloc;
        this.endloc = endloc;
        this.date = date;
        this.time = time;
        this.status = status;
        this.tripname = tripname;
        this.status = status;
        this.notes = notes;
        this.dateTime = dateTime;
        this.include_in = include_in;
    }
    public TripModel(String startloc, String endloc, String date, String time, String tripname, String status, List<String> notes) {
        this.startloc = startloc;
        this.endloc = endloc;
        this.date = date;
        this.time = time;
        this.status = status;
        this.tripname = tripname;
        this.status = status;
        this.notes = notes;

    }
    public TripModel(String startloc, String endloc, String date, String time, String tripname, String status) {
        this.startloc = startloc;
        this.endloc = endloc;
        this.date = date;
        this.time = time;
        this.status = status;
        this.tripname = tripname;
        this.status = status;
        this.notes = null;
    }

    public TripModel() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartloc() {
        return startloc;
    }

    public void setStartloc(String startloc) {
        this.startloc = startloc;
    }

    public String getEndloc() {
        return endloc;
    }

    public void setEndloc(String endloc) {
        this.endloc = endloc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getTripname() {
        return tripname;
    }

    public void setTripname(String tripname) {
        this.tripname = tripname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(startloc);
        dest.writeString(endloc);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(status);
        dest.writeString(tripname);
        dest.writeString(dateTime);
        dest.writeString(trip_id);
        dest.writeString(include_in);
        dest.writeStringList(notes);
    }

}

