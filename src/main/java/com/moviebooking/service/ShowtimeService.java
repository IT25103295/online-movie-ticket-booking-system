package com.moviebooking.service;

import com.moviebooking.model.Seat;
import com.moviebooking.model.SeatMap;
import com.moviebooking.model.SeatStatus;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowtimeService {
    private static final String SHOWTIMES_FILE_PATH = "/WEB-INF/classes/data/showtimes.txt";
    private static final String SEATS_FILE_PATH = "/WEB-INF/classes/data/seats.txt";
    private static final String MOVIES_FILE_PATH = "/WEB-INF/classes/data/movies.txt";
    private static final Object DATA_FILE_LOCK = new Object();

    private final ServletContext servletContext;

    public ShowtimeService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public ArrayList<Showtime> getShowtimesByMovieId(String movieId) throws IOException {
        ArrayList<Showtime> allShowtimes = loadShowtimes();
        ArrayList<Showtime> matchingShowtimes = new ArrayList<>();
        for (Showtime showtime : allShowtimes) {
            if (movieId != null && movieId.trim().equals(showtime.getMovieId())) {
                matchingShowtimes.add(showtime);
            }
        }
        return matchingShowtimes;
    }

    public Showtime getShowtimeById(String showtimeId) throws IOException {
        if (isBlank(showtimeId)) {
            return null;
        }

        for (Showtime showtime : loadShowtimes()) {
            if (showtimeId.trim().equals(showtime.getId())) {
                return showtime;
            }
        }
        return null;
    }

    public SeatMap getSeatMap(String showtimeId) throws IOException {
        Showtime showtime = getShowtimeById(showtimeId);
        return showtime == null ? null : showtime.getSeatMap();
    }

    public boolean holdSeats(String showtimeId, List<String> seatCodes) throws IOException {
        synchronized (DATA_FILE_LOCK) {
            ArrayList<Showtime> showtimes = loadShowtimes();
            Showtime showtime = findShowtime(showtimes, showtimeId);
            if (showtime == null || !showtime.getSeatMap().holdSeats(seatCodes)) {
                return false;
            }

            saveSeats(showtimes);
            return true;
        }
    }

    public boolean releaseSeats(String showtimeId, List<String> seatCodes) throws IOException {
        synchronized (DATA_FILE_LOCK) {
            ArrayList<Showtime> showtimes = loadShowtimes();
            Showtime showtime = findShowtime(showtimes, showtimeId);
            if (showtime == null || !showtime.getSeatMap().releaseSeats(seatCodes)) {
                return false;
            }

            saveSeats(showtimes);
            return true;
        }
    }

    public boolean bookSeats(String showtimeId, List<String> seatCodes) throws IOException {
        synchronized (DATA_FILE_LOCK) {
            ArrayList<Showtime> showtimes = loadShowtimes();
            Showtime showtime = findShowtime(showtimes, showtimeId);
            if (showtime == null || !showtime.getSeatMap().bookSeats(seatCodes)) {
                return false;
            }

            saveSeats(showtimes);
            return true;
        }
    }

    public String getMovieTitle(String movieId) throws IOException {
        if (isBlank(movieId)) {
            return "Selected Movie";
        }

        File moviesFile = getOptionalMoviesFile();
        if (!moviesFile.exists()) {
            return "Movie " + movieId;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(moviesFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 2 && movieId.trim().equals(parts[0])) {
                    return parts[1];
                }
            }
        }
        return "Movie " + movieId;
    }

    private ArrayList<Showtime> loadShowtimes() throws IOException {
        synchronized (DATA_FILE_LOCK) {
            File showtimesFile = getShowtimesFile();
            File seatsFile = getSeatsFile();
            if (!showtimesFile.exists() || showtimesFile.length() == 0) {
                seedSampleShowtimes(showtimesFile);
            }
            if (!seatsFile.exists() || seatsFile.length() == 0) {
                seedSampleSeats(seatsFile, loadShowtimeIds(showtimesFile));
            }

            ArrayList<Showtime> showtimes = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(showtimesFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    Showtime showtime = parseShowtime(line);
                    if (showtime != null) {
                        showtimes.add(showtime);
                    }
                }
            }

            applySeatStatuses(showtimes, seatsFile);
            saveSeats(showtimes);
            return showtimes;
        }
    }

    private Showtime findShowtime(ArrayList<Showtime> showtimes, String showtimeId) {
        if (isBlank(showtimeId)) {
            return null;
        }

        for (Showtime showtime : showtimes) {
            if (showtimeId.trim().equals(showtime.getId())) {
                return showtime;
            }
        }
        return null;
    }

    private void saveSeats(ArrayList<Showtime> showtimes) throws IOException {
        File seatsFile = getSeatsFile();
        ensureParentDirectory(seatsFile);

        // Showtime and seat data are stored in TXT files for this OOP assignment demo.
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(seatsFile)))) {
            for (Showtime showtime : showtimes) {
                for (Seat seat : showtime.getSeatMap().getAllSeats()) {
                    writer.println(showtime.getId() + "|" + seat.getSeatCode() + "|" + seat.getStatus().name());
                }
            }
        }
    }

    private void applySeatStatuses(ArrayList<Showtime> showtimes, File seatsFile) throws IOException {
        Map<String, Showtime> showtimeLookup = new HashMap<>();
        for (Showtime showtime : showtimes) {
            showtimeLookup.put(showtime.getId(), showtime);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(seatsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length != 3) {
                    continue;
                }

                Showtime showtime = showtimeLookup.get(parts[0]);
                if (showtime == null) {
                    continue;
                }

                try {
                    showtime.getSeatMap().setSeatStatus(parts[1], SeatStatus.valueOf(parts[2]));
                } catch (IllegalArgumentException ignored) {
                    // Invalid TXT status values are ignored so the rest of the demo data can still load.
                }
            }
        }
    }

    private Showtime parseShowtime(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 5) {
            return null;
        }
        return new Showtime(parts[0], parts[1], parts[2], parts[3], parts[4], new SeatMap());
    }

    private ArrayList<String> loadShowtimeIds(File showtimesFile) throws IOException {
        ArrayList<String> ids = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(showtimesFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length == 5) {
                    ids.add(parts[0]);
                }
            }
        }
        return ids;
    }

    private void seedSampleShowtimes(File showtimesFile) throws IOException {
        ensureParentDirectory(showtimesFile);
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(showtimesFile)))) {
            writer.println("ST001|M001|Hall 01|2026-05-15|10:30 AM");
            writer.println("ST002|M001|Hall 02|2026-05-15|02:15 PM");
            writer.println("ST003|M001|Hall 01|2026-05-15|07:30 PM");
            writer.println("ST004|M002|Hall 03|2026-05-16|11:00 AM");
            writer.println("ST005|M002|Hall 02|2026-05-16|06:45 PM");
            writer.println("ST006|M003|Hall 01|2026-05-17|01:30 PM");
            writer.println("ST007|M004|Hall 04|2026-05-17|08:00 PM");
            writer.println("ST008|M004|Hall 04|2026-05-18|05:15 PM");
        }
    }

    private void seedSampleSeats(File seatsFile, ArrayList<String> showtimeIds) throws IOException {
        ensureParentDirectory(seatsFile);
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(seatsFile)))) {
            for (String showtimeId : showtimeIds) {
                for (int rowIndex = 0; rowIndex < SeatMap.DEFAULT_ROWS; rowIndex++) {
                    String row = String.valueOf((char) ('A' + rowIndex));
                    for (int number = 1; number <= SeatMap.DEFAULT_COLUMNS; number++) {
                        String seatCode = row + number;
                        SeatStatus status = isSampleBookedSeat(showtimeId, seatCode) ? SeatStatus.BOOKED : SeatStatus.AVAILABLE;
                        writer.println(showtimeId + "|" + seatCode + "|" + status.name());
                    }
                }
            }
        }
    }

    private boolean isSampleBookedSeat(String showtimeId, String seatCode) {
        return ("ST001".equals(showtimeId) && ("B3".equals(seatCode) || "B4".equals(seatCode) || "C4".equals(seatCode)))
                || ("ST002".equals(showtimeId) && ("A1".equals(seatCode) || "A2".equals(seatCode)))
                || ("ST004".equals(showtimeId) && ("D5".equals(seatCode) || "D6".equals(seatCode)))
                || ("ST007".equals(showtimeId) && ("E7".equals(seatCode) || "F8".equals(seatCode)));
    }

    private File getShowtimesFile() throws IOException {
        return resolveDataFile(SHOWTIMES_FILE_PATH);
    }

    private File getSeatsFile() throws IOException {
        return resolveDataFile(SEATS_FILE_PATH);
    }

    private File getOptionalMoviesFile() throws IOException {
        return resolveDataFile(MOVIES_FILE_PATH);
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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
