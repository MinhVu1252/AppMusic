package com.example.demoprojectmusic.Model;

import java.util.List;

public class Podcast {
    public Podcast(String idUser, String name, List<String> songIds) {
        this.idUser = idUser;
        this.name = name;
        this.songIds = songIds;
    }

    private String idUser;
    private String name;
    private List<String> songIds;

    public Podcast() {

    }

    public String getIdUser() {
        return idUser;
    }

    public String getName() {
        return name;
    }

    public List<String> getSongIds() {
        return songIds;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSongIds(List<String> songIds) {
        this.songIds = songIds;
    }
}
