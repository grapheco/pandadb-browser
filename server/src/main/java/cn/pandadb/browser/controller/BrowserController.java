package cn.pandadb.browser.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.pandadb.browser.VO.ExecuteCypherVo;
import cn.pandadb.browser.service.BrowserService;

@RestController
@RequestMapping("/pandadb/browser")
public class BrowserController {

    @Resource
    private BrowserService browserService;

    @PostMapping("/executeCypher")
    public Map<String, Object> executeCypher(ExecuteCypherVo executeCypherVo) {
        return browserService.executeCypher(executeCypherVo);
    }

    @PostMapping("/getOtherRelationByNodeId")
    public Map<String, Object> getOtherRelationByNodeId(ExecuteCypherVo executeCypherVo) {
        return browserService.getOtherRelationByNodeId(executeCypherVo);
    }

    @PostMapping("/login")
    public Map<String, Object> login(ExecuteCypherVo executeCypherVo) {
        return browserService.getStatistics(executeCypherVo);
    }
}
