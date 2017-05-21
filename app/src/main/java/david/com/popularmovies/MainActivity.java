package david.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView txtNoNetworkMessage;
    private static final int NUM_LIST_ITEMS = 20;
    private MovieAdapter mMovieAdapter;
    private RecyclerView mRecyclerView;
    private String[] posterPaths;
    private ArrayList<HashMap> movieList;
    private GridLayoutManager gridLayoutManager;
    private boolean showingMostPopular = true;
    private Bundle movieBundle = new Bundle();
    private Bundle bundleRecyclerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtNoNetworkMessage = (TextView) findViewById(R.id.message_no_network_connection);

        if(savedInstanceState != null){
            bundleRecyclerViewState = savedInstanceState;
            mRecyclerView = bundleRecyclerViewState.getParcelable("test save");
            mRecyclerView.getLayoutManager().onRestoreInstanceState(bundleRecyclerViewState);
        }

        if(isNetworkAvailable()){
            loadMovieList("mostPopular"); //TODO hide no network message when network comes back - BUG
        }else{
            txtNoNetworkMessage.setVisibility(View.VISIBLE);
        }
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return ((activeNetworkInfo != null) && (activeNetworkInfo.isConnected()));
    }


    //TODO implement onSaveStateInstace & onRestoreStateInstance
//    @Override
//    protected void onPause() {
//        super.onPause();
//        bundleRecyclerViewState = new Bundle();
//        Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
//        bundleRecyclerViewState.putParcelable("recyclerState", listState);
//    }
//
    @Override
    protected void onResume() {
        super.onResume();
        if(bundleRecyclerViewState != null){
            //bundleRecyclerViewState = bundleRecyclerViewState.getParcelable("recyclerState");
            mRecyclerView.getLayoutManager().onRestoreInstanceState(bundleRecyclerViewState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        bundleRecyclerViewState.putParcelable("recyclerState", listState);
        outState.putParcelable("test save", bundleRecyclerViewState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(bundleRecyclerViewState != null){
            bundleRecyclerViewState = bundleRecyclerViewState.getParcelable("recyclerState");
        }
    }

    private void showMovies(){
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_moviePosters);
        gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mMovieAdapter = new MovieAdapter(posterPaths, NUM_LIST_ITEMS, this);
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    private void loadMovieList(String sortType) {
        URL myUrl = NetworkUtils.buildUrl(sortType, getApplicationContext());
        new TheMovieDbTask().execute(myUrl);
    }

    @Override
    public void onListItemClick(int clickedItem) {
        String toastMessage = "Item : " + clickedItem + " clicked";
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
        movieBundle.putSerializable("selectedMovie", movieList.get(clickedItem));
        intent.putExtras(movieBundle);
        MainActivity.this.startActivity(intent);
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
                if(!showingMostPopular) showMostPopular();
                break;
            case R.id.menu_highest_rated:
                if(showingMostPopular) showHighestRated();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHighestRated() {
        showingMostPopular = false;
        loadMovieList("highestRated");
    }

    private void showMostPopular() {
        showingMostPopular = true;
        loadMovieList("mostPopular");
    }

    public class TheMovieDbTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            movieList = new ArrayList<>();
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
        }

        @Override
        protected void onPostExecute(String theMovieDbSearchResults) {
            posterPaths = new String[20];
            if (theMovieDbSearchResults != null && !theMovieDbSearchResults.equals("")) {
                Log.d(TAG, theMovieDbSearchResults);
                JSONObject jsonObject = JsonUtils.getJSONObject(theMovieDbSearchResults);
                JSONArray jsonMoviesArray = JsonUtils.getJSONArray(jsonObject, "results");
                String[] moviesResult = new String[jsonMoviesArray.length()];
                int next = 0;
                for(String movie : moviesResult){
                    JSONObject nextMovie = JsonUtils.getJSONObject(jsonMoviesArray, next);
                    posterPaths[next] = JsonUtils.getString(nextMovie, "poster_path");
                    Log.d(TAG, posterPaths[next]);
                    posterPaths[next] = "https://image.tmdb.org/t/p/w780/" + posterPaths[next];     //other poster sizes are w92, w154, w185, w342, w500, w780 or original
                    getAllMovieData(nextMovie);
                    ++next;
                }
                showMovies();
            } else {
                Log.d(TAG, "empty data back from themoviedb api call");
            }
        }

        private void getAllMovieData(JSONObject clickedMovie) {
            HashMap movieMap = new HashMap();
            movieMap.put("title", JsonUtils.getString(clickedMovie, "original_title"));
            movieMap.put("overview", JsonUtils.getString(clickedMovie, "overview"));
            movieMap.put("releaseDate", JsonUtils.getString(clickedMovie, "release_date"));
            movieMap.put("posterPath", JsonUtils.getString(clickedMovie, "poster_path"));
            movieMap.put("voteAverage", JsonUtils.getString(clickedMovie, "vote_average"));
            movieList.add(movieMap);
        }
    }
}
