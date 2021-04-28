package cn.awall.awalladmin.controller;

import cn.awall.awalladmin.pojo.UFile;
import cn.awall.awalladmin.service.FileService;
import cn.awall.awalladmin.utils.IpUtil;
import cn.awall.awalladmin.vo.CommonResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin
@RestController
public class FileController {

    private Logger logger = LoggerFactory.getLogger(FileController.class);

    @Resource
    FileService fileService;

    @Resource
    private ObjectMapper mapper;

    @Resource
    Gson gson;

    @Value("${file.uploadPath}")
    private String realBasePath;

    @Value("${file.accessPath}")
    private String accessPath;

    @Value("${domain}")
    private String domain;

    // 上传栏目url
    @PostMapping("/awall/upload")
    public CommonResult<String> uploadImg(@RequestParam(required=false) MultipartFile file, HttpServletRequest req) throws IOException, ServletException {

        String imgSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf('.')+1);
        // 文件唯一的名字
        String fileName = UUID.randomUUID().toString() +"."+imgSuffix;
        Date todayDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(todayDate);
        // 域名访问的相对路径（通过浏览器访问的链接-虚拟路径）
        String saveToPath = accessPath + today + "/";
        // 真实路径，实际储存的路径
        String realPath = realBasePath + today + "/";
        // 储存文件的物理路径，使用本地路径储存
        String filepath = realPath + fileName;
        logger.info("上传图片名为：" + fileName+"--虚拟文件路径为：" + saveToPath +"--物理文件路径为：" + realPath);
        // 判断有没有对应的文件夹
        File destFile = new File(filepath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        file.transferTo(destFile);
        String url = domain+saveToPath+fileName;
        UFile uFile = new UFile(url, filepath);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        uFile.setUDate(date);
        uFile.setUsername(username);
        uFile.setIp(IpUtil.getIpAddr(req));
        fileService.add(uFile);
        uFile.setPath("/");
        String res = mapper.writeValueAsString(uFile);
        logger.info("上传文件、文件持久化成功!文件路径{}",res);
        // 返回虚拟路径，给链接访问
        return new CommonResult<>(200,res);
    }
    // 内容图片接口
    @PostMapping("/awall/paper/upload")
    //linux
    public String pageUpload(@RequestParam(required=false) MultipartFile file,HttpServletRequest req) throws FileNotFoundException {
        String imgSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf('.')+1);
        // 文件唯一的名字
        String fileName = UUID.randomUUID().toString()+"."+imgSuffix;
        Date todayDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(todayDate);
        // 域名访问的相对路径（通过浏览器访问的链接-虚拟路径）
            String saveToPath = accessPath + today + "/";
        // 真实路径，实际储存的路径
        String realPath = realBasePath + today + "/";
        // 储存文件的物理路径，使用本地路径储存
        String filepath = realPath + fileName;
        logger.info("上传图片名为：" + fileName+"--虚拟文件路径为：" + saveToPath +"--物理文件路径为：" + realPath);
        // 判断有没有对应的文件夹
        File destFile = new File(filepath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url = "http://101.201.125.13:80/awall/"+saveToPath+fileName;
        UFile uFile = new UFile(url, filepath);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        uFile.setUDate(date);
        uFile.setUsername(username);
        uFile.setIp(IpUtil.getIpAddr(req));
        fileService.add(uFile);
        logger.info("上传文件、文件持久化成功!文件路径{}",url);
        HashMap<String,Object> maps = new HashMap<>();
        maps.put("code",0);
        maps.put("msg","");
        HashMap<String, Object> map = new HashMap<>();
        map.put("src",url);
        maps.put("data",map);
        return gson.toJson(maps);
    }
    // 刪除圖片
    @PostMapping("file/del")
    public CommonResult<String> fileDel(@RequestBody Long id){

        UFile uFile = fileService.queryById(id);
        String path = uFile.getPath();
        File file = new File(path);

        if (file.exists()){
            boolean delete = file.delete();
            logger.info("刪除文件結果:{}",delete);
        }

        Integer integer = fileService.delById(id);

        if (integer==1){
            return new CommonResult<>(200,"成功删除["+integer+"]条数据。");
        }else {
            return new CommonResult<>(500,"删除失败!");
        }
    }

    //内容列表
    @GetMapping("/awall/file/list")
    public String getList(Integer page,Integer limit){

        int count = fileService.queryAll().size();
        List<UFile> uFiles = fileService.queryByLimit(page, limit);
        ArrayList<UFile> files = new ArrayList<>();
        for (UFile i : uFiles) {
            if (i.getUrl().startsWith("http")){
                files.add(i);
            }
        }

        HashMap<String,Object> res = new HashMap<>();
        res.put("code",0);
        res.put("count",count);
        res.put("msg","");
        ArrayList<UFile> list = new ArrayList<>(files);
        res.put("data",list);
        return gson.toJson(res);
    }
}
