package cn.pandadb.browser.utils

import cn.pandadb.browser.VO.PandadbConnectionInfo

import java.util
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class PandadbQueryTool(connectInfo: PandadbConnectionInfo) {


  val host = connectInfo.getHost
  val port = connectInfo.getPort
  val client = new PandaClient(host, port)

  def getNodeCount(nodeName: String): Long = {
    val result = client.getStatistics().map(m => m.value.map(f => {
      (f._1, f._2.value.toString.toLong)
    }))
    val node = result.head
    node.get(nodeName).get
  }

  def getRelationShipCount(relationShipName: String): Long = {
    val result = client.getStatistics().map(m => m.value.map(f => {
      (f._1, f._2.value.toString.toLong)
    }))
    val relationship = result.last
    relationship.get(relationShipName).get
  }

  def getAllNodeCount(): Long = {
    var nodeCount = 0L

    val result = client.getStatistics().map(m => m.value.map(f => {
      (f._1, f._2.value.toString.toLong)
    }))
    val node = result.head
    node.foreach(f => {
      nodeCount += f._2.toString.toLong
    })
    nodeCount
  }

  def getAllRelationShipCount(): Long = {
    var relationShipCount = 0L
    val result = client.getStatistics().map(m => m.value.map(f => {
      (f._1, f._2.value.toString.toLong)
    }))
    val relationship = result.last
    relationship.foreach(f => {
      relationShipCount += f._2.toString.toLong
    })
    relationShipCount
  }

  def getStatistics(): util.Map[String, Any] = {
    val result = client.getStatistics().map(m => m.value.map(f => {
      (f._1, f._2.value.toString.toLong)
    }))
    val node = result.head
    val relationship = result.last

    val retJavaMap = new util.HashMap[String, Any]()
    val retNodeList = new ListBuffer[String]
    val retRelationShipList = new ListBuffer[String]
    var nodeCount = 0L
    var relationShipCount = 0L
    node.foreach(f => {
      retNodeList.append(f._1)
      nodeCount += f._2.toString.toLong
    })
    relationship.foreach(f => {
      retRelationShipList.append(f._1)
      relationShipCount += f._2.toString.toLong
    })

    retJavaMap.put("nodeCount", nodeCount)
    retJavaMap.put("relCount", relationShipCount)
    retJavaMap.put("nodeLabels", retNodeList.asJava)
    retJavaMap.put("relTypes", retRelationShipList.asJava)
    retJavaMap
  }

  def getDataByCql(cypher: String): util.Map[String, Any] = {
    val retMap = new util.HashMap[String, Any]


    val mapDataList = new ListBuffer[util.Map[String, util.Map[String, Any]]]
    val nodesList = new ListBuffer[util.Map[String, Any]]
    val relationsList = new ListBuffer[util.Map[String, Any]]
    val retColumns = new util.HashSet[String]()
    val columns = new util.HashSet[String]()

    val result = client.query(cypher)

    while (result.hasNext) {
      val lineMapData = new util.HashMap[String, util.Map[String, Any]]()
      mapDataList.append(lineMapData)

      val value = result.next()
      for ((k, v) <- value) {
        retColumns.add(k)
        if (v.isInstanceOf[LynxNode]) {
          val nodeMap = new mutable.HashMap[String, Any]()
          val mapData = new mutable.HashMap[String, Any]()

          val node = v.asInstanceOf[LynxNode]
          val id = node.id.value.asInstanceOf[LynxInteger].value
          val property = node.keys.map(k => (k.value -> node.property(k).get.value))

          val _property = property.map(kv => (kv._1, kv._2 match {
            case n: LynxList => seqAsJavaList(n.value.map(f => f.value))
            case n: List[LynxValue] => seqAsJavaList(n.map(f => f.value))
            case n: Map[String, LynxValue] => n.map(f => (f._1, f._2.value)).asJava
            case n: LynxValue => n.value
            case n => n.toString
          }))


          val propertyMap = new mutable.HashMap[String, Any]()
          _property.foreach(f => {
            propertyMap.put(f._1, f._2)
          })

          val label = node.labels.map(f => f.value)
          nodeMap.put("id", id)
          nodeMap.put("labels", label.asJava)
          nodeMap.put("properties", propertyMap.asJava)
          nodesList.append(nodeMap.asJava)

          label.foreach(f => columns.add(f))

          mapData.put("type", "object")
          mapData.put("data", propertyMap.asJava)
          lineMapData.put(k, mapData.asJava)

        } else if (v.isInstanceOf[LynxRelationship]) {
          val relaMap = new mutable.HashMap[String, Any]()
          val mapData = new mutable.HashMap[String, Any]()

          val relationship = v.asInstanceOf[LynxRelationship]
          val id = relationship.id.value.asInstanceOf[LynxValue].value
          val startId = relationship.startNodeId.value.asInstanceOf[LynxInteger].value
          val endId = relationship.endNodeId.value.asInstanceOf[LynxInteger].value
          val relationType = relationship.relationType.get.value
          val property = relationship.keys.map(k => (k.value -> relationship.property(k).get.value))


          val _property = property.map(kv => (kv._1, kv._2 match {
            case n: LynxList => seqAsJavaList(n.value.map(f => f.value))
            case n: List[LynxValue] => seqAsJavaList(n.map(f => f.value))
            case n: Map[String, LynxValue] => n.map(f => (f._1, f._2.value)).asJava
            case n: LynxValue => n.value
            case n => n.toString
          }))
          val propertyMap = new mutable.HashMap[String, Any]()
          _property.foreach(f => {
            propertyMap.put(f._1, f._2)
          })

          relaMap.put("id", id)
          relaMap.put("startNode", startId)
          relaMap.put("endNode", endId)
          relaMap.put("type", relationType)
          relaMap.put("properties", propertyMap.asJava)
          relationsList.append(relaMap.asJava)

          mapData.put("type", "object")
          mapData.put("data", propertyMap.asJava)
          lineMapData.put(k, mapData.asJava)
        } else {
          val mapData = new mutable.HashMap[String, Any]()

          mapData.put("type", "string")

          val data = v match {
            case n: LynxList => seqAsJavaList(n.value.map(f => f.value))
            case n: List[LynxValue] => seqAsJavaList(n.map(f => f.value))
            case n: Map[String, LynxValue] => n.map(f => (f._1, f._2.value)).asJava
            case n: LynxValue => n.value.toString
            case n => n.value.toString
          }
          mapData.put("data", data)

          lineMapData.put(k, mapData.asJava)
        }
      }
    }

    val graph = new mutable.HashMap[String, Any]()
    graph.put("nodes", filterListById(nodesList).asJava)
    graph.put("relationships", relationsList.asJava)

    val resultMap = new util.HashMap[String, Any]()
    resultMap.put("data", mutable.ListBuffer(mutable.HashMap(("graph", graph.asJava)).asJava).asJava)
    resultMap.put("columns", columns)

    retMap.put("results", mutable.ListBuffer(resultMap).asJava)
    retMap.put("columns", retColumns)
    retMap.put("mapData", mapDataList.asJava)

    retMap
  }

  def filterListById(nodes: ListBuffer[util.Map[String, Any]]) = {
    if (nodes == null || nodes.size == 0) {
      nodes
    }
    val map = new mutable.HashMap[String, Any]()
    nodes.foreach(node => {
      map.put(node.get("id").toString, node)
    })
    map.values
  }

}
