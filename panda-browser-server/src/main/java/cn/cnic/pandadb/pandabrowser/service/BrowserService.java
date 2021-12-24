package cn.cnic.pandadb.pandabrowser.service;

import cn.cnic.pandadb.pandabrowser.VO.ExecuteCypherVo;

import java.util.Map;

public interface BrowserService {
    Map<String, Object> executeCypher(ExecuteCypherVo executeCypherVo);

    Map<String,Object> getStatistics(ExecuteCypherVo executeCypherVo);
}
