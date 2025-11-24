package com.nanawally.ToDo_microservice.advice.exception;

public class TaskNotFoundException extends RuntimeException {
  public TaskNotFoundException(String message) {
    super(message);
  }
}
