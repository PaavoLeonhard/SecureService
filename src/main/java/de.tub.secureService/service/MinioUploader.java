package de.tub.secureService.service;

import java.io.InputStream;
import java.util.HashMap;
import io.minio.MinioClient;
import javax.crypto.*;


public class MinioUploader {
    private MinioClient minioClient;
    //private String endpoint = "http://localhost:9000";
    private String endpoint ="http://127.0.0.1:9000"; //"http://172.17.0.2:9000"; 172.17.0.2:9000
    //private String endpoint ="http://172.17.0.1:9000";
    //private String endpoint = "http://172.17.0.2:9000";
    private final String bucketName  = "examplebucks";
    private static HashMap<String, SecretKey> fileMapping = new HashMap<>();

    public MinioUploader() {
        String accessKey = System.getenv("MINIO_ACCESS_KEY"); //"Z0P3J8WHKIMQUCLZWLW9";//
        String secretKey = System.getenv("MINIO_SECRET_KEY");//"hs5cYWWMcgsbllIu2pOrSNp2SKofj6O3TGtwnaE3";
        try {
            // Create a minioClient with the Minio Server name, Port, Access key and Secret key.
            minioClient = new MinioClient(endpoint, accessKey, secretKey);
            //Check if the Bucket is already existing
            if(!minioClient.bucketExists(bucketName)) {
                minioClient.makeBucket(bucketName);
                System.out.println("MakeBucket");
            }

        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
        }
    }

    /**
     * Wrapper method for the put method of the minioClient. Maps the url to the responding key in a hashMap
     * @param objectName
     * @param input
     * @param size
     * @param contentType
     */
    public void insertObject (String objectName, InputStream input,long size, String contentType) {
        try {
            //Create a secret key to encrypt the file
            SecretKey key = KeyGenerator.getInstance("AES").generateKey();
            System.out.println("Insert bucket exist"+ minioClient.bucketExists(bucketName));
            fileMapping.put(objectName, key);
            minioClient.putObject(bucketName,objectName,input, size, contentType, key);

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
    public InputStream getObject(String objectName) {
        try {
            SecretKey key =fileMapping.get(objectName);
            return minioClient.getObject(bucketName, objectName, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}