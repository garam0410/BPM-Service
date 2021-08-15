package com.example.bpm_service.dto;

// 영화 DTO
public class MovieDto {

    private String title;
    private String image;
    private String subtitle;
    private String pubTitle;
    private String director;
    private String actor;

    public MovieDto(){}

    public MovieDto(String title, String subtitle, String image, String pubTitle, String director, String actor){
        this.title = title;
        this.subtitle = subtitle;
        this.image = image;
        this.pubTitle = pubTitle;
        this.director = director;
        this.actor = actor;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setSubtitle(String subtitle){
        this.subtitle = subtitle;
    }

    public String getSubtitle(){
        return subtitle;
    }

    public void setImage(String image){
        this.image = image;
    }

    public String getImage(){
        return image;
    }

    public void setPubTitle(String pubTitle){
        this.pubTitle = pubTitle;
    }

    public String getPubTitle(){
        return pubTitle;
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
