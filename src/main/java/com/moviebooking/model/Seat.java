package com.moviebooking.model;

public class Seat {
    private String row;
    private int number;
    private String seatCode;
    private SeatStatus status;

    public Seat() {
    }

    public Seat(String row, int number, SeatStatus status) {
        this.row = row;
        this.number = number;
        this.seatCode = row + number;
        this.status = status;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = seatCode;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }
}
