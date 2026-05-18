package com.moviebooking.service;

import com.moviebooking.model.BookingRequest;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class BookingService {
    private static final String QUEUE_FILE_PATH = "/WEB-INF/classes/data/booking-queue.txt";
    private static final String PROCESSED_FILE_PATH = "/WEB-INF/classes/data/processed-bookings.txt";
    private static final Object BOOKING_FILE_LOCK = new Object();

    private final ServletContext servletContext;
    private Queue<BookingRequest> bookingQueue;
    private List<BookingRequest> processedRequests;

    public BookingService(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.bookingQueue = new LinkedList<>();
        this.processedRequests = new ArrayList<>();
    }

    public BookingRequest enqueueBooking(BookingRequest request) throws IOException {
        synchronized (BOOKING_FILE_LOCK) {
            loadState();
            if (isBlank(request.getRequestId())) {
                request.setRequestId("BR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            }
            if (isBlank(request.getCreatedAt())) {
                request.setCreatedAt(LocalDateTime.now().toString());
            }
            request.setStatus(BookingStatus.PENDING);
            bookingQueue.add(request);
            saveQueue();
            return request;
        }
    }

    public BookingRequest processNext() throws IOException {
        synchronized (BOOKING_FILE_LOCK) {
            loadState();
            BookingRequest request = bookingQueue.poll();
            if (request == null) {
                return null;
            }

            if (validateSeats(request.getShowtimeId(), request.getSelectedSeats()) && bookSeatsForShowtime(request)) {
                request.confirm();
            } else {
                request.reject("Seats are unavailable or missing.");
            }

            processedRequests.add(request);
            saveQueue();
            saveProcessedRequests();
            return request;
        }
    }

    public boolean validateSeats(String showtimeId, List<String> selectedSeats) {
        return !isBlank(showtimeId) && selectedSeats != null && !selectedSeats.isEmpty();
    }

    public int getQueueSize() throws IOException {
        synchronized (BOOKING_FILE_LOCK) {
            loadState();
            return bookingQueue.size();
        }
    }

    public List<BookingRequest> getPendingRequests() throws IOException {
        synchronized (BOOKING_FILE_LOCK) {
            loadState();
            return new ArrayList<>(bookingQueue);
        }
    }

    public List<BookingRequest> getProcessedRequests() throws IOException {
        synchronized (BOOKING_FILE_LOCK) {
            loadState();
            return new ArrayList<>(processedRequests);
        }
    }

    public BookingRequest getLastProcessed() throws IOException {
        synchronized (BOOKING_FILE_LOCK) {
            loadState();
            if (processedRequests.isEmpty()) {
                return null;
            }
            return processedRequests.get(processedRequests.size() - 1);
        }
    }

    public BookingRequest findRequestById(String requestId) throws IOException {
        synchronized (BOOKING_FILE_LOCK) {
            loadState();
            for (BookingRequest request : bookingQueue) {
                if (request.getRequestId().equals(requestId)) {
                    return request;
                }
            }
            for (BookingRequest request : processedRequests) {
                if (request.getRequestId().equals(requestId)) {
                    return request;
                }
            }
            return null;
        }
    }

    private boolean bookSeatsForShowtime(BookingRequest request) {
        // Placeholder for Component 03 integration: call ShowtimeService.bookSeats(showtimeId, selectedSeats) here when available.
        return validateSeats(request.getShowtimeId(), request.getSelectedSeats());
    }

    private void loadState() throws IOException {
        bookingQueue = new LinkedList<>();
        processedRequests = new ArrayList<>();
        ensureDataFiles();
        loadQueueFile();
        loadProcessedFile();
    }

    private void loadQueueFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(getQueueFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                BookingRequest request = parseQueueRequest(line);
                if (request != null) {
                    bookingQueue.add(request);
                }
            }
        }
    }

    private void loadProcessedFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(getProcessedFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                BookingRequest request = parseProcessedRequest(line);
                if (request != null) {
                    processedRequests.add(request);
                }
            }
        }
    }

    private void saveQueue() throws IOException {
        // Booking queue data is stored in TXT files for this OOP assignment demo.
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(getQueueFile())))) {
            for (BookingRequest request : bookingQueue) {
                writer.println(formatQueueRequest(request));
            }
        }
    }

    private void saveProcessedRequests() throws IOException {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(getProcessedFile())))) {
            for (BookingRequest request : processedRequests) {
                writer.println(formatProcessedRequest(request));
            }
        }
    }

    private BookingRequest parseQueueRequest(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 9) {
            return null;
        }
        try {
            return new BookingRequest(parts[0], parts[1], parts[2], parts[3], parts[4], parseSeats(parts[5]),
                    Double.parseDouble(parts[6]), BookingStatus.valueOf(parts[7]), parts[8], "", "-");
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private BookingRequest parseProcessedRequest(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 11) {
            return null;
        }
        try {
            return new BookingRequest(parts[0], parts[1], parts[2], parts[3], parts[4], parseSeats(parts[5]),
                    Double.parseDouble(parts[6]), BookingStatus.valueOf(parts[7]), parts[8], parts[9], parts[10]);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private String formatQueueRequest(BookingRequest request) {
        return String.join("|",
                clean(request.getRequestId()),
                clean(request.getCustomerName()),
                clean(request.getCustomerEmail()),
                clean(request.getMovieId()),
                clean(request.getShowtimeId()),
                clean(String.join(",", request.getSelectedSeats())),
                String.valueOf(request.getTotalPrice()),
                request.getStatus() == null ? BookingStatus.PENDING.name() : request.getStatus().name(),
                clean(request.getCreatedAt()));
    }

    private String formatProcessedRequest(BookingRequest request) {
        return String.join("|",
                clean(request.getRequestId()),
                clean(request.getCustomerName()),
                clean(request.getCustomerEmail()),
                clean(request.getMovieId()),
                clean(request.getShowtimeId()),
                clean(String.join(",", request.getSelectedSeats())),
                String.valueOf(request.getTotalPrice()),
                request.getStatus() == null ? BookingStatus.PENDING.name() : request.getStatus().name(),
                clean(request.getCreatedAt()),
                clean(request.getProcessedAt()),
                clean(request.getRejectionReason()));
    }

    private ArrayList<String> parseSeats(String seatText) {
        ArrayList<String> seats = new ArrayList<>();
        if (isBlank(seatText)) {
            return seats;
        }
        String[] parts = seatText.split(",");
        for (String part : parts) {
            if (!isBlank(part)) {
                seats.add(part.trim());
            }
        }
        return seats;
    }

    private void ensureDataFiles() throws IOException {
        File queueFile = getQueueFile();
        File processedFile = getProcessedFile();
        ensureParentDirectory(queueFile);
        ensureParentDirectory(processedFile);
        if (!queueFile.exists() && !queueFile.createNewFile()) {
            throw new IOException("Unable to create booking queue file.");
        }
        if (!processedFile.exists() && !processedFile.createNewFile()) {
            throw new IOException("Unable to create processed bookings file.");
        }
    }

    private File getQueueFile() throws IOException {
        return resolveDataFile(QUEUE_FILE_PATH);
    }

    private File getProcessedFile() throws IOException {
        return resolveDataFile(PROCESSED_FILE_PATH);
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

    private String clean(String value) {
        return value == null ? "" : value.replace("|", " ").trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
