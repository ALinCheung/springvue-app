package com.springvue.app.server.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadChunk {

    private Integer index;
    private Integer chunkCount;
    private MultipartFile chunk;
}
