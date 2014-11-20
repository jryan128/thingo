package com.joysignalgames.bazingo.internal.server.genre.services;

import com.joysignalgames.bazingo.internal.server.genre.GenreRestServer;
import org.mapdb.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;

public class GenreService {
    private final DB db;
    private final HTreeMap<Long, Genre> genres;
    private final NavigableSet<Fun.Tuple2<String, Long>> userToGenreIdTuples;
    private final Atomic.Long nextId;

    public GenreService() {
        db = DBMaker.newFileDB(getDbLocation()).closeOnJvmShutdown().make();
        genres = db.getHashMap("genres");
        nextId = db.getAtomicLong("nextId");
        userToGenreIdTuples = db.getTreeSet("users");
        Bind.secondaryKey(genres, userToGenreIdTuples, (aLong, genre) -> genre.user);
    }

    private static File getDbLocation() {
        String dbLocation = System.getProperty(GenreRestServer.DB_LOCATION_PROPERTY);
        if (dbLocation == null) {
            throw new RuntimeException(String.format("%s system property not set.", GenreRestServer.DB_LOCATION_PROPERTY));
        }
        return Paths.get(dbLocation).toFile();
    }

    public static void main(String[] args) throws IOException {
        GenreService gs = new GenreService();
        System.out.println(gs.genres);
        System.out.println(gs.userToGenreIdTuples);
        System.out.println(gs.nextId);

//        gs.genres.put(1L, new Genre("user1", new String(Files.readAllBytes(Paths.get("../genres/Romantic Comedy.tsv")), StandardCharsets.UTF_8)));
//        gs.genres.put(2L, new Genre("user1", new String(Files.readAllBytes(Paths.get("../genres/SciFi.tsv")), StandardCharsets.UTF_8)));
//        gs.genres.put(3L, new Genre("user1", new String(Files.readAllBytes(Paths.get("../genres/SciFi.tsv")), StandardCharsets.UTF_8)));
//
//        gs.genres.put(Long.parseLong("B", 36), new Genre("user2", new String(Files.readAllBytes(Paths.get("../genres/Horror.tsv")), StandardCharsets.UTF_8)));
//        gs.genres.put(Long.parseLong("BE", 36), new Genre("user2", new String(Files.readAllBytes(Paths.get("../genres/Star Trek - Voyager.tsv")), StandardCharsets.UTF_8)));
//        gs.db.commit();
    }

    public List<String> getListOfGenresForUser(String user) {
        checkIfAnythingNull(user);
        // TODO: Convert to streams instead of for loop?
        List<String> ids = new ArrayList<>();
        for (Long id : Fun.filter(userToGenreIdTuples, user)) {
            ids.add(convertLongTo36BaseString(id));
        }
        return ids;
    }

    private static void checkIfAnythingNull(Object object) {
        if (object == null) {
            throw new NullPointerException();
        }
    }

    private static String convertLongTo36BaseString(Long i) {
        return Long.toString(i, 36);
    }

    public String createGenre(String user, String data) {
        checkIfAnythingNull(user, data);
        checkIfDataSizeTooBig(data);
        checkIfGenreCountTooBig(user);

        long id = nextId.getAndIncrement();
        genres.put(id, new Genre(user, data));
        db.commit();
        return convertLongTo36BaseString(id);
    }

    private static void checkIfAnythingNull(Object... objects) {
        for (Object object : objects) {
            checkIfAnythingNull(object);
        }
    }

    // NOTE: Should have the HTTP server itself also limit sizes.
    // TODO: Unit test.
    private static void checkIfDataSizeTooBig(String data) {
        int maxBytes = 1024 * 20;
        if (data.getBytes().length > maxBytes) {
            throw new RuntimeException(String.format("Genre goes over the max size %s bytes", maxBytes));
        }
    }

    // TODO: Not the best for performance probably. We should be able to keep a number as we go along
    // instead of having to count them over and over. It's not much of a problem since creating genre
    // won't happen too much.
    // TODO: Unit test.
    private int checkIfGenreCountTooBig(String user) {
        int count = 0;
        for (Long ignored : Fun.filter(userToGenreIdTuples, user)) {
            count += 1;
        }
        int maxGenres = 100;
        if (count > maxGenres) {
            throw new RuntimeException(String.format("User %s has exceeded max genres (%s).", user, count));
        }
        return count;
    }

    public void removeGenre(String user, String id) {
        Long i = convertToBase36Long(id);
        genres.remove(i);
        db.commit();
    }

    private static long convertToBase36Long(String id) {
        return Long.parseLong(id, 36);
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

    private static class Genre implements Serializable {
        public final String user;
        public final String tsv;

        public Genre(String user, String tsv) {
            checkIfAnythingNull(user, tsv);
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

        @Override
        public String toString() {
            return "Genre{" +
                    "user='" + user + '\'' +
                    ", tsv='" + tsv + '\'' +
                    '}';
        }
    }
}

