package de.tub.secureService.api;

import de.tub.secureService.service.MinioUploader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@RestController
public class UploadingController {
    //public static final String uploadingdir = System.getProperty("user.dir") + "/uploadingdir/";
    private int salt = 234545;

    /*
    @RequestMapping("/")
    public String uploading(Model model) {
        File file = new File(uploadingdir);
        model.addAttribute("files", file.listFiles());
        return "uploading";
    }
    */

    //
    @RequestMapping(method = RequestMethod.GET)
        public  void hallo(){
        System.out.println("GET angekommen");
    }


    //value = "/",
    @RequestMapping(method = RequestMethod.POST)
    public String uploadingPost(@RequestParam("uploadingFiles") MultipartFile[] uploadingFiles) throws IOException {
        System.out.println("POST angekommen");

        InputStream completeStream = null;
        String contentType = null;
        long size = 0;
        for(MultipartFile uploadedFile : uploadingFiles) {
            completeStream = new java.io.SequenceInputStream(completeStream, uploadedFile.getInputStream());
            uploadedFile.getContentType();
            size = size + uploadedFile.getSize();
        }
        //Hash the current Time to create a url and object name of the file
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(Long.toString(System.currentTimeMillis() + salt).getBytes());
            String objectName = new String(messageDigest.digest());


            MinioUploader uploader  =new MinioUploader();
            uploader.insertObject(objectName ,completeStream, size,contentType);


            String link = null;
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}