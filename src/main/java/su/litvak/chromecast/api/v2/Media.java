/*
 * Copyright 2014 Vitaly Litvak (vitavaque@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package su.litvak.chromecast.api.v2;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Media streamed on ChromeCast device.
 *
 * @see <a href="https://developers.google.com/cast/docs/reference/receiver/cast.receiver.media.MediaInformation">
 *     https://developers.google.com/cast/docs/reference/receiver/cast.receiver.media.MediaInformation</a>
 */
public class Media {

    /**
     * <p>Stream type.</p>
     *
     * <p>Some receivers use upper-case (like Pandora), some use lower-case (like Google Audio),
     * duplicate elements to support both.</p>
     *
     * @see <a href="https://developers.google.com/cast/docs/reference/receiver/cast.receiver.media#.StreamType">
     *     https://developers.google.com/cast/docs/reference/receiver/cast.receiver.media#.StreamType</a>
     */
    public enum StreamType {
        BUFFERED, buffered,
        LIVE, live,
        NONE, none
    }

    @JsonProperty
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public final Map<String, Object> metadata;

    @JsonProperty("contentId")
    public final String url;

    @JsonProperty
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public final Double duration;

    @JsonProperty
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public final StreamType streamType;

    @JsonProperty
    public final String contentType;

    @JsonProperty
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public final Map<String, Object> customData;

    @JsonIgnore
    public final Map<String, Object> textTrackStyle;

    @JsonIgnore
    public final List<Track> tracks;

    public Media(String url, String contentType) {
        this(url, contentType, null, null);
    }

    public Media(String url, String contentType, Double duration, StreamType streamType) {
        this(url, contentType, duration, streamType, null, null, null, null);
    }

    public Media(@JsonProperty("contentId") String url,
                 @JsonProperty("contentType") String contentType,
                 @JsonProperty("duration") Double duration,
                 @JsonProperty("streamType") StreamType streamType,
                 @JsonProperty("customData") Map<String, Object> customData,
                 @JsonProperty("metadata") Map<String, Object> metadata,
                 @JsonProperty("textTrackStyle") Map<String, Object> textTrackStyle,
                 @JsonProperty("tracks") List<Track> tracks) {
        this.url = url;
        this.contentType = contentType;
        this.duration = duration;
        this.streamType = streamType;
        this.customData = customData == null ? null : Collections.unmodifiableMap(customData);
        this.metadata = metadata == null ? null : Collections.unmodifiableMap(metadata);
        this.textTrackStyle = textTrackStyle == null ? null : Collections.unmodifiableMap(textTrackStyle);
        this.tracks = tracks == null ? null : Collections.unmodifiableList(tracks);
    }

