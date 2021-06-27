package com.example.bpm_service.search;

public class SearchListItem {
    private String image;
    private String title;
    private String director;
    private String actor;

    public void setImage(String image){
        this.image = image;
    }

    public String getImage(){
        return image;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setDirector(String director){
        this.director = director;
    }

    public String getDirector(){
        return director;
    }

    public void setActor(String actor){
        this.actor = actor;
    }

    public String getActor(){
        return actor;
    }
}
