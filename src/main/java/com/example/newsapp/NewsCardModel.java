package com.example.newsapp;

public class NewsCardModel {
    private String identification;
    private String image_resource;
    private String title;
    private String date;
    private String section;
    private String twitter_url;

    public NewsCardModel(String identification , String image_resource, String title, String date, String section, String twitter_url){
        this.identification = identification;
        this.image_resource = image_resource;
        this.title = title;
        this.date = date;
        this.section = section;
        this.twitter_url = twitter_url;
    }

    public String getIdentification(){ return identification;}
    public String getImage_resource(){ return image_resource; }
    public String getTitle(){
        return title;
    }
    public String getDate(){
        return date;
    }
    public String getSection(){ return section;}
    public String getTwitter_url(){return twitter_url;}
}
