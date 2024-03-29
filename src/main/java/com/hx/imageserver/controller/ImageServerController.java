package com.hx.imageserver.controller;

import com.hx.imageserver.bean.Image;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    @Value("${max.size.in.mb}")
    private int MAX_SIZE_IN_MB;

    // 文件存储目录
    public static final Path BASE_DIR = Paths.get(System.getProperty("user.home"), "Saved Images");

    // 判断目录是否存在，创建目录
    public ImageServerController() {
        System.out.println("图片路径:" + BASE_DIR.toString());
        File file = BASE_DIR.toFile();
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * @return
     * @throws IOException
     * @Description 获取所有的图片名称
     */
    @GetMapping("getImageNames")
    public @ResponseBody
    List<Image> getImageNames(@RequestParam("query") String query) throws IOException {

        // 过滤文件，只返回1M以内的照片
        DirectoryStream.Filter<java.nio.file.Path> filter = entry -> {
            boolean sizeOk = Files.size(entry) <= 1024 * 1024 * MAX_SIZE_IN_MB;
            boolean extensionOk = entry.getFileName().toString().endsWith("jpg") || entry.getFileName().toString().endsWith("png");
            return sizeOk && extensionOk;
        };

        // 将结果反馈
        List<Image> result = new ArrayList<>();
        if (StringUtils.isEmpty(query)) {
            for (Path entry : Files.newDirectoryStream(BASE_DIR, filter)) {
                result.add(new Image(entry.getFileName().toString()));
            }
        } else {
            // 模糊查询
            for (Path entry : Files.newDirectoryStream(BASE_DIR, filter)) {
                String fileName = entry.getFileName().toString();
                if (!fileName.contains(query)) {
                    continue;
                }
                result.add(new Image(entry.getFileName().toString()));
            }
        }
        return result;
    }

    /**
     * @param fileName
     * @return
     * @Description 删除照片
     */
    @GetMapping("removeImage")
    public @ResponseBody
    String removeImage(@RequestParam("name") String fileName) {
        Path dest = dest = BASE_DIR.resolve(fileName);
        try {
            Files.delete(dest);
            return "success";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }

    /**
     * @param fileName 下载文件名
     * @return
     * @throws IOException
     * @Description 图片下载
     */
    @GetMapping("downloadImage")
    public ResponseEntity<byte[]> downloadImage(@RequestParam("name") String fileName) throws IOException {
        Path dest = null;
        // 如果请求不带文件名
        if (fileName.indexOf(".jpg") == -1 && fileName.indexOf(".png") == -1) out:{
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

        }
        else {
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
     * @param file
     * @return
     * @Description 单个图片上传
     */
    @PostMapping(value = "uploadImage")
    public @ResponseBody
    String uploadImage(@RequestParam("file") MultipartFile file) {
        // 判断是否上传文件
        if (!file.isEmpty()) {
            // 判断图片大小
            if (file.getSize() > 1024 * 1024 * MAX_SIZE_IN_MB) {
                return "图片太大";
            }
            // 保存图片
            try {
                InputStream in = file.getInputStream();
                String fileName = file.getOriginalFilename();
                Files.copy(in, BASE_DIR.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                return "上传图片出错";
            }

        } else {
            return "请选择图片";
        }

        return "上传成功";
    }

    /**
     * @param files
     * @return
     * @Description 多个图片上传
     */
    @PostMapping(value = "uploadImages")
    public @ResponseBody
    String uploadImages(@RequestParam("files") MultipartFile[] files) {
        // 判断是否上传文件
        if (files != null && files.length != 0) {
            for (MultipartFile file : files) {
                // 获取图片名称
                String fileName = file.getOriginalFilename();
                // 保存图片
                try {
                    InputStream in = file.getInputStream();
                    Files.copy(in, BASE_DIR.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            return "请选择图片";
        }

        return "上传成功";
    }

}
