package de.tub.secureService.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.util.HashMap;

import io.minio.errors.*;
import org.xmlpull.v1.XmlPullParserException;

import io.minio.MinioClient;

import javax.crypto.*;


public class MinioUploader {
    private static MinioClient minioClient;
    private String endpoint = "http://localhost:9000";
    private String bucketName  = "Bucketname";
    private static HashMap<String, SecretKey> fileMapping = new HashMap<>();
    private String url = "loremIpsum";

    public MinioUploader() {
        // Create a minioClient with the Minio Server name, Port, Access key and Secret key.
        String accessKey = System.getenv("MINIO_ACCESS_KEY");
        String secretKey = System.getenv("MINIO_SECRET_KEY");
        try {
            minioClient = new MinioClient(endpoint, accessKey, secretKey);
        } catch (InvalidEndpointException e) {
            e.printStackTrace();
        } catch (InvalidPortException e) {
            e.printStackTrace();
        }
    }

    private void createBucket(String bucketName) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, RegionConflictException {
        boolean isExist = minioClient.bucketExists(bucketName);
        if (isExist) {
            System.out.println("Bucket already exists.");
        } else {
            minioClient.makeBucket(bucketName);
        }
    }

    public void insertObject (String objectName, InputStream input,long size, String contentType) {
        try {
            //Create a secret key to encrypt the file
            SecretKey key = KeyGenerator.getInstance("AES").generateKey();

            fileMapping.put(objectName, key);
            minioClient.putObject(bucketName,objectName,input, size, contentType, key);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertObject(String bucketName, String objectName, String filename) {
        try {
            minioClient.putObject(bucketName, objectName, filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void deleteObject(String bucketName, String objectName) {
        try {
            minioClient.removeObject(bucketName, objectName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void changeObject(String bucketName, String objectName, String filename) {
        try {
            minioClient.removeObject(bucketName, objectName);
            minioClient.putObject(bucketName, objectName, filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public InputStream getObject(String bucketName, String objectName) {
        try {
            return minioClient.getObject(bucketName, objectName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}