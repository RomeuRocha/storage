package com.storage.storage.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.storage.storage.dto.ArchiveDTO;
import com.storage.storage.services.ArchiveService;

@RestController
@RequestMapping("/storage")
public class ArchiveController {

    @Autowired
    ArchiveService storageService;

    @PostMapping
    public ResponseEntity<ArchiveDTO> storage(@RequestParam MultipartFile file, @RequestParam String id) {

        ArchiveDTO dto = storageService.saveFile(file, id);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/new")
    public ArchiveDTO getId() {
        ArchiveDTO dto = storageService.newFolder();
        return dto;
    }

    @GetMapping(value = "/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable String id) {
        byte[] zipFile = storageService.download(id);

        if (zipFile == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + id + ".zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipFile);
    }

    @GetMapping(value = "/file/{id}")
    public List<ArchiveDTO> getFiles(@PathVariable String id) {
        System.out.println("está vindo aqui ?");
        List<ArchiveDTO> files = storageService.getFiles(id);
        return files;
    }

}
