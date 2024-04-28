package org.example.clearsolutiontask.repository;

import org.example.clearsolutiontask.model.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {

    private static final Map<Long, User> users = new ConcurrentHashMap<>();
    private static final AtomicLong userIdCounter = new AtomicLong(0);

    public void save(User user) {
        Long id = userIdCounter.incrementAndGet();
        user.setId(id);
        users.put(id, user);
    }

    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }


    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public boolean existsById(Long userId) {
        return users.containsKey(userId);
    }

    public void deleteById(Long userId) {
        users.remove(userId);
    }

    public List<User> findByBirthDateBetween(LocalDate fromDate, LocalDate toDate) {
        List<User> result = new ArrayList<>();
        for (User user : users.values()) {
            if (user.getBirthDate().isAfter(fromDate) && user.getBirthDate().isBefore(toDate)) {
                result.add(user);
            }
        }
        return result;
    }

}
