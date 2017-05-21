package david.com.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


/**
 * Created by David on 11-May-17.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static Context context;
    private static String base_url_popular;
    private static String base_url_top_rated;

    private static void initData() {
        String apiKey = getKey();
        base_url_popular = context.getString(R.string.base_url_popular) + apiKey;
        base_url_top_rated = context.getString(R.string.base_url_top_rated) + apiKey;
    }

    private static String getKey() {
        Scanner scanner = new Scanner(context.getResources().openRawResource(R.raw.api_key));
        String result = "";
        while (scanner.hasNext()){
            result = scanner.next();
        }
        Log.d(TAG, "in getKey(), result from scanner is: " + result);
        scanner.close();
        return result;
    }

    public static URL buildUrl(String sortType, Context context){
        NetworkUtils.context = context;
        initData();
        String sortParam = "";
        if(sortType.equals("mostPopular")){
            sortParam = base_url_popular;
        }else if(sortType.equals("highestRated")){
            sortParam = base_url_top_rated;
        }

        Uri theMovieDbUri = Uri.parse(sortParam).buildUpon().build();

        URL theMoveDbUrl = null;
        try {
            theMoveDbUrl = new URL(theMovieDbUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return theMoveDbUrl;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            }else{
                return  null;
            }
        }finally {
            urlConnection.disconnect();
        }
    }
}