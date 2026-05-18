package com.moviebooking.model;

public class AdminDashboardSummary {
    private final int totalMovies;
    private final int totalShowtimes;
    private final int queueLength;
    private final int recentBookingsCount;

    public AdminDashboardSummary(int totalMovies, int totalShowtimes, int queueLength, int recentBookingsCount) {
        this.totalMovies = totalMovies;
        this.totalShowtimes = totalShowtimes;
        this.queueLength = queueLength;
        this.recentBookingsCount = recentBookingsCount;
    }

    public int getTotalMovies() {
        return totalMovies;
    }

    public int getTotalShowtimes() {
        return totalShowtimes;
    }

    public int getQueueLength() {
        return queueLength;
    }

    public int getRecentBookingsCount() {
        return recentBookingsCount;
    }
}