    @JsonIgnore
    public final MetaData getMetaData() {
        return new MetaData();
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(new Object[] {this.url, this.contentType, this.streamType, this.duration});
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Media)) {
            return false;
        }
        final Media that = (Media) obj;
        return this.url == null ? that.url == null : this.url.equals(that.url)
                && this.contentType == null ? that.contentType == null : this.contentType.equals(that.contentType)
                && this.streamType == null ? that.streamType == null : this.streamType.equals(that.streamType)
                && this.duration == null ? that.duration == null : this.duration.equals(that.duration);
    }

    @Override
    public final String toString() {
        return String.format("Media{url: %s, contentType: %s, duration: %s}",
                this.url, this.contentType, this.duration);
    }

    public enum MediaType {
        GENERIC(MetaData.GenericMetaData.class),
        MOVIE(MetaData.MovieMetaData.class),
        TV_SHOW(MetaData.TvShowMetaData.class),
        MUSIC_TRACK(MetaData.MusicTrackMetaData.class),
        PHOTO(MetaData.PhotoMetaData.class);

        private final Class<?> dataClass;

        MediaType(Class<?> dataClass) {
            this.dataClass = dataClass;
        }

        public Class<?> getDataClass() {
            return this.dataClass;
        }
    }

    /**
     * Values were taken from
     * https://developers.google.com/android/reference/com/google/android/gms/cast/MediaMetadata.html
     */
    public class MetaData {
        public static final String METADATA_TYPE = "metadataType";

        public static final String ALBUM_ARTIST = "albumArtist";
        public static final String ALBUM_NAME = "albumName";
        public static final String ALBUM_TITLE = "albumTitle";
        public static final String ARTIST = "artist";
        public static final String BROADCAST_DATE = "broadcastDate";
        public static final String COMPOSER = "composer";
        public static final String CREATION_DATE = "creationDate";
        public static final String DISC_NUMBER = "discNumber";
        public static final String EPISODE_NUMBER = "episodeNumber";
        public static final String HEIGHT = "height";
        public static final String IMAGES = "images";
        public static final String LOCATION_NAME = "locationName";
        public static final String LOCATION_LATITUDE = "locationLatitude";
        public static final String LOCATION_LONGITUDE = "locationLongitude";
        public static final String RELEASE_DATE = "releaseDate";
        public static final String SEASON_NUMBER = "seasonNumber";
        public static final String SERIES_TITLE = "seriesTitle";
        public static final String STUDIO = "studio";
        public static final String SUBTITLE = "subtitle";
        public static final String TITLE = "title";
        public static final String TRACK_NUMBER = "trackNumber";
        public static final String WIDTH = "width";

        public int getType() {
            return Integer.parseInt(metadata.get(METADATA_TYPE).toString());
        }

        public MediaType getMediaType() {
            return MediaType.values()[getType()];
        }

        public Object getMetaData() {
            switch (getMediaType()) {
                case GENERIC:
                    return new GenericMetaData();
                case MOVIE:
                    return new MovieMetaData();
                case TV_SHOW:
                    return new TvShowMetaData();
                case MUSIC_TRACK:
                    return new MusicTrackMetaData();
                case PHOTO:
                    return new PhotoMetaData();
                default:
                    throw new IllegalArgumentException("Unknown type: " + getMediaType());
            }
        }

        public String getString(String key) {
            return metadata.get(key).toString();
        }

        public Integer getInt(String key) {
            return (Integer) metadata.get(key);
        }

        public Double getDouble(String key) {
            return (Double) metadata.get(key);
        }

        public String[] getStringArray(String key) {
            List<String> returnValue = new ArrayList<String>();

            List<Map<String, String>> listOfMaps = (List<Map<String, String>>) metadata.get(key);
            for (Map<String, String> urlMap : listOfMaps) {
                returnValue.add(urlMap.get("url"));
            }

            return returnValue.toArray(new String[returnValue.size()]);
        }

        /**
         * This is by no means a complete list, nor are all keys guaranteed to be there. Tread carefully.
         *
         * @return the keys that are most likely available for this {@link MediaType}.
         */
        public String[] keys() {
            switch (getMediaType()) {
                case GENERIC:
                    return new String[]{RELEASE_DATE, TITLE, SUBTITLE, ARTIST, IMAGES};
                case MOVIE:
                    return new String[]{RELEASE_DATE, TITLE, SUBTITLE, STUDIO, IMAGES};
                case TV_SHOW:
                    return new String[]{RELEASE_DATE, BROADCAST_DATE, TITLE, SEASON_NUMBER, EPISODE_NUMBER, SERIES_TITLE, IMAGES};
                case MUSIC_TRACK:
                    return new String[]{RELEASE_DATE, TITLE, ARTIST, ALBUM_ARTIST, ALBUM_TITLE, ALBUM_NAME, COMPOSER, DISC_NUMBER, TRACK_NUMBER, IMAGES};
                case PHOTO:
                    return new String[]{CREATION_DATE, TITLE, ARTIST, WIDTH, HEIGHT, LOCATION_NAME, LOCATION_LATITUDE, LOCATION_LONGITUDE};
                default:
                    return new String[0];
            }
        }

        public class GenericMetaData {
            String releaseDate() {
                return getString(RELEASE_DATE);
            }

            String title() {
                return getString(TITLE);
            }

            String subtitle() {
                return getString(SUBTITLE);
            }

            String artist() {
                return getString(ARTIST);
            }

            String[] images() {
                return getStringArray(IMAGES);
            }
        }

        public class MovieMetaData {
            String releaseDate() {
                return getString(RELEASE_DATE);
            }

            String title() {
                return getString(TITLE);
            }

            String subtitle() {
                return getString(SUBTITLE);
            }

            String studio() {
                return getString(STUDIO);
            }

            String[] images() {
                return getStringArray(IMAGES);
            }
        }

        public class TvShowMetaData {
            String releaseDate() {
                return getString(RELEASE_DATE);
            }

            String broadcastDate() {
                return getString(BROADCAST_DATE);
            }

            String title() {
                return getString(TITLE);
            }

            Integer seasonNumber() {
                return getInt(SEASON_NUMBER);
            }

            Integer episodeNumber() {
                return getInt(EPISODE_NUMBER);
            }

            String seriesTitle() {
                return getString(SERIES_TITLE);
            }

            String[] images() {
                return getStringArray(IMAGES);
            }
        }

        public class MusicTrackMetaData {
            String releaseDate() {
                return getString(RELEASE_DATE);
            }

            String title() {
                return getString(TITLE);
            }

            String artist() {
                return getString(ARTIST);
            }

            String albumArtist() {
                return getString(ALBUM_ARTIST);
            }

            String albumTitle() {
                return getString(ALBUM_TITLE);
            }

            String composer() {
                return getString(COMPOSER);
            }

            Integer discNumber() {
                return getInt(DISC_NUMBER);
            }

            Integer trackNumber() {
                return getInt(TRACK_NUMBER);
            }

            String[] images() {
                return getStringArray(IMAGES);
            }
        }

        public class PhotoMetaData {
            String creationDate() {
                return getString(CREATION_DATE);
            }

            String title() {
                return getString(TITLE);
            }

            String artist() {
                return getString(ARTIST);
            }

            Integer width() {
                return getInt(WIDTH);
            }

            Integer height() {
                return getInt(HEIGHT);
            }

            String locationName() {
                return getString(LOCATION_NAME);
            }

            Double locationLatitude() {
                return getDouble(LOCATION_LATITUDE);
            }

            Double locationLongitude() {
                return getDouble(LOCATION_LONGITUDE);
            }
        }
    }
}
