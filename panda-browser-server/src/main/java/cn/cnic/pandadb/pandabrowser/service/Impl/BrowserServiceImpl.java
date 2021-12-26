package cn.cnic.pandadb.pandabrowser.service.Impl;

import cn.cnic.pandadb.pandabrowser.VO.ExecuteCypherVo;
import cn.cnic.pandadb.pandabrowser.VO.PandadbConnectionInfo;
import cn.cnic.pandadb.pandabrowser.service.BrowserService;
import cn.cnic.pandadb.pandabrowser.utils.PandaQueryTool;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class BrowserServiceImpl implements BrowserService {

    @Override
    public Map<String, Object> executeCypher(ExecuteCypherVo executeCypherVo) {
        PandadbConnectionInfo info = new PandadbConnectionInfo(executeCypherVo);

        if (StringUtils.isEmpty(executeCypherVo.getPandadbUrl())) {
            throw new RuntimeException("pandadbUrl is null");
        }

        PandaQueryTool pandaQueryTool = new PandaQueryTool(info);
        if (StringUtils.isEmpty(executeCypherVo.getCypher())) {
            throw new RuntimeException("cypher error");
        }
        Map<String, Object> dataByCql = pandaQueryTool.getDataByCql(executeCypherVo.getCypher());

        return dataByCql;
    }

    @Override
    public Map<String, Object> getStatistics(ExecuteCypherVo executeCypherVo) {

        if (StringUtils.isEmpty(executeCypherVo.getPandadbUrl())) {
            throw new RuntimeException("pandadbUrl is null");
        }

        PandadbConnectionInfo info = new PandadbConnectionInfo(executeCypherVo);
        PandaQueryTool pandaQueryTool = new PandaQueryTool(info);
        return pandaQueryTool.getStatistics2NewDriver(info);
    }

    @Override
    public Map<String, Object> getOtherRelationByNodeId(ExecuteCypherVo executeCypherVo) {
        String cypher = String.format("match (n)-[r]-(n2) where id(n) = %s return n,r,n2 limit 25" +
                "", executeCypherVo.getNodeId());
        executeCypherVo.setCypher(cypher);
        return executeCypher(executeCypherVo);
    }
}
