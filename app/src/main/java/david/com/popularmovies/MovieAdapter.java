package david.com.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by David on 13-May-17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

//    private RecyclerView movieRecyclerView;
//    private ImageView imageView;
    private Context context;
    private int mNumItems;
    private static final String TAG = MovieAdapter.class.getSimpleName();
    private final ListItemClickListener onClickListener;

    private String[] mPosterPaths;

    public MovieAdapter(String[] posterPaths, int numItems, ListItemClickListener clickListener){
        mNumItems = numItems;
        mPosterPaths = posterPaths;
        onClickListener = clickListener;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //movieRecyclerView = (RecyclerView) parent.findViewById(R.id.rv_moviePosters);
        context = parent.getContext();
        int layoutIdForListItem = R.layout.thumbnail_layout;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        //Picasso.with(context).load("http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg").into(holder.mImageView);
        Log.d(TAG, mPosterPaths[position]);
        //GridLayout gridLayout = holder.gridLayout;
        int width = context.getResources().getDisplayMetrics().widthPixels;
        Picasso.with(context).load(mPosterPaths[position]).resize(width/2, 0).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mPosterPaths.length;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final ImageView mImageView;
        public final FrameLayout frameLayout;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.gv_item_view);
            mImageView = (ImageView) itemView.findViewById(R.id.item_imageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            onClickListener.onListItemClick(clickedPosition);
        }
    }

    void setMovieData(String[] posterPaths){
        mPosterPaths = posterPaths;
        notifyDataSetChanged();
    }

    public interface ListItemClickListener{
        void onListItemClick(int clickedItem);
    }
}
