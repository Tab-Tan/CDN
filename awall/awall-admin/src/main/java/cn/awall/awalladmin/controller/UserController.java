package cn.awall.awalladmin.controller;

import cn.awall.awalladmin.dao.UserMapper;
import cn.awall.awalladmin.dto.UserInfoDO;
import cn.awall.awalladmin.pojo.User;
import cn.awall.awalladmin.service.RedisLoginService;
import cn.awall.awalladmin.service.impl.UserServiceImpl;
import cn.awall.awalladmin.utils.*;
import cn.awall.awalladmin.vo.CommonResult;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@RestController
@CrossOrigin
@Slf4j
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private TxSmsTemplate txSmsTemplate;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ObjectMapper mapper;


    @Autowired
    private UserMapper userMapper;
    private String yzm_reg = "_yzm_reg";
    private String yzm_reset = "_yzm_reset";

    @PostMapping("/awall/tel")
    public String isUser(@RequestBody Object data) throws JsonProcessingException {

        Map<String,String> map = new ObjectMapper().readValue(JSON.toJSONString(data),Map.class);
        String tel = map.get("tel");
        if (!MobileUtils.isMobileNO(tel)) {
            return "手机号格式不正确！";
        }

        System.out.println("tel = " + tel);
        boolean user = userService.isUser(tel);
        System.out.println("user = " + user);
        if (user) {
            return "OK!";
        } else {
            return "账号已存在！";
        }
    }

    @RequestMapping("/awall/sms")
    public String sms(String tel, HttpSession session) {

//        检查无效手机号，减轻数据库负担，和验证码安全！
        if (!MobileUtils.isMobileNO(tel)) {
            return "手机号格式不正确！无法发送验证码！";
        }

//        检测账号是否存在，防止前台没有阻止用户使用已注册的手机号获取注册验证码！
        if (!userService.isUser(tel)) {
            return "账号已存在，不允许发送验证码!";
        }

        //生成六位数字随机验证码！
        String yzm = Integer.toString(100000 + (int) (Math.random() * ((999999 - 100000) + 1)));

        System.out.println("yzm = " + yzm);
        System.out.println("sms" + tel);

        //发送短信！
        String Msg = txSmsTemplate.sendMesModel(tel, yzm);

        System.out.println(Msg);

        //将手机号和验证码放到Redis中
        redisUtils.set(tel+yzm_reg, yzm, 300L);
        return "短信发送成功";
    }

    @PostMapping("/awall/register")
    public String register(@RequestBody Object token) throws JsonProcessingException {
        System.out.println("用户注册");
        Map<String, String> map = new ObjectMapper().readValue(JSON.toJSONString(token), Map.class);
        if (!map.get("code").equals(redisUtils.get(map.get("tel")+yzm_reg))) {
            return "验证码错误！";
        }
        User user = new User();
        user.setTel(map.get("tel"));
        user.setPassword(map.get("password"));
        user.setNikename(map.get("nikename"));
        System.out.println("register controller:"+user);
        boolean register = userService.register(user);
        if (register) {
            return "注册成功！";
        } else {
            return "注册失败！";
        }

    }


    @PostMapping("/awall/login")
    public CommonResult<String> login(@RequestBody Object object, HttpServletRequest request) throws JsonProcessingException {

        Map<String, String> map = mapper.readValue(JSON.toJSONString(object), Map.class);
        Cookie[] cookies = request.getCookies();
        User user = new User();
        String tel = map.get("tel");
        String password = map.get("password");

        user.setTel(tel);
        user.setPassword(password);
        if (!request.getSession().getAttribute("verifyCode").equals(map.get("code"))) {
            return new CommonResult<>(500, "验证码错误");
        } else {
            request.getSession().removeAttribute("verifyCode");
        }

        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(new UsernamePasswordToken(user.getTel(),user.getPassword()));
            // 将当前会话的手机号存一波
            Long userId = userMapper.findUserByTel(user.getTel()).getUserId();
            user.setUserId(userId);
            request.getSession().setAttribute("userId",String.valueOf(userId));
            return new CommonResult<>(200, "登录成功");
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return new CommonResult<>(500, "用户名或密码错误");
        }

    }

    /* 获取验证码图片*/

    @RequestMapping("/awall/getVerifyCode/{uuid}")
    public void getVerificationCode(HttpServletResponse response, HttpServletRequest request) {
        try {
            int width = 200;
            int height = 69;

            BufferedImage verifyImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            //生成对应宽高的初始图片

            String randomText = VerifyCode.drawRandomText(width, height, verifyImg);

            //单独的一个类方法，出于代码复用考虑，进行了封装。

            //功能是生成验证码字符并加上噪点，干扰线，返回值为验证码字符

            System.out.println("randomText = " + randomText);
            //将验证码存入session
            request.getSession().setAttribute("verifyCode",randomText);
            response.setContentType("image/png");//必须设置响应内容类型为图片，否则前台不识别

            OutputStream os = response.getOutputStream(); //获取文件输出流

            ImageIO.write(verifyImg, "png", os);//输出图片流

            os.flush();

            os.close();//关闭流

        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    @PostMapping("/awall/resettel")
    public String resetisUser(String tel) {

        if (!MobileUtils.isMobileNO(tel)) return "手机号格式不正确！";

        System.out.println("tel = " + tel);
        boolean user = userService.isUser(tel);
        System.out.println("user = " + user);
        if (user) return "该账号未注册！";
        else return "OK！";
    }

    @RequestMapping("/awall/resetsms")
    public String resetsms(String tel, HttpSession session) {

        //检查无效手机号，减轻数据库负担，和验证码安全！
        if (!MobileUtils.isMobileNO(tel)) {
            return "手机号格式不正确！无法发送验证码！";
        }

        //检测账号是否存在，防止前台没有阻止用户使用未注册的手机号获取获取验证码！
        if (userService.isUser(tel)) {
            return "账号不存在，不允许发送验证码!";
        }

        //生成六位数字随机验证码！
        String yzm = Integer.toString(100000 + (int) (Math.random() * ((999999 - 100000) + 1)));

        System.out.println("yzm = " + yzm);
        System.out.println("sms" + tel);

        //发送短信！
        String Msg = txSmsTemplate.sendMesModel(tel, yzm);

        System.out.println(Msg);

        //将手机号和验证码放到Redis中
        redisUtils.set(tel+yzm_reset, yzm, 300L);

        return "短信发送成功";
    }

    @RequestMapping("/awall/reset")
    public String reset(User user, String yzm) {

        //检查无效手机号，减轻数据库负担，和验证码安全！
        if (!MobileUtils.isMobileNO(user.getTel())) {
            return "手机号格式不正确！无法发送重置密码！";
        }

        //检测账号是否存在，防止前台没有阻止用户使用未注册的手机号获取获取验证码！
        if (userService.isUser(user.getTel())) {
            return "账号不存在，不允许重置密码!";
        }

        if (!yzm.equals(redisUtils.get(user.getTel()+yzm_reset))) {
            return "验证码错误！";
        }

        //校验成功后从redis中清除验证码！
//        redisUtils.del(user.getTel());

        boolean reset = userService.reset(user);

        if (reset) {
            return "修改成功！";
        } else {
            return "修改失败！";
        }
    }

    @SneakyThrows
    @GetMapping("/awall/user/getUser/{id}")
    public CommonResult<String> getUser(@PathVariable Long id){
        User user = userService.getUser(id);
        if (user!=null){
            UserInfoDO userInfoDO = new UserInfoDO(user.getUserId(), user.getAc(),user.getTel(), user.getHeadImg(), user.getNikename(), user.getHeadImg());
            String res = mapper.writeValueAsString(userInfoDO);
            return new CommonResult<>(200,res);
        }else {
            return new CommonResult<>(500,"此用户不存在");
        }
    }

    @GetMapping("/awall/logout")
    public boolean logout(HttpServletRequest request){
        try {
            String userId = (String) request.getSession().getAttribute("userId");
            if (userId!=null){
                request.getSession().removeAttribute("userId");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // 是否登录
    @GetMapping("/awall/isLogin")
    public String getLogin(HttpServletRequest request){
        String userId = "";

        try {
            userId = (String) request.getSession().getAttribute("userId");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "";
        }
        return userId;
    }


}
