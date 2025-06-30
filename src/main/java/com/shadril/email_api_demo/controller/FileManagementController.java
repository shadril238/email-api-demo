package com.shadril.email_api_demo.controller;

import com.shadril.email_api_demo.dto.FileDetails;
import com.shadril.email_api_demo.dto.request.FileAttachmentDto;
import com.shadril.email_api_demo.dto.response.FileAttachmentResponse;
import com.shadril.email_api_demo.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileManagementController {

    @PostMapping("/encode")
    public ResponseEntity<FileAttachmentResponse> encodeFileToBase64(@RequestParam(required = false, name = "file") MultipartFile multipartFile) {
        FileAttachmentResponse dto = FileUtil.encodeFileToBase64(multipartFile);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/decode")
    public ResponseEntity<ByteArrayResource> decodeAttachment(@RequestBody FileAttachmentDto file) {
        FileDetails fileDetails = FileUtil.decodeBase64ToFileDetails(file);
        return ResponseEntity.ok(fileDetails.getResource());
    }
}
