package com.storage.storage.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    System.out.println("download no arquivo ===================");
    System.out.println(rootPath + File.separator + id);
    
    File folder = new File(rootPath + File.separator + id);
    
    if (folder.exists() && folder.isDirectory()) {
        // Criação de um arquivo ZIP temporário
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            File[] files = folder.listFiles();
            
            // Verifica se há arquivos no diretório e se o array não é nulo
            if (files != null && files.length > 0) {
                for (File file : files) {
                    // Adiciona cada arquivo ao ZIP
                    if (file.isFile()) {
                        try (FileInputStream fis = new FileInputStream(file)) {
                            ZipEntry zipEntry = new ZipEntry(file.getName());
                            zos.putNextEntry(zipEntry);
                            
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fis.read(buffer)) >= 0) {
                                zos.write(buffer, 0, length);
                            }
                            zos.closeEntry();
                        }
                    }
                }
            } else {
                System.out.println("Nenhum arquivo encontrado no diretório.");
                return null;
            }
        } catch (IOException e) {
            System.out.println("Erro ao criar o ZIP: " + e.getMessage());
            return null;
        }
        
        // Retorna o conteúdo do ZIP
        return baos.toByteArray();
    } else {
        System.out.println("Diretório não encontrado ou não é um diretório válido.");
        return null;
    }
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
