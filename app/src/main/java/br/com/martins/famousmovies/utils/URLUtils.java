package br.com.martins.famousmovies.utils;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Andre Martins dos Santos on 06/05/2018.
 */
public class URLUtils {

    private static final String TAG = URLUtils.class.getSimpleName();

    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com";
    private static final String YOUTUBE_VIDEO_PATH = "watch";
    private static final String YOUTUBE_VIDEO_PARAM = "v";

    //https://www.youtube.com/watch?v=H74COj0UQ_Q
    public static URL buildYoutubeVideoUrl(String videoKey) {
        Uri builtUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendPath(YOUTUBE_VIDEO_PATH)
                .appendQueryParameter(YOUTUBE_VIDEO_PARAM, videoKey)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Built URI " + url);
        return url;
    }

    public static Uri buildYoutubeVideoUri(String videoKey) {
        Uri builtUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendPath(YOUTUBE_VIDEO_PATH)
                .appendQueryParameter(YOUTUBE_VIDEO_PARAM, videoKey)
                .build();
        Log.d(TAG, "Built URI " + builtUri);
        return builtUri;
    }
}
