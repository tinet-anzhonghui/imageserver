package com.hx.imageserver.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: AN
 * @Description:图片服务器
 * @Date: Created in 9:11 2019/6/5
 * @Modified by:
 */
@Controller
@RequestMapping("/imageServer/")
public class ImageServerController {

    // 文件大小
    private static final int MAX_SIZE_IN_MB = 1;

    // 文件存储目录
    private static final Path BASE_DIR = Paths.get(System.getProperty("user.home"), "Documents", "Saved Images");

    /**
     * @Description 获取所有的图片名称
     * @return
     * @throws IOException
     */
    @RequestMapping("getImageNames")
    public @ResponseBody List<String> getImageNames() throws IOException {

        // 过滤文件
        DirectoryStream.Filter<java.nio.file.Path> filter = entry -> {
            boolean sizeOk = Files.size(entry) <= 1024 * 1024 * MAX_SIZE_IN_MB;
            boolean extensionOk = entry.getFileName().toString().endsWith("jpg") || entry.getFileName().toString().endsWith("png");
            return sizeOk && extensionOk;
        };

        // 将结果反馈
        List<String> result = new ArrayList<>();
        for (Path entry : Files.newDirectoryStream(BASE_DIR, filter)) {
            result.add(entry.getFileName().toString());
        }
        return result;
    }

    /**
     * @Description 图片下载
     * @param fileName 下载文件名
     * @return
     * @throws IOException
     */
    @GetMapping("downloadImage")
    public ResponseEntity<byte[]> downloadImage(@RequestParam("name") String fileName) throws IOException {
        Path dest = null;
        // 如果请求不带文件名
        if (fileName.indexOf(".jpg") == -1 && fileName.indexOf(".png") == -1) out : {
            // 尝试查找jpg
            String tempJpg = fileName + ".jpg";
            dest = BASE_DIR.resolve(tempJpg);
            if (Files.exists(dest)) {
                fileName = tempJpg;
                break out;
            }

            // 查实查找png
            fileName += ".png";
            dest = BASE_DIR.resolve(fileName);

        } else {
            dest = BASE_DIR.resolve(fileName);
        }

        // 判断文件是否存在
        if (!Files.exists(dest)) {
            throw new FileNotFoundException("image not found");
        }

        // 设置一些文件头的信息
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);

        // 反馈图片文件
        return new ResponseEntity<byte[]>(Files.readAllBytes(dest), headers, HttpStatus.CREATED);
    }

    /**
     * @Description 上传图片
     * @param file
     * @return
     */
    @PostMapping(value="uploadImage")
    public @ResponseBody  String uploadImage(@RequestParam("file") MultipartFile file){
        // 判断是否上传文件
        if (!file.isEmpty()) {
            // 判断图片大小
            if (file.getSize() > 1024 * 1024 * MAX_SIZE_IN_MB) {
                return "imgage is too big";
            }
            // 保存图片
            try {
                InputStream in = file.getInputStream();
                String fileName = file.getOriginalFilename();
                Files.copy(in, BASE_DIR.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }

        } else {
            return "no file";
        }

        return  "success";
    }

}
