package org.example.clearsolutiontask.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ErrorDto {
    private final String message;
}
