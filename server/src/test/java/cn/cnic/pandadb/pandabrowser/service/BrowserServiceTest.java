package cn.cnic.pandadb.pandabrowser.service;

import cn.cnic.pandadb.pandabrowser.PandaBrowserApplication;
import cn.cnic.pandadb.pandabrowser.VO.ExecuteCypherVo;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@SpringBootTest(classes = PandaBrowserApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class BrowserServiceTest {
    @Autowired
    private BrowserService browserService;

    @Test
    public void getDataByCypherTest() {
        ExecuteCypherVo executeCypherVo = new ExecuteCypherVo();
        executeCypherVo.setPandadbUrl("panda://xxx:xx");
        executeCypherVo.setUsername("");
        executeCypherVo.setPassword("");
        executeCypherVo.setCypher("match (n:person)  return n");
        //executeCypherVo.setCypher("match (n:person) where n.name='google' return n");

        Map<String, Object> result =
                browserService.executeCypher(executeCypherVo);

        JSONObject jsonObject = new JSONObject(result);
        log.info(jsonObject.toJSONString());
    }

    @Test
    public void insertTest() {
        String cyphe = "create (n:person) set n.name = 'jl'";

        ExecuteCypherVo executeCypherVo = new ExecuteCypherVo();
        executeCypherVo.setPandadbUrl("10.0.82.148:9989");
        executeCypherVo.setUsername("");
        executeCypherVo.setPassword("");
        executeCypherVo.setCypher(cyphe);
        //executeCypherVo.setCypher("match (n:person) where n.name='google' return n");

        Map<String, Object> result =
                browserService.executeCypher(executeCypherVo);

        JSONObject jsonObject = new JSONObject(result);
        log.info(jsonObject.toJSONString());
    }


    @Test
    public void getStatisticsTest() {
        ExecuteCypherVo executeCypherVo = new ExecuteCypherVo();
        executeCypherVo.setPandadbUrl("panda://xxxx:xx");
        executeCypherVo.setUsername("");
        executeCypherVo.setPassword("");
        executeCypherVo.setCypher("match (n:person) where n.name='google' return n");
        Map<String, Object> result = browserService.getStatistics(executeCypherVo);
        JSONObject jsonObject = new JSONObject(result);
        log.info(jsonObject.toJSONString());
    }
}
