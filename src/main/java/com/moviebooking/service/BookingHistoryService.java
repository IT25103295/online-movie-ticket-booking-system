package com.moviebooking.service;

import com.moviebooking.model.Booking;
import com.moviebooking.model.BookingStatus;
import jakarta.servlet.ServletContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BookingHistoryService {
    public static final String DEMO_CUSTOMER_EMAIL = "demo@cineflex.local";
    private static final String BOOKINGS_FILE_PATH = "/WEB-INF/classes/data/bookings.txt";
    private static final Object BOOKINGS_FILE_LOCK = new Object();

    private final ServletContext servletContext;
    private Map<String, List<Booking>> bookingHistory;

    public BookingHistoryService(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.bookingHistory = new HashMap<>();
    }

    public void saveConfirmedBooking(String customerEmail, Booking booking) throws IOException {
        synchronized (BOOKINGS_FILE_LOCK) {
            loadBookings();
            String historyKey = normalizeEmail(customerEmail);
            bookingHistory.computeIfAbsent(historyKey, key -> new ArrayList<>()).add(booking);
            saveBookings();
        }
    }

    public List<Booking> getBookingsForUser(String customerEmail) throws IOException {
        synchronized (BOOKINGS_FILE_LOCK) {
            loadBookings();
            return new ArrayList<>(bookingHistory.getOrDefault(normalizeEmail(customerEmail), new ArrayList<>()));
        }
    }

    public Booking getBookingById(String bookingId) throws IOException {
        synchronized (BOOKINGS_FILE_LOCK) {
            loadBookings();
            for (List<Booking> bookings : bookingHistory.values()) {
                for (Booking booking : bookings) {
                    if (booking.getBookingId().equals(bookingId)) {
                        return booking;
                    }
                }
            }
            return null;
        }
    }

    public boolean cancelBooking(String bookingId) throws IOException {
        return updateBookingStatus(bookingId, BookingStatus.CANCELLED);
    }

    public boolean removeBooking(String bookingId) throws IOException {
        synchronized (BOOKINGS_FILE_LOCK) {
            loadBookings();
            boolean removed = false;
            for (List<Booking> bookings : bookingHistory.values()) {
                for (int index = 0; index < bookings.size(); index++) {
                    if (bookings.get(index).getBookingId().equals(bookingId)) {
                        bookings.remove(index);
                        removed = true;
                        break;
                    }
                }
                if (removed) {
                    break;
                }
            }
            if (removed) {
                saveBookings();
            }
            return removed;
        }
    }

    public Booking createBookingFromConfirmedRequest(String requestId, String customerName, String customerEmail,
                                                     String movieTitle, String showtimeDate, String showtimeTime,
                                                     String cinemaHall, List<String> seats, double totalPrice) {
        String bookingId = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new Booking(bookingId, requestId, customerName, customerEmail, movieTitle, showtimeDate,
                showtimeTime, cinemaHall, seats, totalPrice, LocalDateTime.now().toString(), BookingStatus.CONFIRMED);
    }

    private boolean updateBookingStatus(String bookingId, BookingStatus status) throws IOException {
        synchronized (BOOKINGS_FILE_LOCK) {
            loadBookings();
            boolean updated = false;
            for (Map.Entry<String, List<Booking>> entry : bookingHistory.entrySet()) {
                List<Booking> bookings = entry.getValue();
                for (int index = 0; index < bookings.size(); index++) {
                    Booking booking = bookings.get(index);
                    if (booking.getBookingId().equals(bookingId)) {
                        bookings.set(index, booking.withStatus(status));
                        updated = true;
                        break;
                    }
                }
                if (updated) {
                    break;
                }
            }
            if (updated) {
                saveBookings();
            }
            return updated;
        }
    }

    private void loadBookings() throws IOException {
        bookingHistory = new HashMap<>();
        File bookingsFile = getBookingsFile();
        if (!bookingsFile.exists() || bookingsFile.length() == 0) {
            seedDemoBooking(bookingsFile);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(bookingsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Booking booking = parseBooking(line);
                if (booking != null) {
                    bookingHistory.computeIfAbsent(normalizeEmail(booking.getCustomerEmail()), key -> new ArrayList<>()).add(booking);
                }
            }
        }
    }

    private void saveBookings() throws IOException {
        File bookingsFile = getBookingsFile();
        ensureParentDirectory(bookingsFile);

        // Booking history data is stored in TXT files for this OOP assignment demo.
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(bookingsFile)))) {
            for (List<Booking> bookings : bookingHistory.values()) {
                for (Booking booking : bookings) {
                    writer.println(formatBooking(booking));
                }
            }
        }
    }

    private Booking parseBooking(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 12) {
            return null;
        }
        try {
            return new Booking(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7],
                    parseSeats(parts[8]), Double.parseDouble(parts[9]), parts[10], BookingStatus.valueOf(parts[11]));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private String formatBooking(Booking booking) {
        return String.join("|",
                clean(booking.getBookingId()),
                clean(booking.getRequestId()),
                clean(booking.getCustomerName()),
                clean(booking.getCustomerEmail()),
                clean(booking.getMovieTitle()),
                clean(booking.getShowtimeDate()),
                clean(booking.getShowtimeTime()),
                clean(booking.getCinemaHall()),
                clean(String.join(",", booking.getSeats())),
                String.valueOf(booking.getTotalPrice()),
                clean(booking.getConfirmedAt()),
                booking.getStatus().name());
    }

    private ArrayList<String> parseSeats(String seatText) {
        ArrayList<String> seats = new ArrayList<>();
        if (seatText == null || seatText.trim().isEmpty()) {
            return seats;
        }
        String[] parts = seatText.split(",");
        for (String part : parts) {
            if (!part.trim().isEmpty()) {
                seats.add(part.trim());
            }
        }
        return seats;
    }

    private void seedDemoBooking(File bookingsFile) throws IOException {
        ensureParentDirectory(bookingsFile);
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(bookingsFile)))) {
            writer.println("BK001|REQ001|Demo Customer|demo@cineflex.local|Red Horizon|2026-05-15|7:30 PM|Hall 1|A1,A2|3000.00|2026-05-15T18:00|CONFIRMED");
        }
    }

    private File getBookingsFile() throws IOException {
        String realPath = servletContext.getRealPath(BOOKINGS_FILE_PATH);
        if (realPath == null) {
            throw new IOException("Unable to resolve bookings.txt with ServletContext. Deploy the WAR as an expanded Tomcat application.");
        }
        File bookingsFile = new File(realPath);
        ensureParentDirectory(bookingsFile);
        return bookingsFile;
    }

    private void ensureParentDirectory(File file) throws IOException {
        File parentDirectory = file.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists() && !parentDirectory.mkdirs()) {
            throw new IOException("Unable to create data directory: " + parentDirectory.getPath());
        }
    }

    private String normalizeEmail(String email) {
        return email == null || email.trim().isEmpty() ? DEMO_CUSTOMER_EMAIL : email.trim().toLowerCase();
    }

    private String clean(String value) {
        return value == null ? "" : value.replace("|", " ").trim();
    }
}
