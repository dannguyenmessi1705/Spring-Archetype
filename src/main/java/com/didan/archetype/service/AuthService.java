package com.didan.archetype.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

public interface AuthService {
  boolean verifyToken(String token);

  boolean verifyTokenWReason(String token) throws SignatureException, InvalidKeySpecException,
      NoSuchAlgorithmException;

  Claims getClaims(String token);

  Claims getClaimsThrows(String token) throws SignatureException, InvalidKeySpecException, NoSuchAlgorithmException;

  Claims getClaimsNotVerifyToken(String token) throws JsonProcessingException;
}

