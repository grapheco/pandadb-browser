package cn.pandadb.browser.service.Impl;

import cn.pandadb.browser.VO.ExecuteCypherVo;
import cn.pandadb.browser.VO.PandadbConnectionInfo;
import cn.pandadb.browser.service.BrowserService;
import cn.pandadb.browser.utils.PandadbQueryTool;
import cn.pandadb.browser.utils.PatternProcess;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class BrowserServiceImpl implements BrowserService {

    @Override
    public Map<String, Object> executeCypher(ExecuteCypherVo executeCypherVo) {
        PandadbConnectionInfo info = new PandadbConnectionInfo(executeCypherVo);

        if (StringUtils.isEmpty(executeCypherVo.getPandadbUrl())) {
            throw new RuntimeException("pandadbUrl is null");
        }

        if (StringUtils.isEmpty(executeCypherVo.getCypher())) {
            throw new RuntimeException("cypher error");
        }

        long start = new Date().getTime();
        Map<String, Object> dataByCql = new HashMap<>();
        try {
            dataByCql = PatternProcess.getDataByCypher(info);
        } catch (Exception e) {
            e.printStackTrace();
            dataByCql.put("errors", e.getMessage());
        }

        long end = new Date().getTime();

        dataByCql.put("resultAvailableAfter", end - start);
        dataByCql.put("resultConsumedAfter", end - start);
        return dataByCql;
    }

    @Override
    public Map<String, Object> getStatistics(ExecuteCypherVo executeCypherVo) {

        if (StringUtils.isEmpty(executeCypherVo.getPandadbUrl())) {
            throw new RuntimeException("pandadbUrl is null");
        }

        PandadbConnectionInfo info = new PandadbConnectionInfo(executeCypherVo);
        PandadbQueryTool pandaQueryTool = new PandadbQueryTool(info);
        return pandaQueryTool.getStatistics();
    }

    @Override
    public Map<String, Object> getOtherRelationByNodeId(ExecuteCypherVo executeCypherVo) {
        String cypher = String.format("match (n)-[r]-(n2) where id(n) = %s return n,r,n2 limit 25" +
                "", executeCypherVo.getNodeId());
        executeCypherVo.setCypher(cypher);
        return executeCypher(executeCypherVo);
    }
}
