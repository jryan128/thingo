package com.joysignalgames.bazingo.internal.server.genre.services;

import org.mapdb.Atomic;
import org.mapdb.Bind;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;
import org.mapdb.HTreeMap;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;

@Singleton
public class GenreService {

    private static final Long MAX_GENRES = 100L;
    private final DB db;
    private final HTreeMap<Long, Genre> genres;
    private final NavigableSet<Fun.Tuple2<String, Long>> userToGenreIdTuples;
    private final Atomic.Long nextId;
    
    public GenreService() {
        db = DBMaker.newFileDB(new File("db"))
                .closeOnJvmShutdown()
                .make();
        genres = db.getHashMap("genres");
        nextId = db.getAtomicLong("nextId");
        userToGenreIdTuples = db.getTreeSet("users");
        Bind.secondaryKey(genres, userToGenreIdTuples, (aLong, genre) -> genre.user);
    }

    public static void main(String[] args) throws IOException {
        GenreService gs = new GenreService();
        gs.genres.put(1L, new Genre("user1", new String(Files.readAllBytes(Paths.get("../genres/Romantic Comedy.tsv")), StandardCharsets.UTF_8)));
        gs.genres.put(2L, new Genre("user1", new String(Files.readAllBytes(Paths.get("../genres/SciFi.tsv")), StandardCharsets.UTF_8)));
        gs.genres.put(3L, new Genre("user1", new String(Files.readAllBytes(Paths.get("../genres/SciFi.tsv")), StandardCharsets.UTF_8)));

        gs.genres.put(Long.parseLong("B", 36), new Genre("user2", new String(Files.readAllBytes(Paths.get("../genres/Horror.tsv")), StandardCharsets.UTF_8)));
        gs.genres.put(Long.parseLong("BE", 36), new Genre("user2", new String(Files.readAllBytes(Paths.get("../genres/Star Trek - Voyager.tsv")), StandardCharsets.UTF_8)));
        gs.db.commit();
    }

    public List<String> getListOfGenresForUser(String user) {
        // TODO: Convert to streams.
        List<String> ids = new ArrayList<>();
        for (Long id : Fun.filter(userToGenreIdTuples, user)) {
            ids.add(convertLongTo36BaseString(id));
        }
        return ids;
    }

    public String createGenre(String user, String data) {
        long id = nextId.getAndIncrement();
        genres.put(id, new Genre(user, data));
        db.commit();
        return convertLongTo36BaseString(id);
    }

    private String convertLongTo36BaseString(Long i) {
        return Long.toString(i, 36);
    }

    public void removeGenre(String user, String id) {
        Long i = convertToBase36Long(id);
        genres.remove(i);
        db.commit();
    }

    public String getGenre(String id) {
        Long i = convertToBase36Long(id);
        return genres.get(i).tsv;
    }

    public void updateGenre(String user, String id, String data) {
        Long i = convertToBase36Long(id);
        genres.put(i, new Genre(user, data));
        db.commit();
    }

    private long convertToBase36Long(String id) {
        return Long.parseLong(id, 36);
    }

    private static class Genre implements Serializable {
        public final String user;
        public final String tsv;

        public Genre(String user, String tsv) {
            this.user = user;
            this.tsv = tsv;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Genre genre = (Genre) o;

            if (tsv != null ? !tsv.equals(genre.tsv) : genre.tsv != null) return false;
            if (user != null ? !user.equals(genre.user) : genre.user != null) return false;

            return true;
        }
    }
}

