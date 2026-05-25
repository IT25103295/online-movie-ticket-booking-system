package com.moviebooking.service;

import com.moviebooking.model.User;
import com.moviebooking.model.UserRole;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthService {
    public static final String CURRENT_USER_SESSION_KEY = "currentUser";// Constant key used to store current logged-in user in session
    private static final String USERS_FILE_PATH = "/WEB-INF/classes/data/users.txt";// File path where user data is stored in text file format
    private static final Object USERS_FILE_LOCK = new Object();

    private final ServletContext servletContext;

    public AuthService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    // Method to load users from text file
    public List<User> loadUsers() throws IOException {
        synchronized (USERS_FILE_LOCK) {
            File usersFile = getUsersFile();
            if (!usersFile.exists() || usersFile.length() == 0) {
                seedDefaultUsers(usersFile);
            }

            List<User> users = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    User user = parseUser(line);
                    if (user != null) {
                        users.add(user);
                    }
                }
            }
            return users;
        }
    }

    public void saveUsers(List<User> users) throws IOException {
        synchronized (USERS_FILE_LOCK) {
            File usersFile = getUsersFile();
            ensureParentDirectory(usersFile);

            // Users are stored in TXT files for this OOP assignment demo.
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(usersFile)))) {
                for (User user : users) {
                    writer.println(formatUser(user));
                }
            }
        }
    }
     // This method validates user login credentials
    public User validateCredentials(String usernameOrEmail, String password) throws IOException {
        String loginValue = normalize(usernameOrEmail);
        if (loginValue.isEmpty() || password == null) {
            return null;
        }

        for (User user : loadUsers()) {
            boolean usernameMatches = normalize(user.getUsername()).equals(loginValue);
            boolean emailMatches = normalize(user.getEmail()).equals(loginValue);
            if ((usernameMatches || emailMatches) && password.equals(user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public User registerUser(String name, String username, String email, String password) throws IOException {
        synchronized (USERS_FILE_LOCK) {
            List<User> users = loadUsers();
            User newUser = new User(UUID.randomUUID().toString(), name.trim(), username.trim(), email.trim(), password, UserRole.USER);
            users.add(newUser);
            saveUsers(users);
            return newUser;
        }
    }

    public User findByUsername(String username) throws IOException {
        String targetUsername = normalize(username);
        Optional<User> user = loadUsers().stream()
                .filter(existingUser -> normalize(existingUser.getUsername()).equals(targetUsername))
                .findFirst();
        return user.orElse(null);
    }

    public User findByEmail(String email) throws IOException {
        String targetEmail = normalize(email);
        Optional<User> user = loadUsers().stream()
                .filter(existingUser -> normalize(existingUser.getEmail()).equals(targetEmail))
                .findFirst();
        return user.orElse(null);
    }

    public User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object user = session.getAttribute(CURRENT_USER_SESSION_KEY);
        return user instanceof User ? (User) user : null;
    }

    public boolean isLoggedIn(HttpSession session) {
        return getCurrentUser(session) != null;
    }

    public boolean isAdmin(HttpSession session) {
        User user = getCurrentUser(session);
        return user != null && user.isAdmin();
    }

    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    private File getUsersFile() throws IOException {
        String realPath = servletContext.getRealPath(USERS_FILE_PATH);
        if (realPath == null) {
            throw new IOException("Unable to resolve users.txt with ServletContext. Deploy the WAR as an expanded Tomcat application.");
        }

        File usersFile = new File(realPath);
        ensureParentDirectory(usersFile);
        return usersFile;
    }

    private void seedDefaultUsers(File usersFile) throws IOException {
        List<User> defaultUsers = new ArrayList<>();
        defaultUsers.add(new User("1", "Administrator", "admin", "admin@cineflex.local", "admin123", UserRole.ADMIN));
        defaultUsers.add(new User("2", "Demo User", "user", "user@cineflex.local", "user123", UserRole.USER));
        ensureParentDirectory(usersFile);

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(usersFile)))) {
            for (User user : defaultUsers) {
                writer.println(formatUser(user));
            }
        }
    }

    private void ensureParentDirectory(File file) throws IOException {
        File parentDirectory = file.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists() && !parentDirectory.mkdirs()) {
            throw new IOException("Unable to create data directory: " + parentDirectory.getPath());
        }
    }

    private User parseUser(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 6) {
            return null;
        }

        try {
            return new User(parts[0], parts[1], parts[2], parts[3], parts[4], UserRole.valueOf(parts[5]));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private String formatUser(User user) {
        return String.join("|",
                clean(user.getId()),
                clean(user.getName()),
                clean(user.getUsername()),
                clean(user.getEmail()),
                clean(user.getPassword()),
                user.getRole() == null ? UserRole.USER.name() : user.getRole().name());
    }

    private String clean(String value) {
        return value == null ? "" : value.replace("|", " ").trim();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
