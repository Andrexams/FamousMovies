package br.com.martins.famousmovies.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Andre Martins dos Santos on 02/04/2018.
 */

public class Movie implements Serializable{

    private Long id;
    private String originalTitle;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private Date releaseDate;
    private Double voteAverage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Movie){
            if(((Movie)obj).getId().equals(this.id)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.intValue() * Integer.MIN_VALUE;
    }
}
