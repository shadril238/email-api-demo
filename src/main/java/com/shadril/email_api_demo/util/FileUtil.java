package com.shadril.email_api_demo.util;

import com.shadril.email_api_demo.dto.FileDetails;
import com.shadril.email_api_demo.dto.request.FileAttachmentDto;
import com.shadril.email_api_demo.dto.response.FileAttachmentResponse;
import com.shadril.email_api_demo.exception.FileConversionException;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Component
public class FileUtil {

    public static FileDetails decodeBase64ToFileDetails(FileAttachmentDto dto) {
        if (dto == null || dto.getBase64ContentAsDataUri() == null) {
            throw new FileConversionException("Base64 content must be provided.");
        }

        // Extract mime type and base64 content from data URI
        String base64Content = dto.getBase64ContentAsDataUri();
        String mimeType;
        String actualBase64Data;

        if (!base64Content.startsWith("data:")) {
            throw new FileConversionException("Base64 content must be in data URI format");
        } else {
            try {
                // Split the data URI into header and content parts
                String[] parts = base64Content.split(",", 2);
                if (parts.length != 2) {
                    throw new FileConversionException("Invalid data URI format");
                }

                String header = parts[0];
                actualBase64Data = parts[1];

                mimeType = header.substring(5, header.indexOf(";base64"));

                MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
                String extension = allTypes.forName(mimeType).getExtension();

                if (!isValidBase64(actualBase64Data)) {
                    throw new FileConversionException("Base64 content is not valid");
                }

                byte[] data = decodeBase64Stream(actualBase64Data);
                return FileDetails.builder()
                        .fileName(dto.getFileNameWithoutExtension().concat(extension))
                        .mimeType(mimeType)
                        .size(data.length)
                        .resource(new ByteArrayResource(data))
                        .build();
            } catch (MimeTypeException e) {
                throw new FileConversionException("Invalid Mime type");
            } catch (Exception e) {
                throw new FileConversionException("Failed to parse data URI: " + e.getMessage());
            }
        }
    }

    public static FileAttachmentResponse encodeFileToBase64(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileConversionException("File must exist.");
        }

        byte[] fileContent;
        try {
            fileContent = file.getBytes();
        } catch (IOException e) {
            throw new FileConversionException(e.getMessage());
        }

        String base64Content = Base64.getEncoder().encodeToString(fileContent);
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        // Get filename without extension
        String fileNameWithoutExtension = fileName != null ?
                fileName.substring(0, fileName.lastIndexOf('.')) : null;

        // Create data URI format
        String dataUri = String.format("data:%s;base64,%s",
                contentType != null ? contentType : "application/octet-stream",
                base64Content);

        return FileAttachmentResponse.builder()
                .base64ContentAsDataUri(dataUri)
                .fileNameWithoutExtension(fileNameWithoutExtension)  // Use filename without extension
                .build();
    }

    private static boolean isValidBase64(String base64String) {
        try {
            Base64.getDecoder().decode(base64String);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] decodeBase64Stream(String base64Data) {
        try (InputStream inputStream = new ByteArrayInputStream(base64Data.getBytes());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Base64.Decoder decoder = Base64.getDecoder();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] decodedBytes = decoder.decode(new String(buffer, 0, bytesRead));
                outputStream.write(decodedBytes);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new FileConversionException("Error decoding Base64 data: " + e.getMessage());
        }
    }

}
