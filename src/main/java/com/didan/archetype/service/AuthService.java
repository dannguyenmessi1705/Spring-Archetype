package com.didan.archetype.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

public interface AuthService {
  boolean verifyToken(String token);

  boolean verifyTokenWReason(String token) throws SignatureException, InvalidKeySpecException,
      NoSuchAlgorithmException;

  Claims getClaims(String token);

  Claims getClaimsThrows(String token) throws SignatureException, InvalidKeySpecException, NoSuchAlgorithmException;

  Claims getClaimsNotVerifyToken(String token);
}

