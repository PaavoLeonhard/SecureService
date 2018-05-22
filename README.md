# SecureService

This service takes a POST request with a MultipartFile in the body and returns an URL with which you can retrieve the file later. 
The file is stored encrypted in a Minio DB that only the service has access to. 
Whenever the service shuts down the file is lost since the key is only stored in memory. 
The file is retrieved by a simple GET request to the service with the URL that was returned earlier.
