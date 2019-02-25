package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by YangYang on 2019/2/23.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {
    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file,String path){
        String filename = file.getOriginalFilename();
        //获取扩展名
        String fileExtensionName = filename.substring(filename.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件，上传的文件名：{},上传的路径是:{},新文件名:{}"+filename,path,uploadFileName);

        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, uploadFileName);
        try {
            //这个文件已经上传成功了
            file.transferTo(targetFile);
            //然后将文件上传到ftp服务器上面
            FTPUtil.uploadFile(Lists.<File>newArrayList(targetFile));
            //上传到ftp服务器成功后，删除upload下面的文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件失败",e);
            return null;
        }
        return targetFile.getName();
    }

}
