package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.dynamodb.AlbumTrackDao;
import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AlbumTrackDaoTest {


    @Mock
    private AlbumTrackDao albumTrackDao;

    private AlbumTrack albumTrack;

    @BeforeEach

    public void setUp() {
        initMocks(this);
        albumTrack = new AlbumTrack();

        albumTrack.setAsin("1234");

    }


    @Test
    public void getAlbumTrack_returnsCorrectAlbumTrack() {

        //GIVEN
        String asin = "1234";

        AlbumTrack expected = new AlbumTrack();
        expected.setAsin(asin);

        //WHEN

        when(albumTrackDao.getAlbumTrack(asin)).thenReturn(expected);

        AlbumTrack result = albumTrackDao.getAlbumTrack(asin);

        //THEN

        Assertions.assertEquals(result, expected, "Expected album tracks to be equal");

    }
}
