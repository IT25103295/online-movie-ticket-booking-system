package com.moviebooking.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatMap {
    public static final int DEFAULT_ROWS = 6;
    public static final int DEFAULT_COLUMNS = 8;

    private final int rows;
    private final int columns;
    private final Seat[][] seats;
    private final Map<String, Seat> seatLookup;

    public SeatMap() {
        this(DEFAULT_ROWS, DEFAULT_COLUMNS);
    }

    public SeatMap(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.seats = new Seat[rows][columns];
        this.seatLookup = new HashMap<>();
        initializeSeats();
    }

    public boolean isAvailable(String seatCode) {
        return SeatStatus.AVAILABLE.equals(getSeatStatus(seatCode));
    }

    public boolean holdSeats(List<String> seatCodes) {
        if (!canChangeSeats(seatCodes, SeatStatus.AVAILABLE)) {
            return false;
        }

        updateSeats(seatCodes, SeatStatus.HELD);
        return true;
    }

    public boolean releaseSeats(List<String> seatCodes) {
        if (!canChangeSeats(seatCodes, SeatStatus.HELD)) {
            return false;
        }

        updateSeats(seatCodes, SeatStatus.AVAILABLE);
        return true;
    }

    public boolean bookSeats(List<String> seatCodes) {
        if (!canChangeSeats(seatCodes, SeatStatus.HELD)) {
            return false;
        }

        updateSeats(seatCodes, SeatStatus.BOOKED);
        return true;
    }

    public SeatStatus getSeatStatus(String seatCode) {
        Seat seat = seatLookup.get(normalizeSeatCode(seatCode));
        return seat == null ? null : seat.getStatus();
    }

    public ArrayList<Seat> getAllSeats() {
        ArrayList<Seat> allSeats = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < columns; columnIndex++) {
                allSeats.add(seats[rowIndex][columnIndex]);
            }
        }
        return allSeats;
    }

    public void setSeatStatus(String seatCode, SeatStatus status) {
        Seat seat = seatLookup.get(normalizeSeatCode(seatCode));
        if (seat != null && status != null) {
            seat.setStatus(status);
        }
    }

    private void initializeSeats() {
        for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
            String rowLabel = String.valueOf((char) ('A' + rowIndex));
            for (int columnIndex = 0; columnIndex < columns; columnIndex++) {
                Seat seat = new Seat(rowLabel, columnIndex + 1, SeatStatus.AVAILABLE);
                seats[rowIndex][columnIndex] = seat;
                seatLookup.put(seat.getSeatCode(), seat);
            }
        }
    }

    private boolean canChangeSeats(List<String> seatCodes, SeatStatus requiredStatus) {
        if (seatCodes == null || seatCodes.isEmpty()) {
            return false;
        }

        for (String seatCode : seatCodes) {
            if (!requiredStatus.equals(getSeatStatus(seatCode))) {
                return false;
            }
        }
        return true;
    }

    private void updateSeats(List<String> seatCodes, SeatStatus status) {
        for (String seatCode : seatCodes) {
            setSeatStatus(seatCode, status);
        }
    }

    private String normalizeSeatCode(String seatCode) {
        return seatCode == null ? "" : seatCode.trim().toUpperCase();
    }
}
