package com.amazon.ata.music.playlist.service.dynamodb.models;

import com.amazon.ata.music.playlist.service.converters.AlbumTrackLinkedListConverter;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a record in the playlists table.
 */
@DynamoDBTable(tableName = "playlists")
public class Playlist {
    private String id;
    private String name;
    private String customerId;
    private Integer songCount;
    private Set<String> tags = new HashSet<>();
    private List<AlbumTrack> songList;

    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    // PARTICIPANTS: You do not need to modify the songList getters/setters or annotations
    @DynamoDBTypeConverted(converter = AlbumTrackLinkedListConverter.class)
    @DynamoDBAttribute(attributeName = "songList")
    public List<AlbumTrack> getSongList() {
        return songList;
    }

    public void setSongList(List<AlbumTrack> songList) {
        this.songList = songList;
    }

    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName = "customerId")
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.N)
    @DynamoDBAttribute(attributeName = "songCount")
    public Integer getSongCount() {
        return songCount;
    }

    public void setSongCount(Integer songCount) {
        this.songCount = songCount;
    }

    @DynamoDBAttribute(attributeName = "tags")
    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
