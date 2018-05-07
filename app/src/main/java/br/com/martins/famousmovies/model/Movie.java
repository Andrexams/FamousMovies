package br.com.martins.famousmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Andre Martins dos Santos on 02/04/2018.
 */

public class Movie implements Parcelable{

    private Long id;
    private String originalTitle;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private Date releaseDate;
    private Double voteAverage;
    private Boolean favorite;

    public Movie(){
        favorite = Boolean.FALSE;
    }

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

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
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

    @Override
    public int describeContents() {
        return 0;
    }

    private Movie(Parcel from){
        id = from.readLong();
        originalTitle = from.readString();
        posterPath = from.readString();
        backdropPath = from.readString();
        overview = from.readString();
        voteAverage = from.readDouble();
        releaseDate = (Date)from.readSerializable();
        favorite = from.readByte() != 0;
    }

    public static final Parcelable.Creator<Movie>
            CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(originalTitle);
        parcel.writeString(posterPath);
        parcel.writeString(backdropPath);
        parcel.writeString(overview);
        parcel.writeDouble(voteAverage);
        parcel.writeSerializable(releaseDate);
        parcel.writeByte((byte) (favorite ? 1 : 0));
    }
}
