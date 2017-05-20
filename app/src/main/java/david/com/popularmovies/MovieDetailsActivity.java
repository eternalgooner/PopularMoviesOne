package david.com.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();
    private TextView movieTitle;
    private ImageView moviePoster;
    private TextView userRating;
    private TextView releaseDate;
    protected TextView moveSummary;
    private HashMap movieSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        movieTitle = (TextView) findViewById(R.id.txtMovieTitle);
        userRating = (TextView) findViewById(R.id.txtMovieUserRating);
        releaseDate = (TextView) findViewById(R.id.txtMovieReleaseDate);
        moveSummary = (TextView) findViewById(R.id.txtMovieSummary);
        moviePoster = (ImageView) findViewById(R.id.imgMoviePoster);

        Bundle bundle = this.getIntent().getExtras();
        movieSelected = (HashMap) bundle.getSerializable("selectedMovie");

        displayMovieDetails(movieSelected);
    }

    private void displayMovieDetails(HashMap movie) {
        String posterPrefix = "http://image.tmdb.org/t/p/w780/";
        movieTitle.setText((String)movie.get("title"));
        moveSummary.setText((String)movie.get("overview"));
        userRating.setText((String)movie.get("voteAverage"));
        releaseDate.setText((String)movie.get("releaseDate"));
        Picasso.with(getApplicationContext()).load(posterPrefix + (String) movie.get("posterPath")).into(moviePoster);
        Log.d(TAG, "poster path is: " + movie.get("posterPath"));
    }
}
