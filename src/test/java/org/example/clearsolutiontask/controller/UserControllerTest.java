package org.example.clearsolutiontask.controller;

import org.example.clearsolutiontask.dto.EditUserDto;
import org.example.clearsolutiontask.model.User;
import org.example.clearsolutiontask.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;


    @Test
    void createUser_ValidUser_ReturnsCreated() throws Exception {

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"birthDate\":\"2002-04-27\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void createUser_UserUnderAge_ReturnsBadRequest() throws Exception {

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"testexample.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"birthDate\":\"2007-04-27\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserFields_ValidFields_ReturnsOk() throws Exception {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);


        EditUserDto user = EditUserDto.builder()
                .firstName("Doe")
                .lastName("John")
                .email("test@example.com")
                .birthDate(birthDate)
                .build();

        userService.createUser(user);

        System.out.println(userService.findById(1L));
        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jane\"}"))
                .andExpect(status().isOk());
        System.out.println(userService.findById(1L));
        User updatedUser = userService.findById(1L).orElseThrow();
        assertEquals("Jane", updatedUser.getFirstName());
    }

    @Test
    void updateUserFields_NonExistingUser_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/users/{userId}", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jane\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserFields_InvalidFields_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"invalid-email\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAllUserFields_ValidFields_ReturnsOk() throws Exception {

        LocalDate birthDate = LocalDate.of(1990, 1, 1);


        EditUserDto user = EditUserDto.builder()
                .firstName("Doe")
                .lastName("John")
                .email("test@example.com")
                .birthDate(birthDate)
                .build();
        userService.createUser(user);

        mockMvc.perform(put("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"updated@example.com\",\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"birthDate\":\"1990-01-01\"}"))
                .andExpect(status().isOk());


        User updatedUser = userService.findById(1L).orElseThrow();
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("Jane", updatedUser.getFirstName());
        assertEquals("Doe", updatedUser.getLastName());
        assertEquals("1990-01-01", updatedUser.getBirthDate().toString());
    }

    @Test
    void updateAllUserFields_NonExistingUser_ReturnsNotFound() throws Exception {
        mockMvc.perform(put("/users/{userId}", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"updated@example.com\",\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"birthDate\":\"1990-01-01\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAllUserFields_InvalidFields_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"invalid-email\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_ValidUser_ReturnsNotFound() throws Exception {


        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        EditUserDto user = EditUserDto.builder()
                .firstName("Doe")
                .lastName("John")
                .email("test@example.com")
                .birthDate(birthDate)
                .build();

        userService.createUser(user);

        mockMvc.perform(delete("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void deleteUser_NonExistingUser_ReturnsOk() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 100L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getUsersByBirthDateRange_ValidRange_ReturnsOk() throws Exception {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        LocalDate fromDate = LocalDate.of(1994, 1, 1);
        LocalDate toDate = LocalDate.of(2003, 1, 1);


        for (int i = 0; i < 5; i++) {
            birthDate.plusYears(5);

            EditUserDto user = EditUserDto.builder()
                    .firstName("Doe")
                    .lastName("John")
                    .email("test@example.com")
                    .birthDate(birthDate)
                    .build();
            userService.createUser(user);
        }

        mockMvc.perform(get("/users/search")
                        .param("from", fromDate.toString())
                        .param("to", toDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getUsersByBirthDateRange_InvalidRange_ReturnsBadRequest() throws Exception {
        LocalDate from = LocalDate.of(2003, 1, 1);
        LocalDate to = LocalDate.of(1990, 1, 1);

        mockMvc.perform(get("/users/search")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_ExistingUser_ReturnsUser() throws Exception {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        EditUserDto user = EditUserDto.builder()
                .firstName("Doe")
                .lastName("John")
                .email("test@example.com")
                .birthDate(birthDate)
                .build();
        userService.createUser(user);

        mockMvc.perform(get("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void getUserById_NonExistingUser_ReturnsUserNotFoundException() throws Exception {
        mockMvc.perform(get("/users/{userId}", 100L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
