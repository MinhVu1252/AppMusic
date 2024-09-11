package com.example.demoprojectmusic.Model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
public class Album {

    //N
    @SerializedName("id")
    private long id;

    @SerializedName("title")
    private String title;

    @SerializedName("cover_xl")
    private String cover_xl;

    @SerializedName("genres")
    private GenreData genreData;

    public void setGenres(GenreData genreData) {
        this.genreData = genreData;
    }

    public GenreData getGenreData() {
        return genreData;
    }

    @SerializedName("label")
    private String label;

    @SerializedName("nb_tracks")
    private int nb_tracks;

    @SerializedName("release_date")
    private String release_date;

    @SerializedName("contributors")
    private List<Contributor> contributors;

    @SerializedName("artist")
    private Artist artist;

    @SerializedName("type")
    private String type;

    @SerializedName("tracks")
    private TrackData trackData;

    public TrackData getTrackData() {
        return trackData;
    }

    public void setTrackData(TrackData trackData) {
        this.trackData = trackData;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Album {");
        stringBuilder.append("id=").append(id);
        stringBuilder.append(", title='").append(title).append('\'');
        stringBuilder.append(", cover_xl='").append(cover_xl).append('\'');
        stringBuilder.append(", label='").append(label).append('\'');
        stringBuilder.append(", nb_tracks=").append(nb_tracks);
        stringBuilder.append(", release_date='").append(release_date).append('\'');

        if (artist != null) {
            stringBuilder.append(", artist=").append(artist.toString());
        } else {
            stringBuilder.append(", artist=null");
        }

        if (contributors != null) {
            stringBuilder.append(", contributors=").append(contributors.toString());
        }

        if (genreData != null && genreData.getData() != null) {
            stringBuilder.append(", genreData=").append(genreData.getData().toString());
        }

        if (trackData != null && trackData.getData() != null) {
            stringBuilder.append(", trackData=").append(trackData.getData().toString());
        }


        if (type != null) {
            stringBuilder.append(", type='").append(type).append('\'');
        }

        stringBuilder.append('}');
        return stringBuilder.toString();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover_xl() {
        return cover_xl;
    }

    public void setCover_xl(String cover_xl) {
        this.cover_xl = cover_xl;
    }


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getNb_tracks() {
        return nb_tracks;
    }

    public void setNb_tracks(int nb_tracks) {
        this.nb_tracks = nb_tracks;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
