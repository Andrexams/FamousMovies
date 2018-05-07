package br.com.martins.famousmovies;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import br.com.martins.famousmovies.model.Video;
import br.com.martins.famousmovies.utils.URLUtils;

/**
 * Created by Andre Martins dos Santos on 06/05/2018.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private static final String TAG = VideoAdapter.class.getSimpleName();
    private List<Video> mListVideo;
    private MovieAdapterOnClickHandler mMovieAdapterOnClickHandler;

    public VideoAdapter(MovieAdapterOnClickHandler movieAdapterOnClickHandler){
        this.mMovieAdapterOnClickHandler = movieAdapterOnClickHandler;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdItem = R.layout.video_item;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdItem, parent, shouldAttachToParentImmediately);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        Video video = mListVideo.get(position);
        try{
            holder.nameTextView.setText(video.getName());
        }catch (Exception e){
            Log.e(TAG,"Error or load image",e);
        }
    }


    @Override
    public int getItemCount() {
        if(mListVideo != null){
            return mListVideo.size();
        }
        return 0;
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView videoImageView;
        private TextView nameTextView;
        public VideoViewHolder(View itemView) {
            super(itemView);
            videoImageView = (ImageView) itemView.findViewById(R.id.iv_video_play);
            videoImageView.setOnClickListener(this);

            nameTextView = (TextView)itemView.findViewById(R.id.tv_video_name);
            nameTextView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mMovieAdapterOnClickHandler.onClick(mListVideo.get(adapterPosition));
        }
    }

    public void setListVideo(List<Video> listVideo) {
        mListVideo = listVideo;
        notifyDataSetChanged();
    }

    public interface MovieAdapterOnClickHandler{
        void onClick(Video video);
    }
}
