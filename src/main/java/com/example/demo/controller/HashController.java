package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

@RestController
public class HashController {

    @GetMapping(value = "/hash")
    public ResponseEntity<String> getHash() throws NoSuchAlgorithmException, UnsupportedEncodingException {

        String message = "This is Your First and Last Name";
        String sha256Algorithm = "SHA-256";
        String sha512Algorithm = "SHA-512";

        MessageDigest sha256md = MessageDigest.getInstance(sha256Algorithm);
        sha256md.update(message.getBytes());
        byte[] hash256 = sha256md.digest();

        MessageDigest sha512md = MessageDigest.getInstance(sha512Algorithm);
        sha512md.update(message.getBytes());
        byte[] hash512 = sha512md.digest();

        StringBuilder hexString256 = new StringBuilder();
        StringBuilder hexString512 = new StringBuilder();

        for (byte b : hash256) {
            hexString256.append(Integer.toHexString(0xff & b));
        }
        for (byte b : hash512) {
            hexString512.append(Integer.toHexString(0xff & b));
        }

        String s = new StringBuilder()
                .append("Data: ")
                .append(message)
                .append("<br /><br />")
                .append(sha256Algorithm)
                .append(": CheckSum Value: ")
                .append(hexString256)
                .append("<br /><br />")
                .append(sha512Algorithm)
                .append(": CheckSum Value: ")
                .append(hexString512)
                .toString();

        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    @GetMapping(value = "/generate")
    public ResponseEntity<String> generate() throws NoSuchAlgorithmException, UnsupportedEncodingException {

        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        int randomLength = (int) (rnd.nextFloat() * SALTCHARS.length());
        while (salt.length() < randomLength) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        MessageDigest Sha256 = MessageDigest.getInstance("SHA-256");
        MessageDigest Sha512 = MessageDigest.getInstance("SHA-512");

        md5.update(saltStr.getBytes());
        Sha256.update(saltStr.getBytes());
        Sha512.update(saltStr.getBytes());

        byte[] md5_hash = md5.digest();
        byte[] sha256_hash = Sha256.digest();
        byte[] sha512_hash = Sha512.digest();

        StringBuilder hexStringMd5 = new StringBuilder();
        StringBuilder hexStringSha256 = new StringBuilder();
        StringBuilder hexStringSha512 = new StringBuilder();
        for (byte md5Hash : md5_hash) {
            hexStringMd5.append(Integer.toHexString(0xff & md5Hash));
        }
        for (byte sha256Hash : sha256_hash) {
            hexStringSha256.append(Integer.toHexString(0xff & sha256Hash));
        }
        for (byte sha512Hash : sha512_hash) {
            hexStringSha512.append(Integer.toHexString(0xff & sha512Hash));
        }

        String s = new StringBuilder()
                .append("Random String (Length " + randomLength + "):")
                .append("<br />")
                .append(saltStr)
                .append("<br /><br />")
                .append("MD5 Hash (Byte Length " + md5_hash.length + "):")
                .append("<br />")
                .append(hexStringMd5)
                .append("<br /><br />")
                .append("SHA 256 Hash (Byte Length " + sha256_hash.length + "):")
                .append("<br />")
                .append(hexStringSha256)
                .append("<br /><br />")
                .append("SHA 512 Hash (Byte Length " + sha512_hash.length + "):")
                .append("<br />")
                .append(hexStringSha512)
                .toString();

        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    @GetMapping("/md5")
    public ResponseEntity<String> getHashCollision() throws NoSuchAlgorithmException, IOException {

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

        // https://raw.githubusercontent.com/corkami/collisions/master/examples/collision1.png
        File file1 = new File("src/main/resources/static/collision1.png");

        // https://raw.githubusercontent.com/corkami/collisions/master/examples/collision2.png
        File file2 = new File("src/main/resources/static/collision2.png");

        byte[] fileContent1 = Files.readAllBytes(file1.toPath());
        byte[] fileContent2 = Files.readAllBytes(file2.toPath());

        byte[] md5_1 = md5.digest(fileContent1);
        byte[] md5_2 = md5.digest(fileContent2);

        byte[] sha256_1 = sha256.digest(fileContent1);
        byte[] sha256_2 = sha256.digest(fileContent2);

        if (Arrays.equals(md5_1, md5_2)) {
            System.out.println("Collision found!");
        } else {
            System.out.println("No collision found.");
        }

        StringBuilder hexStringmd5_1 = new StringBuilder();
        StringBuilder hexStringmd5_2 = new StringBuilder();

        for (byte b : md5_1) {
            hexStringmd5_1.append(Integer.toHexString(0xff & b));
        }
        for (byte b : md5_2) {
            hexStringmd5_2.append(Integer.toHexString(0xff & b));
        }

        StringBuilder hexString256 = new StringBuilder();
        StringBuilder hexString512 = new StringBuilder();

        for (byte b : sha256_1) {
            hexString256.append(Integer.toHexString(0xff & b));
        }
        for (byte b : sha256_2) {
            hexString512.append(Integer.toHexString(0xff & b));
        }

        String m1 = "MD5 Hash of message 1: " + hexStringmd5_1;
        String m2 = "MD5 Hash of message 2: " + hexStringmd5_2;

        String s1 = "SHA 256 Hash of message 1: " + hexString256;
        String s2 = "SHA 256 Hash of message 2: " + hexString512;
        StringBuilder sb = new StringBuilder()
                .append(m1)
                .append("<br />")
                .append(m2)
                .append("<br />")
                .append("<br />")
                .append(s1)
                .append("<br />")
                .append(s2);

        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }
}
