package com.moviebooking.service;

import com.moviebooking.model.Movie;
import jakarta.servlet.ServletContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.UUID;

public class MovieService {
    private static final String MOVIES_FILE_PATH = "/WEB-INF/classes/data/movies.txt";
    private static final Object MOVIES_FILE_LOCK = new Object();

    private final ServletContext servletContext;

    public MovieService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public ArrayList<Movie> getAllMovies() throws IOException {
        return loadMovies();
    }

    public Movie getMovieById(String id) throws IOException {
        if (isBlank(id)) {
            return null;
        }

        for (Movie movie : loadMovies()) {
            if (id.trim().equals(movie.getId())) {
                return movie;
            }
        }
        return null;
    }

    public void addMovie(Movie movie) throws IOException {
        synchronized (MOVIES_FILE_LOCK) {
            ArrayList<Movie> movies = loadMovies();
            if (isBlank(movie.getId())) {
                movie.setId(UUID.randomUUID().toString());
            }
            movies.add(movie);
            saveMovies(movies);
        }
    }

    public boolean updateMovie(Movie movie) throws IOException {
        synchronized (MOVIES_FILE_LOCK) {
            ArrayList<Movie> movies = loadMovies();
            for (int index = 0; index < movies.size(); index++) {
                if (movies.get(index).getId().equals(movie.getId())) {
                    movies.set(index, movie);
                    saveMovies(movies);
                    return true;
                }
            }
            return false;
        }
    }

    public boolean deleteMovie(String id) throws IOException {
        synchronized (MOVIES_FILE_LOCK) {
            ArrayList<Movie> movies = loadMovies();
            boolean removed = false;
            for (int index = 0; index < movies.size(); index++) {
                if (movies.get(index).getId().equals(id)) {
                    movies.remove(index);
                    removed = true;
                    break;
                }
            }

            if (removed) {
                saveMovies(movies);
            }
            return removed;
        }
    }

    public ArrayList<Movie> getSortedMovies(String sortBy) throws IOException {
        ArrayList<Movie> movies = loadMovies();
        insertionSort(movies, sortBy);
        return movies;
    }

    private ArrayList<Movie> loadMovies() throws IOException {
        synchronized (MOVIES_FILE_LOCK) {
            File moviesFile = getMoviesFile();
            if (!moviesFile.exists() || moviesFile.length() == 0) {
                seedSampleMovies(moviesFile);
            }

            ArrayList<Movie> movies = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(moviesFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    Movie movie = parseMovie(line);
                    if (movie != null) {
                        movies.add(movie);
                    }
                }
            }
            return movies;
        }
    }

    private void saveMovies(ArrayList<Movie> movies) throws IOException {
        File moviesFile = getMoviesFile();
        ensureParentDirectory(moviesFile);

        // Movies are stored in TXT files for this OOP assignment demo.
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(moviesFile)))) {
            for (Movie movie : movies) {
                writer.println(formatMovie(movie));
            }
        }
    }

    private void insertionSort(ArrayList<Movie> movies, String sortBy) {
        for (int i = 1; i < movies.size(); i++) {
            Movie currentMovie = movies.get(i);
            int previousIndex = i - 1;

            while (previousIndex >= 0 && compareMovies(movies.get(previousIndex), currentMovie, sortBy) > 0) {
                movies.set(previousIndex + 1, movies.get(previousIndex));
                previousIndex--;
            }

            movies.set(previousIndex + 1, currentMovie);
        }
    }

    private int compareMovies(Movie first, Movie second, String sortBy) {
        String normalizedSort = sortBy == null ? "" : sortBy.trim().toLowerCase();
        switch (normalizedSort) {
            case "rating":
                return Double.compare(second.getRating(), first.getRating());
            case "price":
                return Double.compare(first.getPrice(), second.getPrice());
            case "duration":
                return Integer.compare(first.getDurationMinutes(), second.getDurationMinutes());
            case "title":
            default:
                return clean(first.getTitle()).compareToIgnoreCase(clean(second.getTitle()));
        }
    }

    private File getMoviesFile() throws IOException {
        String realPath = servletContext.getRealPath(MOVIES_FILE_PATH);
        if (realPath == null) {
            throw new IOException("Unable to resolve movies.txt with ServletContext. Deploy the WAR as an expanded Tomcat application.");
        }

        File moviesFile = new File(realPath);
        ensureParentDirectory(moviesFile);
        return moviesFile;
    }

    private void seedSampleMovies(File moviesFile) throws IOException {
        ArrayList<Movie> samples = new ArrayList<>();
        samples.add(new Movie("M001", "Red Horizon", "A daring rescue mission unfolds above a city glowing with danger and old secrets.", "Action", 8.7, 132, 1850.00, "https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=600&q=80", "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1600&q=80", "PG-13"));
        samples.add(new Movie("M002", "Midnight Reel", "A film archivist discovers that a forgotten cinema print can change the present.", "Mystery", 8.2, 118, 1650.00, "https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=600&q=80", "https://images.unsplash.com/photo-1505686994434-e3cc5abf1330?auto=format&fit=crop&w=1600&q=80", "PG"));
        samples.add(new Movie("M003", "Laugh Track Love", "Two rival comedians share a stage, a city, and one very complicated weekend.", "Comedy", 7.8, 104, 1450.00, "https://images.unsplash.com/photo-1542204165-65bf26472b9b?auto=format&fit=crop&w=600&q=80", "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?auto=format&fit=crop&w=1600&q=80", "PG"));
        samples.add(new Movie("M004", "Galaxy Gate", "A pilot and a scientist cross a collapsing starway to bring their crew home.", "Sci-Fi", 9.1, 146, 2100.00, "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?auto=format&fit=crop&w=600&q=80", "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&w=1600&q=80", "PG-13"));
        saveSeedMovies(moviesFile, samples);
    }

    private void saveSeedMovies(File moviesFile, ArrayList<Movie> movies) throws IOException {
        ensureParentDirectory(moviesFile);
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(moviesFile)))) {
            for (Movie movie : movies) {
                writer.println(formatMovie(movie));
            }
        }
    }

    private Movie parseMovie(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 10) {
            return null;
        }

        try {
            return new Movie(
                    parts[0],
                    parts[1],
                    parts[2],
                    parts[3],
                    Double.parseDouble(parts[4]),
                    Integer.parseInt(parts[5]),
                    Double.parseDouble(parts[6]),
                    parts[7],
                    parts[8],
                    parts[9]
            );
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String formatMovie(Movie movie) {
        return String.join("|",
                clean(movie.getId()),
                clean(movie.getTitle()),
                clean(movie.getDescription()),
                clean(movie.getGenre()),
                String.valueOf(movie.getRating()),
                String.valueOf(movie.getDurationMinutes()),
                String.valueOf(movie.getPrice()),
                clean(movie.getPosterUrl()),
                clean(movie.getBannerUrl()),
                clean(movie.getAgeRating()));
    }

    private void ensureParentDirectory(File file) throws IOException {
        File parentDirectory = file.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists() && !parentDirectory.mkdirs()) {
            throw new IOException("Unable to create data directory: " + parentDirectory.getPath());
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String clean(String value) {
        return value == null ? "" : value.replace("|", " ").trim();
    }
}
