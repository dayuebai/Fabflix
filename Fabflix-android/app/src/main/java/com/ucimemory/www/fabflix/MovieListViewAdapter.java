package com.ucimemory.www.fabflix;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private ArrayList<Movie> movies;

    public MovieListViewAdapter(ArrayList<Movie> movies, Context context) {
        super(context, R.layout.activity_search_row, movies);
        this.movies = movies;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.activity_search_row, parent, false);

        Movie movie = movies.get(position);

        TextView title = (TextView) view.findViewById(R.id.title);

        TextView year = (TextView)view.findViewById(R.id.year);
        TextView director = (TextView)view.findViewById(R.id.director);
        TextView genres = (TextView)view.findViewById(R.id.genres);
        TextView stars = (TextView)view.findViewById(R.id.stars);

        title.setText(movie.getTitle());
        year.setText(Integer.toString(movie.getYear()));
        director.setText(movie.getDirector());
        genres.setText(movie.getGenres());
        stars.setText(movie.getStars());

        return view;
    }
}
