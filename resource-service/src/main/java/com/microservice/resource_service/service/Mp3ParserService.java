package com.microservice.resource_service.service;

import com.microservice.resource_service.dto.SongDto;
import com.microservice.resource_service.excpetion.BadRequestException;
import com.microservice.resource_service.excpetion.InternalServerErrorException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class Mp3ParserService {
    private final Mp3Parser mp3Parser;

    public Mp3ParserService() {
        this.mp3Parser = new Mp3Parser();
    }

    public SongDto readSongMetadata(byte[] bytes) {
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();

        try {
            mp3Parser.parse(new ByteArrayInputStream(bytes), handler, metadata, context);

            return SongDto.builder()
                    .name(metadata.get("dc:title"))
                    .artist(metadata.get("xmpDM:artist"))
                    .album(metadata.get("xmpDM:album"))
                    .duration(formatDuration(Double.parseDouble(metadata.get("xmpDM:duration"))))
                    .year(metadata.get("xmpDM:releaseDate"))
                    .build();
        } catch (IOException e) {
            throw new InternalServerErrorException("Error while parsing mp3 file");
        } catch (SAXException | TikaException e) {
            throw new BadRequestException("The request body is invalid MP3");
        }
    }

    public String formatDuration(double totalSeconds) {
        int roundedSeconds = (int) Math.round(totalSeconds);

        int minutes = roundedSeconds / 60;
        int seconds = roundedSeconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }
}
