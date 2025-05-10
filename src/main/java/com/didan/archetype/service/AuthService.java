package com.didan.archetype.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

public interface AuthService {
  PrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException;

  PublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException;

  boolean verifyToken(String token);

  boolean verifyTokenWReason(String token) throws SignatureException, InvalidKeySpecException,
      NoSuchAlgorithmException;

  Claims getClaims(String token);

  Claims getClaimsThrows(String token) throws SignatureException, InvalidKeySpecException, NoSuchAlgorithmException;

  Claims getClaimsNotVerifyToken(String token) throws JsonProcessingException;

  String signToken(Map<String, Object> claims, String subject, String issuer, long seconds) throws NoSuchAlgorithmException, InvalidKeySpecException;

}

