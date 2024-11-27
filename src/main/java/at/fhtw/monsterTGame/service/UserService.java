package at.fhtw.monsterTGame.service;

import at.fhtw.monsterTGame.model.User;
import at.fhtw.monsterTGame.persistence.UnitOfWork;
import at.fhtw.monsterTGame.persistence.repository.UserRepository;
import at.fhtw.monsterTGame.persistence.repository.UserRepositoryImpl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public class UserService {
    private final UserRepository userRepo;

    public UserService() {
        this.userRepo = new UserRepositoryImpl(new UnitOfWork());
    }

    public boolean addUser(User user) throws SQLException {
        // Überprüfen, ob der Benutzername bereits existiert
        if (userRepo.findByName(user.getName()) != null) {
            throw new IllegalArgumentException("A user with this name already exists.");
        }
        return userRepo.saveUser(user);
    }

    public String authenticateUser(String name, String passwordHash) throws SQLException {
        // Benutzer anhand des Namens suchen
        User existingUser = userRepo.findByName(name);
        if (existingUser == null || !existingUser.getPasswordHash().equals(passwordHash)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        // Generiere ein neues Session-Token
        String sessionToken = "authToken-" + UUID.randomUUID();
        userRepo.updateTocken(name, sessionToken);
        return sessionToken;
    }

    public Collection<User> getAllUsers() throws SQLException, IllegalAccessException {
        // Alle Benutzer abrufen
        return userRepo.findAllUsers();
    }

    public User getUserByName(String name) throws SQLException {
        // Benutzer anhand des Namens abrufen
        return userRepo.findByName(name);
    }
}
