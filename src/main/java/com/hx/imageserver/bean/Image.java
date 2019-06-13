package com.hx.imageserver.bean;

/**
 * @Author: AN
 * @Description: 定义图片对象，考虑到后期拓展问题
 * @Date: Created in 9:22 2019/6/13
 * @Modified by:
 */
public class Image {

    // 图片名称
    private String name;

    public Image(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
