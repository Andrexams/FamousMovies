package br.com.martins.famousmovies;

/**
 * Created by Andre Martins dos Santos on 08/04/2018.
 */

public interface AsyncTaskDelegate {
    void onPreExecute();
    void onPostExecute(Object output);
    void onException(Exception e);
}
