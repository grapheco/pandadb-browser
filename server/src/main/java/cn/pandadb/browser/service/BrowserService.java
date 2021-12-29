package cn.pandadb.browser.service;

import java.util.Map;

import cn.pandadb.browser.VO.ExecuteCypherVo;

public interface BrowserService {
    Map<String, Object> executeCypher(ExecuteCypherVo executeCypherVo);

    Map<String,Object> getStatistics(ExecuteCypherVo executeCypherVo);

    Map<String, Object> getOtherRelationByNodeId(ExecuteCypherVo executeCypherVo);
}
