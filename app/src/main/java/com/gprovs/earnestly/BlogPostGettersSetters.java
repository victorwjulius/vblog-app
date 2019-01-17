package com.gprovs.earnestly;

import java.sql.Timestamp;
import java.util.Date;

public class BlogPostGettersSetters extends BlogPostId{

    public String user_id,title,description;// had ,image_url,thumb_url,
    public Date timestamp;

    public BlogPostGettersSetters(String user_id, String title, String description, Date timestamp) {
        this.user_id = user_id;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }

    public BlogPostGettersSetters() {

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    //    public BlogPostGettersSetters(String user_id,String title, String description, Date timestamp) {//had ( String image_url, String thumb_url)
//        this.user_id = user_id;
//        this.image_url = image_url;
//        this.thumb_url = thumb_url;
//        this.title = title;
//        this.description = description;
//        this.timestamp = timestamp;
//    }
//
//    public BlogPostGettersSetters() {
//    }
//
//    public String getUser_id() {
//        return user_id;
//    }
//
//    public void setUser_id(String user_id) {
//        this.user_id = user_id;
//    }

//    public String getImage_url() {
//        return image_url;
//    }
//
//    public void setImage_url(String image_url) {
//        this.image_url = image_url;
//    }
//
//    public String getThumb_url() {
//        return thumb_url;
//    }
//
//    public void setThumb_url(String thumb_url) {
//        this.thumb_url = thumb_url;
//    }

//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public Date getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(Date timestamp) {
//        this.timestamp = timestamp;
//    }
}
