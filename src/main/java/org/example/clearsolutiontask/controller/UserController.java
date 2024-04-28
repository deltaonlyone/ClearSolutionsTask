package org.example.clearsolutiontask.controller;

import lombok.RequiredArgsConstructor;
import org.example.clearsolutiontask.dto.EditUserDto;
import org.example.clearsolutiontask.dto.GetUserDto;
import org.example.clearsolutiontask.exception.UserNotFoundException;
import org.example.clearsolutiontask.exception.ValidationException;
import org.example.clearsolutiontask.model.User;
import org.example.clearsolutiontask.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 2.1.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@Validated @RequestBody EditUserDto userDto) {
        userService.createUser(userDto);
    }

    // 2.2.
    @PatchMapping("/{userId}")
    public GetUserDto patchUser(@PathVariable Long userId, @RequestBody EditUserDto userDto) {
        return userService.patchUser(userId, userDto);
    }

    // 2.3.
    @PutMapping("/{userId}")
    public GetUserDto updateUser(@PathVariable Long userId, @Validated @RequestBody EditUserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    // 2.4.
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteById(userId);
    }

    // 2.5.
    @GetMapping("/search")
    public List<User> getUsersByBirthDateRange(@RequestParam("from") String fromDate, @RequestParam("to") String toDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate from = LocalDate.parse(fromDate, formatter);
        LocalDate to = LocalDate.parse(toDate, formatter);
        if (from.isAfter(to)) {
            throw new ValidationException("The 'from' date cannot be after the 'to' date");
        }
        return userService.getUsersByBirthDateRange(from, to);
    }

    @GetMapping("/{userId}")
    public User getUsersById(@PathVariable Long userId) {
        return userService.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }
}
