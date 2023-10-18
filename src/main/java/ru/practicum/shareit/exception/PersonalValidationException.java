package ru.practicum.shareit.exception;

public class PersonalValidationException extends RuntimeException {
    public PersonalValidationException(String message) {
        super(message);
    }
}