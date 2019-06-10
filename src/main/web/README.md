# 图片上传
采用 post 请求，可以直接参照 web 目录下的 upload.html 文件

# 图片下载
采用 get 请求，直接在浏览器中输入请求地址即可，name 是图片的名称，支持 jpg 和 png 格式
[下载图片](localhost:8188/imageServer/downloadImage?name=a3)

# 图片列表
在浏览器中输入第几即可：localhost:8188/imageServer/getImageNames