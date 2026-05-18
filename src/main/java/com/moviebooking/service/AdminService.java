package com.moviebooking.service;

import com.moviebooking.model.AdminBookingRecord;
import com.moviebooking.model.AdminDashboardSummary;
import com.moviebooking.model.Movie;
import com.moviebooking.model.Showtime;
import jakarta.servlet.ServletContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminService {
    private static final String MOVIES_FILE_PATH = "/WEB-INF/classes/data/movies.txt";
    private static final String SHOWTIMES_FILE_PATH = "/WEB-INF/classes/data/showtimes.txt";
    private static final String SEATS_FILE_PATH = "/WEB-INF/classes/data/seats.txt";
    private static final String BOOKING_QUEUE_FILE_PATH = "/WEB-INF/classes/data/booking-queue.txt";
    private static final String PROCESSED_BOOKINGS_FILE_PATH = "/WEB-INF/classes/data/processed-bookings.txt";
    private static final String BOOKINGS_FILE_PATH = "/WEB-INF/classes/data/bookings.txt";
    private static final Object ADMIN_FILE_LOCK = new Object();

    private final ServletContext servletContext;

    public AdminService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public AdminDashboardSummary getDashboardSummary() throws IOException {
        synchronized (ADMIN_FILE_LOCK) {
            ensureDataFiles();
            return new AdminDashboardSummary(
                    getMovies().size(),
                    getShowtimes().size(),
                    countLines(getBookingQueueFile()),
                    getRecentBookings().size());
        }
    }

    public ArrayList<Movie> getMovies() throws IOException {
        synchronized (ADMIN_FILE_LOCK) {
            File moviesFile = getMoviesFile();
            if (!moviesFile.exists() || moviesFile.length() == 0) {
                seedMovies(moviesFile);
            }

            ArrayList<Movie> movies = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(moviesFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Movie movie = parseMovie(line);
                    if (movie != null) {
                        movies.add(movie);
                    }
                }
            }
            return movies;
        }
    }

    public void addMovie(Movie movie) throws IOException {
        synchronized (ADMIN_FILE_LOCK) {
            ArrayList<Movie> movies = getMovies();
            if (isBlank(movie.getId())) {
                movie.setId("M-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            }
            movies.add(movie);
            saveMovies(movies);
        }
    }

    public boolean updateMovie(Movie movie) throws IOException {
        synchronized (ADMIN_FILE_LOCK) {
            ArrayList<Movie> movies = getMovies();
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

    public boolean deleteMovie(String movieId) throws IOException {
        synchronized (ADMIN_FILE_LOCK) {
            ArrayList<Movie> movies = getMovies();
            boolean removed = movies.removeIf(movie -> movie.getId().equals(movieId));
            if (removed) {
                saveMovies(movies);
            }
            return removed;
        }
    }

    public ArrayList<Showtime> getShowtimes() throws IOException {
        synchronized (ADMIN_FILE_LOCK) {
            File showtimesFile = getShowtimesFile();
            if (!showtimesFile.exists() || showtimesFile.length() == 0) {
                seedShowtimes(showtimesFile);
            }

            ArrayList<Showtime> showtimes = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(showtimesFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Showtime showtime = parseShowtime(line);
                    if (showtime != null) {
                        showtimes.add(showtime);
                    }
                }
            }
            return showtimes;
        }
    }

    public void addShowtime(Showtime showtime) throws IOException {
        synchronized (ADMIN_FILE_LOCK) {
            ArrayList<Showtime> showtimes = getShowtimes();
            if (isBlank(showtime.getId())) {
                showtime.setId("ST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            }
            showtimes.add(showtime);
            saveShowtimes(showtimes);
            seedSeatsForShowtime(showtime.getId());
        }
    }

    public boolean updateShowtime(Showtime showtime) throws IOException {
        synchronized (ADMIN_FILE_LOCK) {
            ArrayList<Showtime> showtimes = getShowtimes();
            for (int index = 0; index < showtimes.size(); index++) {
                if (showtimes.get(index).getId().equals(showtime.getId())) {
                    showtimes.set(index, showtime);
                    saveShowtimes(showtimes);
                    seedSeatsForShowtime(showtime.getId());
                    return true;
                }
            }
            return false;
        }
    }

    public boolean deleteShowtime(String showtimeId) throws IOException {
        synchronized (ADMIN_FILE_LOCK) {
            ArrayList<Showtime> showtimes = getShowtimes();
            boolean removed = showtimes.removeIf(showtime -> showtime.getId().equals(showtimeId));
            if (removed) {
                saveShowtimes(showtimes);
                removeSeatsForShowtime(showtimeId);
            }
            return removed;
        }
    }

    public ArrayList<AdminBookingRecord> getRecentBookings() throws IOException {
        synchronized (ADMIN_FILE_LOCK) {
            ensureDataFiles();
            ArrayList<AdminBookingRecord> records = new ArrayList<>();
            records.addAll(readBookingsFile(getBookingsFile()));
            records.addAll(readProcessedBookingsFile(getProcessedBookingsFile()));
            return records;
        }
    }

    public boolean hasQueueData() throws IOException {
        return getBookingQueueFile().exists() && getBookingQueueFile().length() > 0;
    }

    private void saveMovies(ArrayList<Movie> movies) throws IOException {
        // Admin panel operations persist data using TXT files for this OOP assignment demo.
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(getMoviesFile())))) {
            for (Movie movie : movies) {
                writer.println(formatMovie(movie));
            }
        }
    }

    private void saveShowtimes(ArrayList<Showtime> showtimes) throws IOException {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(getShowtimesFile())))) {
            for (Showtime showtime : showtimes) {
                writer.println(formatShowtime(showtime));
            }
        }
    }

    private Movie parseMovie(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 10) {
            return null;
        }
        try {
            return new Movie(parts[0], parts[1], parts[2], parts[3], Double.parseDouble(parts[4]),
                    Integer.parseInt(parts[5]), Double.parseDouble(parts[6]), parts[7], parts[8], parts[9]);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Showtime parseShowtime(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 5) {
            return null;
        }
        return new Showtime(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }

    private ArrayList<AdminBookingRecord> readBookingsFile(File file) throws IOException {
        ArrayList<AdminBookingRecord> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length == 12) {
                    records.add(new AdminBookingRecord(parts[0], parts[2] + " / " + parts[3], parts[4], parts[8], parts[11], parseDouble(parts[9])));
                }
            }
        }
        return records;
    }

    private ArrayList<AdminBookingRecord> readProcessedBookingsFile(File file) throws IOException {
        ArrayList<AdminBookingRecord> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 11) {
                    records.add(new AdminBookingRecord(parts[0], parts[1] + " / " + parts[2], parts[3], parts[5], parts[7], parseDouble(parts[6])));
                }
            }
        }
        return records;
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

    private String formatShowtime(Showtime showtime) {
        return String.join("|",
                clean(showtime.getId()),
                clean(showtime.getMovieId()),
                clean(showtime.getCinemaHall()),
                clean(showtime.getDate()),
                clean(showtime.getTime()));
    }

    private void seedMovies(File file) throws IOException {
        ArrayList<Movie> movies = new ArrayList<>();
        movies.add(new Movie("M001", "Red Horizon", "A daring rescue mission unfolds above a city glowing with danger.", "Action", 8.7, 132, 1850.00, "https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=600&q=80", "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1600&q=80", "PG-13"));
        movies.add(new Movie("M002", "Midnight Reel", "A film archivist discovers a forgotten cinema print with a secret.", "Mystery", 8.2, 118, 1650.00, "https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=600&q=80", "https://images.unsplash.com/photo-1505686994434-e3cc5abf1330?auto=format&fit=crop&w=1600&q=80", "PG"));
        saveMovies(movies);
    }

    private void seedShowtimes(File file) throws IOException {
        ArrayList<Showtime> showtimes = new ArrayList<>();
        showtimes.add(new Showtime("ST001", "M001", "Hall 01", "2026-05-15", "10:30 AM"));
        showtimes.add(new Showtime("ST002", "M001", "Hall 02", "2026-05-15", "07:30 PM"));
        showtimes.add(new Showtime("ST003", "M002", "Hall 03", "2026-05-16", "02:15 PM"));
        saveShowtimes(showtimes);
        for (Showtime showtime : showtimes) {
            seedSeatsForShowtime(showtime.getId());
        }
    }

    private void seedSeatsForShowtime(String showtimeId) throws IOException {
        File seatsFile = getSeatsFile();
        ArrayList<String> lines = readAllLines(seatsFile);
        for (String line : lines) {
            if (line.startsWith(showtimeId + "|")) {
                return;
            }
        }
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(seatsFile, true)))) {
            for (char row = 'A'; row <= 'F'; row++) {
                for (int number = 1; number <= 8; number++) {
                    writer.println(showtimeId + "|" + row + number + "|AVAILABLE");
                }
            }
        }
    }

    private void removeSeatsForShowtime(String showtimeId) throws IOException {
        File seatsFile = getSeatsFile();
        ArrayList<String> keptLines = new ArrayList<>();
        for (String line : readAllLines(seatsFile)) {
            if (!line.startsWith(showtimeId + "|")) {
                keptLines.add(line);
            }
        }
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(seatsFile)))) {
            for (String line : keptLines) {
                writer.println(line);
            }
        }
    }

    private ArrayList<String> readAllLines(File file) throws IOException {
        ensureFile(file);
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    private int countLines(File file) throws IOException {
        return readAllLines(file).size();
    }

    private void ensureDataFiles() throws IOException {
        ensureFile(getMoviesFile());
        ensureFile(getShowtimesFile());
        ensureFile(getSeatsFile());
        ensureFile(getBookingQueueFile());
        ensureFile(getProcessedBookingsFile());
        ensureFile(getBookingsFile());
    }

    private void ensureFile(File file) throws IOException {
        ensureParentDirectory(file);
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Unable to create data file: " + file.getPath());
        }
    }

    private File getMoviesFile() throws IOException {
        return resolveDataFile(MOVIES_FILE_PATH);
    }

    private File getShowtimesFile() throws IOException {
        return resolveDataFile(SHOWTIMES_FILE_PATH);
    }

    private File getSeatsFile() throws IOException {
        return resolveDataFile(SEATS_FILE_PATH);
    }

    private File getBookingQueueFile() throws IOException {
        return resolveDataFile(BOOKING_QUEUE_FILE_PATH);
    }

    private File getProcessedBookingsFile() throws IOException {
        return resolveDataFile(PROCESSED_BOOKINGS_FILE_PATH);
    }

    private File getBookingsFile() throws IOException {
        return resolveDataFile(BOOKINGS_FILE_PATH);
    }

    private File resolveDataFile(String resourcePath) throws IOException {
        String realPath = servletContext.getRealPath(resourcePath);
        if (realPath == null) {
            throw new IOException("Unable to resolve TXT data file with ServletContext. Deploy the WAR as an expanded Tomcat application.");
        }
        File file = new File(realPath);
        ensureParentDirectory(file);
        return file;
    }

    private void ensureParentDirectory(File file) throws IOException {
        File parentDirectory = file.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists() && !parentDirectory.mkdirs()) {
            throw new IOException("Unable to create data directory: " + parentDirectory.getPath());
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String clean(String value) {
        return value == null ? "" : value.replace("|", " ").trim();
    }
}
