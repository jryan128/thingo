package com.joysignalgames.bazingo.internal.server.genre.services;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// TODO: Could probably be more performant. Should do some tests using synch instead of ConcurrentHashMap
// or tweak ConcurrentHashMap params.
@Singleton
public class GenreService {

    private final ConcurrentHashMap<String, List<Integer>> usersToGenreIds = new ConcurrentHashMap<>();
    // TODO: Not the most efficient way to do this. Sparse array probably a better solution for
    // the genres map. The IDs are just numbers so, the hash part is kind of useless.
    private final ConcurrentHashMap<Integer, String> genres = new ConcurrentHashMap<>();
    private final AtomicInteger nextId;

    public List<String> getListOfGenresForUser(String user) {
        List<Integer> ids = usersToGenreIds.get(user);
        return ids.stream()
                .map(this::convertIntToBase36String)
                .collect(Collectors.toList());
    }

    public String createGenre(String user, String data) {
        int i = nextId.getAndIncrement();
        genres.put(i, data);
        usersToGenreIds.putIfAbsent(user, Collections.synchronizedList(new ArrayList<>()));
        usersToGenreIds.get(user).add(i);
        return convertIntToBase36String(i);
    }

    private String convertIntToBase36String(Integer i) {
        return Integer.toString(i, 36);
    }

    public void removeGenre(String user, String id) {
        Integer i = convertToBase36Integer(id);
        usersToGenreIds.get(user).remove(i);
        genres.remove(i);
    }

    public String getGenre(String id) {
        Integer i = convertToBase36Integer(id);
        return genres.get(i);
    }

    public void updateGenre(String id, String data) {
        Integer i = convertToBase36Integer(id);
        genres.put(i, data);
    }

    private int convertToBase36Integer(String id) {
        return Integer.parseInt(id, 36);
    }

    public GenreService() {
        // FIXME: exception handling, Files.list is not in try-with-resources.
        Path genreStore = Paths.get("src/test/genrestore");
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(genreStore)) {
            int largest = -1;
            for (Path userDirectory : directoryStream) {
                String userName = userDirectory.getFileName().toString();
                Map<Integer, String> userGenres = Files.list(userDirectory)
                        .collect(Collectors.toMap(this::convertFileNameToInt, this::readLinesIntoString));
                List<Integer> genreIds = new ArrayList<>();
                for (Map.Entry<Integer, String> entry : userGenres.entrySet()) {
                    Integer i = entry.getKey();
                    if (i > largest) {
                        largest = i;
                    }
                    genreIds.add(i);
                    this.genres.put(i, entry.getValue());
                }
                this.usersToGenreIds.put(userName, Collections.synchronizedList(genreIds));
            }
            this.nextId = new AtomicInteger(largest + 1);
        } catch (Exception e) {
            throw new RuntimeException("Could not create GenreService.", e);
        }
    }

    private int convertFileNameToInt(Path f) {
        return convertToBase36Integer(f.getFileName().toString());
    }

    private String readLinesIntoString(Path file) throws UncheckedIOException {
        try {
            return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
