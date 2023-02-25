package com.storage.storage.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.storage.storage.dto.ArchiveDTO;
import com.storage.storage.entities.Archive;

@Service
public class ArchiveService {

    @Value("${app.path}")
    private String rootPath;

    public List<ArchiveDTO> getFiles(String id) {
        List<ArchiveDTO> listFiles = new ArrayList<>();
        File folder = new File(rootPath+"\\"+id);
        if (folder.exists()) {
            File[] files = folder.listFiles();

            for (File file : files) {
                //get data modified file for Brazil time
                Instant instant = Instant.ofEpochMilli(file.lastModified());
        
               
                ArchiveDTO dto = new ArchiveDTO(id, file.getName(), instant);
                listFiles.add(dto);
            }
           
        }
        return listFiles;
    }


    public byte[] download(String id) {
        File folder = new File(rootPath+"\\"+id);
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files.length > 0) {
                try {
                    return Files.readAllBytes(files[0].toPath());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    return null;
                }
            }
        }
        return null;
    }

    public ArchiveDTO newFolder(){
        String id = UUID.randomUUID().toString();
        File folder = new File(rootPath+"\\"+id);
        if (!folder.exists()) {
            folder.mkdir();
        }
        ArchiveDTO dto = new ArchiveDTO(id, rootPath+"\\"+id, Instant.now());

        return dto;
    }

    public ArchiveDTO saveFile(MultipartFile file, String id){
        if (file != null) {
            try {
                Archive archive = new Archive();
                archive.setContent(file.getBytes());
                archive.setPath( rootPath+"\\"+id+"\\"+file.getOriginalFilename());
                
                if(existsFolder( rootPath+"\\"+id)){
                    
                    processFile(archive);
                    ArchiveDTO dto = new ArchiveDTO(id, file.getOriginalFilename(), Instant.now());

                    return dto;
                }else{
                    //gerar IoException
                    throw new IOException("Folder not found");
                }
                    

                
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
        return null;
    }

    
    @Async("taskExecutor")
    public void processFile(Archive file) throws IOException {
        // get folder where file will be saved
        String pathDirectory = file.getPath().substring(0, file.getPath().lastIndexOf("\\"));
        // verify if folder exists
        File folder = new File(pathDirectory);

        if (folder.exists()) {
        
            Path path = Paths.get(file.getPath());
            Files.write(path, file.getContent());

        }

    }

    public Boolean existsFolder(String path){
        File folder = new File(path);
        if (folder.exists()) {
            return true;
        }
        return false;
    }


}
