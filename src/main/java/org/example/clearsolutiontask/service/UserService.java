package org.example.clearsolutiontask.service;

import lombok.RequiredArgsConstructor;
import org.example.clearsolutiontask.dto.EditUserDto;
import org.example.clearsolutiontask.dto.GetUserDto;
import org.example.clearsolutiontask.exception.UserNotFoundException;
import org.example.clearsolutiontask.exception.ValidationException;
import org.example.clearsolutiontask.model.User;
import org.example.clearsolutiontask.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${user.age.min}")
    private int minUserAge;

    private final UserRepository userRepository;


    public void createUser(EditUserDto userDto) {
        long age = ChronoUnit.YEARS.between(userDto.getBirthDate(), LocalDate.now());
        if (age < minUserAge) {
            throw new ValidationException("User must be older than 18 age");
        }

        User user = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .birthDate(userDto.getBirthDate())
                .address(userDto.getAddress())
                .phoneNumber(userDto.getPhoneNumber())
                .build();
        userRepository.save(user);
    }

    public GetUserDto patchUser(Long userId, EditUserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getFirstName() != null) {
            user.setFirstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null) {
            user.setLastName(userDto.getLastName());
        }
        if (userDto.getBirthDate() != null) {
            user.setBirthDate(userDto.getBirthDate());
        }
        if (userDto.getAddress() != null) {
            user.setAddress(userDto.getAddress());
        }
        if (userDto.getPhoneNumber() != null) {
            user.setPhoneNumber(userDto.getPhoneNumber());
        }
        user.setId(userId);
        user = userRepository.update(user);
        return GetUserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public GetUserDto updateUser(Long userId, EditUserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.setId(userId);
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setBirthDate(userDto.getBirthDate());
        user.setAddress(userDto.getAddress());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user = userRepository.update(user);
        return GetUserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public void deleteById(Long userId) {
        if (userRepository.existsById(userId)) {
            return;
        }
        userRepository.deleteById(userId);
    }

    public List<User> getUsersByBirthDateRange(LocalDate fromDate, LocalDate toDate) {
        return userRepository.findByBirthDateBetween(fromDate, toDate);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }


}
