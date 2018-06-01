package com.ucimemory.www.fabflix;

import android.app.ActionBar.LayoutParams;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

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
            connectToTomcat(query, "title", "1");

        }
    }


    public void connectToTomcat(String query, String attr, String pageNumber) {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final String queryString;
        final String q = query;
        final String a = attr;
        final String p = pageNumber;

        if (attr.equals("title")){
            queryString = "?id=&sort=rating&order=desc&title="
                    + q + "&genre=&year=&director=&star=&pageNumber=" + pageNumber + "&movieNumber=10";
        }
        else {
            queryString =  "?id=" + q + "&sort=rating&order=desc&title=&genre=&year=&director=&star=&pageNumber=" + pageNumber + "&movieNumber=10";
        }

        final StringRequest loginRequest = new StringRequest(Request.Method.GET, "https://18.217.63.24:8443/Fabflix-website/api/db" + queryString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        JsonArray httpResponse = new JsonParser().parse(response).getAsJsonArray();

                        if (httpResponse.size() > 0) {


                            final ArrayList<Movie> movies = new ArrayList<Movie>();
                            int totalFound = httpResponse.get(0).getAsJsonObject().get("totalFound").getAsInt();

                            for (JsonElement movie : httpResponse) {
                                String title = movie.getAsJsonObject().get("movieName").getAsString();
                                int year = movie.getAsJsonObject().get("movieYear").getAsInt();
                                String director = movie.getAsJsonObject().get("movieDirector").getAsString();
                                String listOfGenres = movie.getAsJsonObject().get("listofGenres").getAsString();
                                String listOfStars = movie.getAsJsonObject().get("listofStars").getAsString();
                                String id =  movie.getAsJsonObject().get("movieId").getAsString();

                                movies.add(new Movie(title, year, director, listOfGenres, listOfStars, id));
                            }
                            showPagination(totalFound, q, a, p);

                            MovieListViewAdapter adapter = new MovieListViewAdapter(movies, getApplicationContext());

                            ListView listView = (ListView)findViewById(R.id.list);
                            listView.setAdapter(adapter);

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Movie movie = movies.get(position);
                                    String mId = movie.getId();

                                    Intent goToIntent = new Intent(getApplicationContext(), SingleActivity.class);

                                    goToIntent.putExtra("id", mId);

                                    System.out.println("Search box input query: " + q);

                                    startActivity(goToIntent);
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
                    public void onErrorResponse(VolleyError e) {
                        Log.d("security.error", e.toString());
                    }
                }
        );

        queue.add(loginRequest);

    }

    private void showPagination(int size, String query, String attr, String pageNumber) {
        int counter = (int) Math.ceil(size * 1.0/ 10);
        final ArrayList<Button> buttons = new ArrayList<Button>();
        final String q = query;
        final String a = attr;

        System.out.println("The number of buttons: " + counter);
        int i = 0;
        LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.buttons);
        buttonLayout.removeAllViews();

        if (Integer.parseInt(pageNumber) > 1) {
            buttons.add(new Button(this));
            buttons.get(i).setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            buttons.get(i).setText("Previous");
            buttons.get(i).setGravity(Gravity.CENTER_HORIZONTAL);
            final int k = Integer.parseInt(pageNumber) - 1;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            buttonLayout.addView(buttons.get(i), layoutParams);

            buttons.get(i).setOnClickListener(new OnClickListener() {
                public void onClick(View v)
                {
                    connectToTomcat(q, a, Integer.toString(k));
                }
            });
            ++i;
        }

        int o = 0;
        for (; o < counter && o < 4; ++i, ++o) {
            buttons.add(new Button(this));
            buttons.get(i).setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            buttons.get(i).setText(Integer.toString(o + 1));
            buttons.get(i).setGravity(Gravity.CENTER_HORIZONTAL);

            if (o+1 == Integer.parseInt(pageNumber)) {
                buttons.get(i).setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            }

            final int k = o, x = i;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            buttonLayout.addView(buttons.get(i), layoutParams);

            buttons.get(i).setOnClickListener(new OnClickListener() {
                public void onClick(View v)
                {
                    connectToTomcat(q, a, Integer.toString(k + 1));
                    buttons.get(x).setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
                }
            });

        }

        if (counter > 4) {
            buttons.add(new Button(this));
            buttons.get(i).setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            buttons.get(i).setText(Integer.toString(counter));
            buttons.get(i).setGravity(Gravity.CENTER_HORIZONTAL);

            if (counter == Integer.parseInt(pageNumber)) {
                buttons.get(i).setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            buttonLayout.addView(buttons.get(i), layoutParams);

            final int c = counter, z = i;
            buttons.get(i).setOnClickListener(new OnClickListener() {
                public void onClick(View v)
                {
                    connectToTomcat(q, a, Integer.toString(c));
                    buttons.get(z).setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
                }
            });

            ++i;
        }
        if (Integer.parseInt(pageNumber) < counter) {
            buttons.add(new Button(this));
            buttons.get(i).setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            buttons.get(i).setText("Next");
            buttons.get(i).setGravity(Gravity.CENTER_HORIZONTAL);
            final int k = Integer.parseInt(pageNumber) + 1;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            buttonLayout.addView(buttons.get(i), layoutParams);

            buttons.get(i).setOnClickListener(new OnClickListener() {
                public void onClick(View v)
                {
                    connectToTomcat(q, a, Integer.toString(k));
                }
            });
        }

    }

}