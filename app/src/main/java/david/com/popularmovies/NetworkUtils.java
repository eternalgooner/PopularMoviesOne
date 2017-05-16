package david.com.popularmovies;

import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by David on 11-May-17.
 */

public class NetworkUtils {
    //static String base_url_popular = "https://api.themoviedb.org/3/movie/popular?api_key=" + getTheMovieDbKey();
    static String base_url_popular = "https://api.themoviedb.org/3/movie/popular?api_key=c3a5750ba7734dbde0f2c5fa9f91f790";

    private static String getTheMovieDbKey() {
        String key = "";
        try {
            key = new Scanner(new File("D:\\David\\Documents\\app keys\\themoviedbkey.txt")).next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return key;
        //File file = new File("D:\\David\\Documents\\app keys\\themoviedbkey.txt");
    }

    public static URL buildUrl(String sortType){
        Uri theMovieDbUri = Uri.parse(base_url_popular).buildUpon().build();

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
