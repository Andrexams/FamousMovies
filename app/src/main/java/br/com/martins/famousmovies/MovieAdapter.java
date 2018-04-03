package br.com.martins.famousmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.martins.famousmovies.model.Movie;

/**
 * Created by Andre Martins dos Santos on 29/03/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> mListMovies;
    private MovieAdapterOnClickHandler mMovieAdapterOnClickHandler;

    public MovieAdapter(MovieAdapterOnClickHandler movieAdapterOnClickHandler){
        this.mMovieAdapterOnClickHandler = movieAdapterOnClickHandler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdItem, parent, shouldAttachToParentImmediately);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.mTextViewMovieName.setText(mListMovies.get(position).getOriginalTitle());
        //TODO Carregar o poster do filme
    }

    @Override
    public int getItemCount() {
        if(mListMovies != null){
            return mListMovies.size();
        }
        return 0;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextViewMovieName;
        public MovieViewHolder(View itemView) {
            super(itemView);
            mTextViewMovieName = (TextView) itemView.findViewById(R.id.tv_movie_name);
            mTextViewMovieName.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mMovieAdapterOnClickHandler.onClick(mListMovies.get(adapterPosition));
        }
    }

    public void setListMovies(List<Movie> listMovies) {
        mListMovies = listMovies;
        notifyDataSetChanged();
    }

    public interface MovieAdapterOnClickHandler{
        public void onClick(Movie movie);
    }
}
