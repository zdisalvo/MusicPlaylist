package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.converters.ModelConverter;
import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.models.requests.AddSongToPlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.AddSongToPlaylistResult;
import com.amazon.ata.music.playlist.service.models.SongModel;
import com.amazon.ata.music.playlist.service.dynamodb.AlbumTrackDao;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of the AddSongToPlaylistActivity for the MusicPlaylistService's AddSongToPlaylist API.
 *
 * This API allows the customer to add a song to their existing playlist.
 */
public class AddSongToPlaylistActivity implements RequestHandler<AddSongToPlaylistRequest, AddSongToPlaylistResult> {
    private final Logger log = LogManager.getLogger();
    private final PlaylistDao playlistDao;
    private final AlbumTrackDao albumTrackDao;

    /**
     * Instantiates a new AddSongToPlaylistActivity object.
     *
     * @param playlistDao PlaylistDao to access the playlist table.
     * @param albumTrackDao AlbumTrackDao to access the album_track table.
     */
    @Inject
    public AddSongToPlaylistActivity(PlaylistDao playlistDao, AlbumTrackDao albumTrackDao) {
        this.playlistDao = playlistDao;
        this.albumTrackDao = albumTrackDao;
    }

    /**
     * This method handles the incoming request by adding an additional song
     * to a playlist and persisting the updated playlist.
     * <p>
     * It then returns the updated song list of the playlist.
     * <p>
     * If the playlist does not exist, this should throw a PlaylistNotFoundException.
     * <p>
     * If the album track does not exist, this should throw an AlbumTrackNotFoundException.
     *
     * @param addSongToPlaylistRequest request object containing the playlist ID and an asin and track number
     *                                 to retrieve the song data
     * @return addSongToPlaylistResult result object containing the playlist's updated list of
     *                                 API defined {@link SongModel}s
     */
    @Override
    public AddSongToPlaylistResult handleRequest(final AddSongToPlaylistRequest addSongToPlaylistRequest, Context context) {
        log.info("Received AddSongToPlaylistRequest {} ", addSongToPlaylistRequest);
        String requestedAsin = addSongToPlaylistRequest.getAsin();
        Integer trackNumber = addSongToPlaylistRequest.getTrackNumber();
        AlbumTrack albumTrack = albumTrackDao.getAlbumTrack(requestedAsin, trackNumber);
        SongModel songModel = new ModelConverter().toSongModel(albumTrack);

        String playlistId = addSongToPlaylistRequest.getId();

        Playlist playlist = playlistDao.getPlaylist(playlistId);
        LinkedList<AlbumTrack> songLinkedList = new LinkedList<>();
        if (playlist.getSongList() == null) {
            songLinkedList.addLast(albumTrack);
            playlist.setSongList(songLinkedList);
        } else {
            List<AlbumTrack> songList = playlist.getSongList();
            for (AlbumTrack track : songList) {
                songLinkedList.addLast(track);
            }
            songLinkedList.addLast(albumTrack);
            playlist.setSongList(songLinkedList);
        }

        //TODO not needed?
        //playlist.setSongCount(playlist.getSongCount() + 1);

        //PlaylistModel playlistModel = new ModelConverter().toPlaylistModel(playlist);
        //SongModel songModel = new ModelConverter().toSongModel(albumTrack);

        playlistDao.savePlaylist(playlist);

        //TODO - Building LinkedList of SongModels
        LinkedList<SongModel> songModelLinkedList = new LinkedList<>();
        for (AlbumTrack track : songLinkedList) {
            SongModel songModelTrack = new ModelConverter().toSongModel(track);
            songModelLinkedList.addLast(songModelTrack);
        }


        return AddSongToPlaylistResult.builder()
                .withSongList(songModelLinkedList)
                .build();
    }
}
