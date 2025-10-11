package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Playlist {
    private int id;
    private String name;
    private List<Content> contents;

    public Playlist(String name) {
        this.id = generateUniqueId();
        this.name = name;
        this.contents = new ArrayList<>();
    }

    private int generateUniqueId() {
        return Math.abs(UUID.randomUUID().hashCode());
    }

    public void addContent(Content content) {
        if (!contents.contains(content)) {
            this.contents.add(content);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }
}