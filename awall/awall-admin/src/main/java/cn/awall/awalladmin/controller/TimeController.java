package cn.awall.awalladmin.controller;

import cn.awall.awalladmin.pojo.Obj;
import cn.awall.awalladmin.service.impl.ObjServiceImpl;
import cn.awall.awalladmin.service.impl.TrainServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@Controller
public class TimeController {

    @Resource
    private TrainServiceImpl trainService;

    @Resource
    private ObjServiceImpl objServiceImpl;

    @RequestMapping("/awall/index")
    public String index() {
        return "index";
    }


    @RequestMapping("/awall/time")
    @ResponseBody
    public String time(String school, String entrance, String train, String obj) {
        System.out.println("school = " + school);
        System.out.println("entrance = " + entrance);
        System.out.println("train = " + train);
        System.out.println("obj = " + obj);

        Map time = new HashMap();

        if (trainService.isObj(school, train, entrance, obj)) {
            time = trainService.getTime(school, train, entrance, obj);
            Obj obj1 = objServiceImpl.queryById(obj);
            time.put("发展对象培训开始时间", obj1.getPstime());
            time.put("发展对象培训结束时间", obj1.getPbtime());
            time.put("发展对象座谈会时间", obj1.getZtime());
            time.put("团组织推优时间", obj1.getTtime());
            time.put("列为发展对象时间时间", obj1.getLtime());
            time.put("发展对象公示开始时间", obj1.getGstime());
            time.put("发展对象公示结束时间", obj1.getGbtime());
        } else time.put("msg", "您不符合发展要求！");

        return time.toString();
    }
}
