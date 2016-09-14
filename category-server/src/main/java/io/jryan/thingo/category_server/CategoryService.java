package io.jryan.thingo.category_server;

import org.mapdb.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;

public class CategoryService {
    private final DB db;
    private final HTreeMap<Long, Category> categories;
    private final NavigableSet<Fun.Tuple2<String, Long>> userToCategoryIdTuples;
    private final Atomic.Long nextId;

    public CategoryService() {
        db = DBMaker.newFileDB(getDbLocation()).closeOnJvmShutdown().make();
        categories = db.getHashMap("categories");
        nextId = db.getAtomicLong("nextId");
        userToCategoryIdTuples = db.getTreeSet("users");
        Bind.secondaryKey(categories, userToCategoryIdTuples, (aLong, category) -> category.user);
    }

    private static File getDbLocation() {
        String dbLocation = System.getProperty(CategoryRestServer.DB_LOCATION_PROPERTY);
        if (dbLocation == null) {
            throw new RuntimeException(String.format("%s system property not set.", CategoryRestServer.DB_LOCATION_PROPERTY));
        }
        return Paths.get(dbLocation).toFile();
    }

    public static void main(String[] args) throws IOException {
        CategoryService cs = new CategoryService();
        System.out.println(cs.categories);
        System.out.println(cs.userToCategoryIdTuples);
        System.out.println(cs.nextId);

//        cs.categories.put(1L, new Category("user1", new String(Files.readAllBytes(Paths.get("../categories/Romantic Comedy.tsv")), StandardCharsets.UTF_8)));
//        cs.categories.put(2L, new Category("user1", new String(Files.readAllBytes(Paths.get("../categories/SciFi.tsv")), StandardCharsets.UTF_8)));
//        cs.categories.put(3L, new Category("user1", new String(Files.readAllBytes(Paths.get("../categories/SciFi.tsv")), StandardCharsets.UTF_8)));
//
//        cs.categories.put(Long.parseLong("B", 36), new Category("user2", new String(Files.readAllBytes(Paths.get("../categories/Horror.tsv")), StandardCharsets.UTF_8)));
//        cs.categories.put(Long.parseLong("BE", 36), new Category("user2", new String(Files.readAllBytes(Paths.get("../categories/Star Trek - Voyager.tsv")), StandardCharsets.UTF_8)));
//        cs.db.commit();
    }

    private static void checkIfAnythingNull(Object object) {
        if (object == null) {
            throw new NullPointerException();
        }
    }

    private static String convertLongTo36BaseString(Long i) {
        return Long.toString(i, 36);
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
            throw new RuntimeException(String.format("Category goes over the max size %s bytes", maxBytes));
        }
    }

    private static long convertToBase36Long(String id) {
        return Long.parseLong(id, 36);
    }

    public List<String> getListOfCategoriesForUser(String user) {
        checkIfAnythingNull(user);
        // TODO: Convert to streams instead of for loop?
        List<String> ids = new ArrayList<>();
        for (Long id : Fun.filter(userToCategoryIdTuples, user)) {
            ids.add(convertLongTo36BaseString(id));
        }
        return ids;
    }

    public String createCategory(String user, String data) {
        checkIfAnythingNull(user, data);
        checkIfDataSizeTooBig(data);
        checkIfCategoryCountTooBig(user);

        long id = nextId.getAndIncrement();
        categories.put(id, new Category(user, data));
        db.commit();
        return convertLongTo36BaseString(id);
    }

    // TODO: Not the best for performance probably. We should be able to keep a number as we go along
    // instead of having to count them over and over. It's not much of a problem since creating category
    // won't happen too much.
    // TODO: Unit test.
    private int checkIfCategoryCountTooBig(String user) {
        int count = 0;
        for (Long ignored : Fun.filter(userToCategoryIdTuples, user)) {
            count += 1;
        }
        int maxCategories = 100;
        if (count > maxCategories) {
            throw new RuntimeException(String.format("User %s has exceeded max categories (%s).", user, count));
        }
        return count;
    }

    public void removeCategory(String user, String id) {
        Long i = convertToBase36Long(id);
        categories.remove(i);
        db.commit();
    }

    public String getCategory(String id) {
        Long i = convertToBase36Long(id);
        return categories.get(i).tsv;
    }

    public void updateCategory(String user, String id, String data) {
        Long i = convertToBase36Long(id);
        categories.put(i, new Category(user, data));
        db.commit();
    }

    private static class Category implements Serializable {
        public final String user;
        public final String tsv;

        public Category(String user, String tsv) {
            checkIfAnythingNull(user, tsv);
            this.user = user;
            this.tsv = tsv;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Category category = (Category) o;

            if (tsv != null ? !tsv.equals(category.tsv) : category.tsv != null) return false;
            if (user != null ? !user.equals(category.user) : category.user != null) return false;

            return true;
        }

        @Override
        public String toString() {
            return "Category{" +
                    "user='" + user + '\'' +
                    ", tsv='" + tsv + '\'' +
                    '}';
        }
    }
}

