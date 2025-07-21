package com.microservice.song_service.dto;

import com.microservice.song_service.model.Song;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongDto {
    @NotNull(message = "Song id is required")
    private int id;
    @NotNull(message = "Song name is required")
    @Size(min = 1, max = 100, message = "Name files length should be between 1 and 100")
    private String name;
    @NotNull(message = "Song artist is required")
    @Size(min = 1, max = 100, message = "Artist files length should be between 1 and 100")
    private String artist;
    @NotNull(message = "Song album is required")
    @Size(min = 1, max = 100, message = "Album files length should be between 1 and 100")
    private String album;
    @NotNull(message = "Song duration is required")
    @Pattern(regexp = "^([0-5][0-9]):([0-5][0-9])$", message = "Duration must be in mm:ss format with leading zeros")
    private String duration;
    @NotNull(message = "Song year is required")
    @Pattern(regexp = "^(19|20)[0-9]{2}$", message = "Year must be between 1900 and 2099")
    private String year;

    public static Song createSong(SongDto songDto) {
        return new Song(songDto.getId(), songDto.getName(), songDto.getArtist(), songDto.getAlbum(), songDto.getDuration(), songDto.getYear());
    }

    public Song createSong() {
        return createSong(this);
    }

    public static SongDto toSongDto(Song song) {
        return new SongDto(song.getId(), song.getName(), song.getArtist(), song.getAlbum(), song.getDuration(), song.getYear());
    }
}
