package com.ucimemory.www.fabflix;

import java.util.ArrayList;

public class Movie {
    private String title;
    private int year;
    private String director;
    private String genres;
    private String stars;
    private String id;

    public Movie(String title, int year, String director, String genres, String stars, String id) {
        this.title = title;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public int getYear() {
        return this.year;
    }

    public String getDirector() {
        return this.director;
    }

    public String getGenres() {
        return this.genres;
    }

    public String getStars() {
        return this.stars;
    }

    public String getId() {
        return this.id;
    }


}
