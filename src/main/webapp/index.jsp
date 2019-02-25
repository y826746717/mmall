<%@page language="java" import="java.util.*" contentType="text/html;charset=utf-8" %>
<html>
<body>
<h2>Hello World22222!</h2>

SpringMvc 上传文件测试
<form name="form1" action="/product/upload.do" method ="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="SpringMVC上传文件"/>

</form>

SpringMvc 富文本图片上传文件测试
<form name="form2" action="/product/richtext_img_upload.do" method ="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="富文本图片上传"/>

</form>
</body>
</html>
