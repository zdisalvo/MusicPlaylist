package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.converters.ModelConverter;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeValueException;
import com.amazon.ata.music.playlist.service.models.requests.CreatePlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.CreatePlaylistResult;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;

import com.amazon.ata.music.playlist.service.util.MusicPlaylistServiceUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Implementation of the CreatePlaylistActivity for the MusicPlaylistService's CreatePlaylist API.
 *
 * This API allows the customer to create a new playlist with no songs.
 */
public class CreatePlaylistActivity implements RequestHandler<CreatePlaylistRequest, CreatePlaylistResult> {
    private final Logger log = LogManager.getLogger();
    private final PlaylistDao playlistDao;

    /**
     * Instantiates a new CreatePlaylistActivity object.
     *
     * @param playlistDao PlaylistDao to access the playlists table.
     */
    public CreatePlaylistActivity(PlaylistDao playlistDao) {
        this.playlistDao = playlistDao;
    }

    /**
     * This method handles the incoming request by persisting a new playlist
     * with the provided playlist name and customer ID from the request.
     * <p>
     * It then returns the newly created playlist.
     * <p>
     * If the provided playlist name or customer ID has invalid characters, throws an
     * InvalidAttributeValueException
     *
     * @param createPlaylistRequest request object containing the playlist name and customer ID
     *                              associated with it
     * @return createPlaylistResult result object containing the API defined {@link PlaylistModel}
     */
    @Override
    public CreatePlaylistResult handleRequest(final CreatePlaylistRequest createPlaylistRequest, Context context) {
        log.info("Received CreatePlaylistRequest {}", createPlaylistRequest);
        Playlist playlist = new Playlist();

        //check if Playlist name is valid, set name if valid
        if (MusicPlaylistServiceUtils.isValidString(createPlaylistRequest.getName())) {
            playlist.setName(createPlaylistRequest.getName());
        } else {
            throw new InvalidAttributeValueException("The Playlist Name cannot contain \" ' or \\");
        }

        //check if customerId is valid, set customerId if valid
        if (MusicPlaylistServiceUtils.isValidString(createPlaylistRequest.getCustomerId())) {
            playlist.setCustomerId(createPlaylistRequest.getCustomerId());
        } else {
            throw new InvalidAttributeValueException("The Playlist Name cannot contain \" ' or \\");
        }

        //set new Playlist Id
        String playlistId = MusicPlaylistServiceUtils.generatePlaylistId();
        playlist.setId(playlistId);

        //set tags if present
        if (createPlaylistRequest.getTags() != null && createPlaylistRequest.getTags().size() > 0) {
            playlist.setTags(createPlaylistRequest.getTags());
        } else {
            playlist.setTags(new HashSet<>());
        }

        playlist.setSongList(new ArrayList<>());


        PlaylistModel playlistModel = new ModelConverter().toPlaylistModel(playlist);

        playlistDao.savePlaylist(playlist);

        return CreatePlaylistResult.builder()
                .withPlaylist(playlistModel)
                .build();
    }
}
