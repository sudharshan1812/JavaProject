package com.smarttoll.dto;

public record AuthResponse(String token, String username, String role) {
}