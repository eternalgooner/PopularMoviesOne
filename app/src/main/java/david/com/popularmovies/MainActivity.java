package david.com.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int NUM_LIST_ITEMS = 20;
    private MovieAdapter mMovieAdapter;
    private RecyclerView mRecyclerView;
    private String[] posterPaths;
    private GridLayoutManager gridLayoutManager;
    private boolean showingMostPopular = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadMovieList("mostPopular");
    }

    private void showMovies(){
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_moviePosters);

        //GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mMovieAdapter = new MovieAdapter(posterPaths, NUM_LIST_ITEMS, this);
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    private void loadMovieList(String sortType) {
        URL myUrl = NetworkUtils.buildUrl(sortType);
        new TheMovieDbTask().execute(myUrl);
    }

    @Override
    public void onListItemClick(int clickedItem) {
        String toastMessage = "Item : " + clickedItem + " clicked";
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();

        switch (itemSelected){
            case R.id.menu_most_popular:
                //TODO finish this off - show most popular method
                if(!showingMostPopular) showMostPopular();
                break;
            case R.id.menu_highest_rated:
                //TODO show highest rated method
                if(showingMostPopular) showHighestRated();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHighestRated() {
        loadMovieList("highestRated");
    }

    private void showMostPopular() {
       loadMovieList("mostPopular");
    }

    public class TheMovieDbTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL requestPopularMoviesUrl = params[0];
            String theMovieDbResult = null;
            try {
                theMovieDbResult = NetworkUtils.getResponseFromHttpUrl(requestPopularMoviesUrl);
                return theMovieDbResult;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            //return theMovieDbResult;
        }

        @Override
        protected void onPostExecute(String theMovieDbSearchResults) {
            posterPaths = new String[20];
            if (theMovieDbSearchResults != null && !theMovieDbSearchResults.equals("")) {
                try {
                    JSONObject JSONString = new JSONObject(theMovieDbSearchResults);
                    JSONArray moviesArray = JSONString.getJSONArray("results");
                    String[] moviesResult = new String[moviesArray.length()];
                    int next = 0;
                    for(String movie : moviesResult){
                        JSONObject nextMovie = moviesArray.getJSONObject(next);
                        posterPaths[next] = nextMovie.getString("poster_path");
                        Log.d(TAG, posterPaths[next]);
                        posterPaths[next] = "https://image.tmdb.org/t/p/w185/" + posterPaths[next];
                        ++next;
                    }
                    //mMovieAdapter.setMovieData(posterPaths);
                    showMovies();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "empty data back from themoviedb api call");
            }
           // mMovieAdapter.setMovieData(posterPaths);
        }
    }
}
