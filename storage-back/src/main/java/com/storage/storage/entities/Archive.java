package com.storage.storage.entities;

import lombok.Data;

@Data
public class Archive {

    private String path;

    private byte[] content;

}
