package com.xywm.backend.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.xywm.backend.common.BusinessException;
import com.xywm.backend.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/file")
public class FileController {

    // 存放图片的专属文件夹路径
    private static final String UPLOAD_DIR = "E:/Projects/xywm/uploads/";

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 确保目录存在
        if (!FileUtil.exist(UPLOAD_DIR)) {
            FileUtil.mkdir(UPLOAD_DIR);
        }

        // 获取原文件名并生成新文件名（防止重名覆盖）
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = IdUtil.fastSimpleUUID() + ext;

        try {
            // 保存文件到本地磁盘
            file.transferTo(new File(UPLOAD_DIR + newFileName));
            // 返回可以在浏览器访问的URL路径
            String url = "http://localhost:8080/uploads/" + newFileName;
            return Result.success(url);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("文件上传失败");
        }
    }
}