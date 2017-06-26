package com.hot.sentefinder.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Jamie on 4/25/2017.
 */

@DatabaseTable(tableName = FeedBack.TABLE_NAME_FEEDBACK)
public class FeedBack {
    public static final String TABLE_NAME_FEEDBACK = "feedback";

    @DatabaseField(generatedId = true)
    private long id;

    @Expose
    @DatabaseField
    @SerializedName("fspId")
    private long fspId;

    @Expose
    @DatabaseField
    @SerializedName("rating")
    private int rating;

    @Expose
    @DatabaseField
    @SerializedName("description")
    private String description;


    public FeedBack(){}

    public FeedBack(int rating, String description) {
        this.rating = rating;
        this.description = description;
    }

    public FeedBack(long fspId, int rating, String description) {
        this.fspId = fspId;
        this.rating = rating;
        this.description = description;
    }

    public FeedBack(long id, long fspId, int rating, String description) {
        this.id = id;
        this.fspId = fspId;
        this.rating = rating;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFspId() {
        return fspId;
    }

    public void setFspId(long fspId) {
        this.fspId = fspId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id: ").append(id);
        sb.append(", fspId: ").append(fspId);
        sb.append(", rating: ").append(rating);
        sb.append(", description: ").append(description);

        return super.toString();
    }
}
