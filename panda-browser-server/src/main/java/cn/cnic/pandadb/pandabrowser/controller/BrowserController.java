package cn.cnic.pandadb.pandabrowser.controller;

import cn.cnic.pandadb.pandabrowser.VO.ExecuteCypherVo;
import cn.cnic.pandadb.pandabrowser.service.BrowserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/pandadb/browser")
public class BrowserController {

    @Resource
    private BrowserService browserService;

    @PostMapping("/executeCypher")
    public Map<String, Object> executeCypher(ExecuteCypherVo executeCypherVo) {
        return browserService.executeCypher(executeCypherVo);
    }

    @PostMapping("/login")
    public Map<String, Object> login(ExecuteCypherVo executeCypherVo) {
        return browserService.getStatistics(executeCypherVo);
    }
}
