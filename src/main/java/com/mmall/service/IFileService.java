package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by YangYang on 2019/2/23.
 */
public interface IFileService {

    public String upload(MultipartFile file, String path);
}
