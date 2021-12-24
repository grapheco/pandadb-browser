package cn.cnic.pandadb.pandabrowser.utils;


import cn.cnic.pandadb.pandabrowser.VO.PandadbConnectionInfo;
import cn.pandadb.driver.PandaDriver;
import cn.pandadb.driver.PandaStatementResult;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PandaQueryTool {

    private Driver driver;
    private Session session;

    public PandaQueryTool(PandadbConnectionInfo info) {
        if (LocalCacheUtil.get(info.getPandadbUrl()) == null) {
            getDataSource(info);
        } else {
            this.session = (Session) LocalCacheUtil.get(info.getPandadbUrl());
            if (!this.session.isOpen()) {
                LocalCacheUtil.remove(info.getPandadbUrl());
                getDataSource(info);
            }
        }
    }

    private void getDataSource(PandadbConnectionInfo info) {

        Config config = Config.build()
                .withEncryption()
                .withConnectionTimeout(120, TimeUnit.SECONDS)
                .withMaxConnectionLifetime(60, TimeUnit.MINUTES)
                .withMaxConnectionPoolSize(2000)
                .withConnectionAcquisitionTimeout(20, TimeUnit.SECONDS)
                .toConfig();
        if (info.getUsername() == null) {
            info.setUsername("");
        }
        if (info.getPassword() == null) {
            info.setPassword("");
        }
        this.driver = GraphDatabase.driver(info.getPandadbUrl(), AuthTokens.basic(info.getUsername(), info.getPassword()), config);
        this.session = this.driver.session();
    }

    public Map<String, Object> getStatistics() {
        PandaDriver pandaDriver = (PandaDriver) driver;
        long nodeCount = pandaDriver.getStatistics().allNodes();
        long relCount = pandaDriver.getStatistics().allRelations();

        scala.collection.immutable.Map<String, Object> nodesCountByLabel = pandaDriver.getStatistics().nodesCountByLabel();
        Set<String> nodeLabels = new HashSet<>();
        nodesCountByLabel.keySet().foreach(var -> nodeLabels.add(var));

        scala.collection.immutable.Map<String, Object> relsCountByType = pandaDriver.getStatistics().relsCountByType();
        Set<String> relTypes = new HashSet<>();
        relsCountByType.keySet().foreach(var -> relTypes.add(var));

        Map<String, Object> ret = new HashMap<>();
        ret.put("nodeCount", nodeCount);
        ret.put("relCount", relCount);
        ret.put("nodeLabels", nodeLabels);
        ret.put("relTypes", relTypes);
        return ret;
    }

    public Map<String, Object> getDataByCql(String cypher) {

        Map<String, Object> ret = new HashMap<>();
        Map<String, Object> graph = new HashMap<>();

        List<Map<String, Object>> nodesList = new ArrayList<>();
        List<Map<String, Object>> relationsList = new ArrayList<>();
        List<Map<String, Object>> mapDataList = new ArrayList<>();
        Set<String> columns = new HashSet<>();

        StatementResult result = null;
        try {
            result = execute(cypher);

            PandaStatementResult pandaStatementResult = (PandaStatementResult) result;
            long resultAvailableAfter = pandaStatementResult.consume().resultAvailableAfter(TimeUnit.MILLISECONDS);
            long resultConsumedAfter = pandaStatementResult.consume().resultConsumedAfter(TimeUnit.MILLISECONDS);
            ret.put("resultAvailableAfter", resultAvailableAfter);
            ret.put("resultConsumedAfter", resultConsumedAfter);
        } catch (Exception e) {
            ret.put("errors", e.getMessage());
            return ret;
        }

        while (result.hasNext()) {
            Record record = result.next();
            Set<String> keys = new HashMap<>(record.asMap()).keySet();
            Map<String, Object> mapData = new HashMap<>();
            mapDataList.add(mapData);

            for (String key : keys) {
                try {
                    Path path = record.get(key).asPath();
                    List<Map<String, Object>> datasPath = new ArrayList<>();

                    Iterator<Path.Segment> iterator = path.iterator();
                    while (iterator.hasNext()) {
                        Path.Segment next = iterator.next();
                        Node start = next.start();
                        Map<String, Object> startMap = new HashMap<>();
                        startMap.put("id", "" + start.id());
                        List<String> startNodeLabels = new ArrayList<String>();
                        for (String label : start.labels()) {
                            startNodeLabels.add(label);
                            columns.add(label);
                        }
                        startMap.put("labels", startNodeLabels);
                        startMap.put("properties", start.asMap());
                        nodesList.add(startMap);
                        datasPath.add(start.asMap());

                        Relationship relationship = next.relationship();
                        Map<String, Object> rela = new HashMap<>();
                        rela.put("id", relationship.id());
                        rela.put("startNode", relationship.startNodeId());
                        rela.put("endNode", relationship.endNodeId());
                        rela.put("type", relationship.type());
                        rela.put("properties", relationship.asMap());
                        relationsList.add(rela);
                        datasPath.add(relationship.asMap());

                        Node end = next.end();
                        Map<String, Object> endMap = new HashMap<>();
                        endMap.put("id", "" + end.id());
                        List<String> endNodeLabels = new ArrayList<String>();
                        for (String label : end.labels()) {
                            endNodeLabels.add(label);
                            columns.add(label);
                        }
                        endMap.put("labels", endNodeLabels);
                        endMap.put("properties", end.asMap());
                        nodesList.add(endMap);
                        datasPath.add(end.asMap());
                    }

                    mapData.put(key, datasPath);
                } catch (Exception e1) {
                    try {
                        Node node = record.get(key).asNode();
                        Map<String, Object> nod = new HashMap<>();
                        nod.put("id", "" + node.id());
                        List<String> nodeLabels = new ArrayList<String>();
                        for (String label : node.labels()) {
                            nodeLabels.add(label);
                            columns.add(label);
                        }
                        nod.put("labels", nodeLabels);
                        nod.put("properties", node.asMap());
                        nodesList.add(nod);

                        mapData.put(key, node.asMap());
                    } catch (Exception e) {
                        mapData.put(key, record.get(key).asObject());
                    }
                }
            }
        }

        graph.put("nodes", filterListById(nodesList));
        graph.put("relationships", relationsList);
        Map<String, Object> data = new HashMap<>();
        data.put("graph", graph);

        Map<String, Object> resultMap = new HashMap<>();
        JSONArray datas = new JSONArray();
        datas.add(data);
        resultMap.put("data", datas);
        resultMap.put("columns", columns);

        JSONArray results = new JSONArray();
        results.add(resultMap);
        ret.put("results", results);
        ret.put("mapData", mapDataList);
        return ret;
    }

    /**
     * 过滤重复的node
     *
     * @param list
     * @return
     */
    private static List<Map<String, Object>> filterListById(List<Map<String, Object>> list) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        Map<String, Map<String, Object>> ma = new HashMap<>();
        list.forEach(var -> ma.put(var.get("id").toString(), var));
        return new ArrayList<>(ma.values());
    }


    /**
     * 　* @MethodName:     excute
     * 　* @Description:    执行Cypher查询
     */
    public StatementResult execute(String cql) {
        StatementResult result = session.run(cql);
        return result;
    }
}
