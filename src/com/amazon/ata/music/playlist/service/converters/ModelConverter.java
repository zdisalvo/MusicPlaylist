package com.amazon.ata.music.playlist.service.converters;

import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;

public class ModelConverter {
    /**
     * Converts a provided {@link Playlist} into a {@link PlaylistModel} representation.
     * @param playlist the playlist to convert
     * @return the converted playlist
     */
    public PlaylistModel toPlaylistModel(Playlist playlist) {

        Integer songCount = 0;

        if (playlist.getSongCount() != null) {
            songCount = playlist.getSongCount();
        }

        return PlaylistModel.builder()
            .withId(playlist.getId())
                .withName(playlist.getName())
                .withCustomerId(playlist.getCustomerId())
                .withSongCount(songCount)
                .withTags(playlist.getTags())
            .build();
    }
}
