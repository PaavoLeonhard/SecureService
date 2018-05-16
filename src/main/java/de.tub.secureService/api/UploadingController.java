package de.tub.secureService.api;

import com.google.common.primitives.Bytes;
import de.tub.secureService.service.MinioUploader;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Collections;

@RestController
public class UploadingController {
    private int salt = 234545;

    /**
     * Takes the url of the file that is requested. Takes the fiel out  Minio and returns an outputStream
     * @param fileName
     * @param response
     */
    @RequestMapping(value = "/{file_name}", method = RequestMethod.GET)
    public void getFile(
            @PathVariable("file_name") String fileName,
            HttpServletResponse response) {
        try {
            MinioUploader uploader  =new MinioUploader();
            InputStream is = uploader.getObject(fileName);
            String test = new String(Bytes.toArray(Collections.singleton(is.read())));
            //byte [] test =IOUtils.toByteArray(is);
            System.out.println(test);
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file to output stream");
        }

    }


    /**
     *  Takes files that are uploaded to the service and stores them in Minio. Returns the url und which it can later be retrievevd
     * @param uploadingFiles
     * @return
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.POST, value= "/",consumes = "multipart/form-data")
    public String uploadingPost(@RequestParam(value = "type") MultipartFile[] uploadingFiles) throws IOException {
        //Convert the MultipartFile into a Stream
        InputStream completeStream = null;
        String contentType = null;

        if(uploadingFiles.length < 1){
            return null;
        }
        completeStream = uploadingFiles[0].getInputStream();
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
            messageDigest.update(Long.toBinaryString(System.currentTimeMillis() + salt).getBytes());
            byte[] hashName = messageDigest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashName) {
                sb.append(String.format("%02X", b));
            }
            String objectName= sb.toString().toLowerCase();
            //Create a MinioUploader and upload the file
            MinioUploader uploader = new MinioUploader();
            uploader.insertObject(objectName ,completeStream, size,"application/octet-stream");
            return objectName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}