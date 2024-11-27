package at.fhtw.monsterTGame.service;

import at.fhtw.monsterTGame.model.User;
import at.fhtw.monsterTGame.persistence.ConnectionManager;
import at.fhtw.monsterTGame.persistence.repository.UserRepository;
import at.fhtw.monsterTGame.persistence.repository.UserRepositoryCustom;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class UserService {
    private final UserRepository userRepo;

    public UserService() {
        this.userRepo = new UserRepositoryCustom(new ConnectionManager());
    }

    public boolean addUser(User user) throws SQLException {
        if (userRepo.findUserByName(user.getUsername()) != null) {
            throw new IllegalArgumentException("A user with this name already exists.");
        }
        return userRepo.insertUser(user);
    }

    public String authenticateUser(String username, String password) throws SQLException {
        User existingUser = userRepo.findUserByName(username);
        if (existingUser == null || !existingUser.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        String sessionToken = "authToken-" + UUID.randomUUID();
        userRepo.updateUserToken(username, sessionToken);
        return sessionToken;
    }

    public List<User> getAllUsers() throws SQLException {
        return userRepo.fetchAllUsers();
    }

    public User getUserByUsername(String username) throws SQLException {
        return userRepo.findUserByName(username);
    }
}
