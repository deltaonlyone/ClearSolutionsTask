package org.example.clearsolutiontask.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

import static org.example.clearsolutiontask.utils.ValidationConstants.EMAIL_REGEX;
import static org.example.clearsolutiontask.utils.ValidationConstants.PHONE_REGEX;

@Data
@Builder
@Jacksonized
public class EditUserDto {
    @NotNull
    @Email(regexp = EMAIL_REGEX, message = "Invalid email format")
    private String email;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @Past(message = "Birth date must be in the past")
    @NotNull
    private LocalDate birthDate;

    private String address;

    @Pattern(regexp = PHONE_REGEX)
    private String phoneNumber;
}
