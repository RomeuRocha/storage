package com.storage.storage.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class ArchiveDTO {
    
    private String id;
    
    private String name;
    
    private Instant moment;

}
