package com.ucimemory.www.fabflix;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

public class SearchActivity extends AppCompatActivity {
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            System.out.println(query);
            connectToTomcat(query);

        }
    }

    private void connectToTomcat(String query) {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final String queryString = "?id=&sort=rating&order=desc&title="
                + query + "&genre=&year=&director=&star=&pageNumber=1&movieNumber=10";

        final StringRequest loginRequest = new StringRequest(Request.Method.GET, "https://10.0.2.2:8443/Fabflix-website/api/db" + queryString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        JsonArray httpResponse = new JsonParser().parse(response).getAsJsonArray();

                        if (httpResponse.size() > 0) {
                            ArrayList<Movie> movies = new ArrayList<Movie>();

                            for (JsonElement movie : httpResponse) {
                                String title = movie.getAsJsonObject().get("movieName").getAsString();
                                int year = movie.getAsJsonObject().get("movieYear").getAsInt();
                                String director = movie.getAsJsonObject().get("movieDirector").getAsString();
                                String listOfGenres = movie.getAsJsonObject().get("listofGenres").getAsString();
                                String listOfStars = movie.getAsJsonObject().get("listofStars").getAsString();
                                String id =  movie.getAsJsonObject().get("movieId").getAsString();

                                movies.add(new Movie(title, year, director, listOfGenres, listOfStars, id));
                            }

                            MovieListViewAdapter adapter = new MovieListViewAdapter(movies, getApplicationContext());

                            ListView listView = (ListView)findViewById(R.id.list);
                            listView.setAdapter(adapter);

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                }
                            });

                        }
                        else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Movie not found.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("security.error", error.toString());
                    }
                }
        );

        queue.add(loginRequest);

    }

}