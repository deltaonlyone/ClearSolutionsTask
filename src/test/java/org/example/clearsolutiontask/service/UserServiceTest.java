package org.example.clearsolutiontask.service;

import org.example.clearsolutiontask.dto.EditUserDto;
import org.example.clearsolutiontask.dto.GetUserDto;
import org.example.clearsolutiontask.exception.UserNotFoundException;
import org.example.clearsolutiontask.model.User;
import org.example.clearsolutiontask.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void createUser_ValidData_Success() {
        EditUserDto userDto = EditUserDto.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Street")
                .phoneNumber("1234567890")
                .build();

        assertDoesNotThrow(() -> userService.createUser(userDto));
        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    void patchUser_ValidData_Success() {
        Long userId = 1L;
        EditUserDto userDto = EditUserDto.builder()
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .birthDate(LocalDate.of(1995, 5, 5))
                .address("456 Avenue")
                .phoneNumber("9876543210")
                .build();
        User existingUser = User.builder()
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .birthDate(LocalDate.of(1995, 5, 5))
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.update(any(User.class))).thenReturn(existingUser);

        GetUserDto updatedUser = userService.patchUser(userId, userDto);

        assertNotNull(updatedUser);
        assertEquals(userDto.getEmail(), updatedUser.getEmail());
        assertEquals(userDto.getFirstName(), updatedUser.getFirstName());
        assertEquals(userDto.getLastName(), updatedUser.getLastName());
        assertEquals(userDto.getBirthDate(), updatedUser.getBirthDate());
        assertEquals(userDto.getAddress(), updatedUser.getAddress());
        assertEquals(userDto.getPhoneNumber(), updatedUser.getPhoneNumber());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).update(existingUser);
    }

    @Test
    void patchUser_UserNotFound_ThrowsException() {
        Long userId = 1L;
        EditUserDto userDto = EditUserDto.builder()
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .birthDate(LocalDate.of(1995, 5, 5))
                .address("456 Avenue")
                .phoneNumber("9876543210")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.patchUser(userId, userDto));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).update(any(User.class));
    }

    @Test
    void patchUser_NullUserDto_ThrowsException() {
        Long userId = 1L;
        assertThrows(UserNotFoundException.class, () -> userService.patchUser(userId, null));
    }

    @Test
    void patchUser_NullFields_Success() {
        Long userId = 1L;
        EditUserDto userDto = EditUserDto.builder().build();
        User existingUser = User.builder()
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .birthDate(LocalDate.of(1995, 5, 5))
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.update(any(User.class))).thenReturn(existingUser);

        GetUserDto updatedUser = userService.patchUser(userId, userDto);

        assertNotNull(updatedUser);
        assertEquals(existingUser.getEmail(), updatedUser.getEmail());
        assertEquals(existingUser.getFirstName(), updatedUser.getFirstName());
        assertEquals(existingUser.getLastName(), updatedUser.getLastName());
        assertEquals(existingUser.getBirthDate(), updatedUser.getBirthDate());
        assertEquals(existingUser.getAddress(), updatedUser.getAddress());
        assertEquals(existingUser.getPhoneNumber(), updatedUser.getPhoneNumber());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).update(existingUser);
    }

    @Test
    void patchUser_UpdatePhoneNumberInvalidFormat_Success() {
        Long userId = 1L;
        EditUserDto userDto = EditUserDto.builder()
                .phoneNumber("invalid-phone-number-format")
                .build();
        User existingUser = User.builder()
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .birthDate(LocalDate.of(1995, 5, 5))
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.update(any(User.class))).thenReturn(existingUser);

        GetUserDto updatedUser = userService.patchUser(userId, userDto);

        assertNotNull(updatedUser);
        assertEquals(userDto.getPhoneNumber(), updatedUser.getPhoneNumber());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).update(existingUser);
    }

    @Test
    void patchUser_UpdateOnlySpecificFields_Success() {
        Long userId = 1L;
        // Suppose we want to update only email and address
        EditUserDto userDto = EditUserDto.builder()
                .email("updated@example.com")
                .address("New Address")
                .build();
        User existingUser = User.builder()
                .email("updated@example.com")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.update(any(User.class))).thenReturn(existingUser);

        GetUserDto updatedUser = userService.patchUser(userId, userDto);

        assertNotNull(updatedUser);
        assertEquals(userDto.getEmail(), updatedUser.getEmail());
        assertNull(updatedUser.getFirstName()); // Ensure other fields are not updated
        assertNull(updatedUser.getLastName());
        assertNull(updatedUser.getBirthDate());
        assertEquals(userDto.getAddress(), updatedUser.getAddress());
        assertNull(updatedUser.getPhoneNumber());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).update(existingUser);
    }

    @Test
    void patchUser_UpdateAllFields_Success() {
        Long userId = 1L;
        // Suppose we want to update all fields
        EditUserDto userDto = EditUserDto.builder()
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .birthDate(LocalDate.of(1995, 5, 5))
                .address("456 Avenue")
                .phoneNumber("9876543210")
                .build();
        User existingUser = User.builder()
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .birthDate(LocalDate.of(1995, 5, 5))
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.update(any(User.class))).thenReturn(existingUser);

        GetUserDto updatedUser = userService.patchUser(userId, userDto);

        assertNotNull(updatedUser);
        assertEquals(userDto.getEmail(), updatedUser.getEmail());
        assertEquals(userDto.getFirstName(), updatedUser.getFirstName());
        assertEquals(userDto.getLastName(), updatedUser.getLastName());
        assertEquals(userDto.getBirthDate(), updatedUser.getBirthDate());
        assertEquals(userDto.getAddress(), updatedUser.getAddress());
        assertEquals(userDto.getPhoneNumber(), updatedUser.getPhoneNumber());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).update(existingUser);
    }

    @Test
    void deleteById_UserExists_DeletesUser() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        userService.deleteById(userId);

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }


    @Test
    void getUsersByBirthDateRange_ReturnsUserList() {
        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(1995, 12, 31);
        List<User> userList = new ArrayList<>();
        userList.add(User.builder().build());
        userList.add(User.builder().build());
        when(userRepository.findByBirthDateBetween(fromDate, toDate)).thenReturn(userList);

        List<User> result = userService.getUsersByBirthDateRange(fromDate, toDate);

        assertEquals(userList.size(), result.size());
        assertEquals(userList, result);
        verify(userRepository, times(1)).findByBirthDateBetween(fromDate, toDate);
    }

    @Test
    void updateUser_NonExistingUser_ThrowsUserNotFoundException() {
        Long userId = 1L;
        EditUserDto userDto = EditUserDto.builder()
                .email("updated@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .phoneNumber("1234567890")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, userDto));

        verify(userRepository).findById(userId);
    }
}
