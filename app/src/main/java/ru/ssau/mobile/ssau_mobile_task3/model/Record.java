package ru.ssau.mobile.ssau_mobile_task3.model;

import java.io.Serializable;
import java.util.ArrayList;

import ru.ssau.mobile.ssau_mobile_task3.db.RecordOperations;

/**
 * Created by Pavel on 14.12.2016.
 */

public class Record implements Serializable {
    private Category category;
    private ArrayList<Photo> photos;
    private long start, end, minutes, id;
    private String summary;

    public Record() {};

    public Record(long id, Category category, long start, long end, long minutes,
                  String summary, ArrayList<Photo> photos) {
        this.category = category;
        this.photos = photos;
        this.start = start;
        this.end = end;
        this.minutes = minutes;
        this.id = id;
        this.summary = summary;
    }

    public void copyFrom(Record rec) {
        this.category = rec.category;
        if (rec.photos != null)
            this.photos = (ArrayList<Photo>) rec.photos.clone();
        else
            this.photos = null;
        this.start = rec.start;
        this.end = rec.end;
        this.minutes = rec.minutes;
        this.id = rec.id;
        this.summary = (rec.summary == null ? null : rec.summary.toString());
    }

    public RecordSendable toSendable() {
        RecordSendable sendable = new RecordSendable();
        sendable.setId(id);
        sendable.setCategory(category);
        sendable.setStart(start);
        sendable.setEnd(end);
        sendable.setMinutes(minutes);
        sendable.setSummary(summary);
        sendable.setPhotos(RecordOperations.getPhotoString(this));
        return sendable;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
