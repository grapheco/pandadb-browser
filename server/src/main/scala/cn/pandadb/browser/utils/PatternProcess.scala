package cn.pandadb.browser.utils

import cn.pandadb.browser.VO.PandadbConnectionInfo

import java.util
import java.util.regex.Pattern
import scala.collection.JavaConverters._
import scala.collection.mutable

/**
 * @program: TestClientWithStatistics
 * @description:
 * @author: LiamGao
 * @create: 2022-05-15 21:39
 */
object PatternProcess {
  val basicNodeLength = "match()returncount()".length
  val basicRelLength = "match()-[]->()returncount()".length


  def getDataByCypher(info: PandadbConnectionInfo): java.util.Map[String, Any] = {

    val retMap = new mutable.HashMap[String, Any]()
    val mapDataList = new mutable.ListBuffer[util.Map[String, Any]]()
    val aa = checkCypher(info.getCypher)

    val mode = aa.asInstanceOf[CypherMode]
    val isCountQuery = mode.isGoCount
    val schema = mode.schema


    val queryTool = new PandadbQueryTool(info)
    val columns = new mutable.ListBuffer[String]
    if (isCountQuery) { //count  query
      val isCountNode = schema.get("isCountNode").get

      if ("no".equals(isCountNode)) {//query rel count
        val relSchema = schema.get("relSchema").get
        val colKey = "count(" + relSchema + ")"
        val relTypeOption = mode.schema.get("retType")
        columns.append(colKey)

        val countMapData = new mutable.HashMap[String, String]()
        countMapData.put("type", "string")

        if (relTypeOption.isEmpty) { //查询所有rel数量
          val relCount = queryTool.getAllRelationShipCount()
          countMapData.put("data", relCount.toString)
        } else {
          val relCount = queryTool.getRelationShipCount(relTypeOption.get)
          countMapData.put("data", relCount.toString)
        }
        val data = new mutable.HashMap[String, Any]()
        data.put(colKey, countMapData.asJava)
        mapDataList.append(data.asJava)
      } else {// query node count
        val nodeSchema = schema.get("nodeSchema").get
        val colKey = "count(" + nodeSchema + ")"
        val nodeLabelOption = mode.schema.get("nodeLabel")
        columns.append(colKey)

        val countMapData = new mutable.HashMap[String, String]()
        countMapData.put("type", "string")

        if (nodeLabelOption.isEmpty) { //查询所有node数量
          val nodeCount = queryTool.getAllNodeCount()
          countMapData.put("data", nodeCount.toString)
        } else {
          val nodeCount = queryTool.getNodeCount(nodeLabelOption.get)
          countMapData.put("data", nodeCount.toString)
        }

        val data = new mutable.HashMap[String, Any]()
        data.put(colKey, countMapData.asJava)
        mapDataList.append(data.asJava)
      }
      retMap.put("mapData", mapDataList.asJava)
      retMap.put("columns", columns.asJava)

      val graph = new mutable.HashMap[String, Any]()
      graph.put("nodes", mutable.ListBuffer.empty.asJava)
      graph.put("relationships", mutable.ListBuffer.empty.asJava)

      val resultMap = new util.HashMap[String, Any]()
      resultMap.put("data", mutable.ListBuffer(mutable.HashMap(("graph", graph.asJava)).asJava).asJava)
      resultMap.put("columns", mutable.ListBuffer.empty.asJava)

      retMap.put("results", mutable.ListBuffer(resultMap).asJava)

      retMap.asJava
    } else {
      queryTool.getDataByCql(info.getCypher)
    }
  }


  def checkCypher(str: String): CypherMode = {
    val pstr = str.replace(" ", "").toLowerCase()

    val isRelation: Boolean = pstr.contains("""-[""")
    if (isRelation) {
      val left = pstr.indexOf("[")
      val right = pstr.indexOf("]", left)
      val relStr = pstr.slice(left + 1, right)
      val relAndType = relStr.split(":")
      checkIsRelationCountCypher(str, pstr, relAndType)
    }
    else {
      val left = pstr.indexOf("match(")
      val right = pstr.indexOf(")", left + 6)
      val nodeStr = pstr.slice(left + 6, right)
      val nodeAndLabel = nodeStr.split(":") // length = 1 or 2
      checkIsNodeCountCypher(str, pstr, nodeAndLabel)
    }
  }

  def checkIsRelationCountCypher(originCypher: String, noBlankLowerStr: String, array: Array[String]): CypherMode = {
    val startNodePattern = Pattern.compile("""\(.*\)-""")
    val endNodePattern = Pattern.compile("""->\(.*\) """)
    val start = startNodePattern.matcher(originCypher)
    val end = endNodePattern.matcher(originCypher)
    start.find()
    end.find()
    val l1 = getRelNodeLength(start.group())
    val l2 = getRelNodeLength(end.group())

    if (noBlankLowerStr.contains("count")) {
      if (array.length == 1) {
        val relSchema = array.head
        if (noBlankLowerStr.length == basicRelLength + relSchema.length * 2 + l1 + l2) {
          val trueNodeSchemaIndex = originCypher.toLowerCase().indexOf(s"$relSchema")
          val schema = originCypher.slice(trueNodeSchemaIndex, trueNodeSchemaIndex + relSchema.length)
          CypherMode(true, Map("isCountNode" -> "no", "relSchema" -> schema))

        }
        else CypherMode(false, Map.empty)
      }
      else {
        val relSchema = array.head
        val relType = array.last
        if (noBlankLowerStr.length == basicRelLength + relSchema.length * 2 + relType.length + 1 + l1 + l2) {
          val oc = originCypher.toLowerCase()
          val trueNodeSchemaIndex = oc.indexOf(s"$relSchema")
          val trueNodeLabelIndex = oc.toLowerCase().indexOf(s"$relType")
          val schema = originCypher.slice(trueNodeSchemaIndex, trueNodeSchemaIndex + relSchema.length)
          val label = originCypher.slice(trueNodeLabelIndex, trueNodeLabelIndex + relType.length)
          CypherMode(true, Map("isCountNode" -> "no", "relSchema" -> schema, "relType" -> label))
        }
        else CypherMode(false, Map.empty)
      }
    }
    else CypherMode(false, Map.empty)
  }

  def checkIsNodeCountCypher(originCypher: String, noBlankLowerStr: String, array: Array[String]): CypherMode = {
    if (noBlankLowerStr.contains("count")) {
      if (array.length == 1) {
        val nodeSchema = array.head
        if (noBlankLowerStr.length == basicNodeLength + nodeSchema.length * 2) {
          val trueNodeSchemaIndex = originCypher.toLowerCase().indexOf(s"$nodeSchema")
          val schema = originCypher.slice(trueNodeSchemaIndex, trueNodeSchemaIndex + nodeSchema.length)
          CypherMode(true, Map("isCountNode" -> "yes", "nodeSchema" -> schema))
        }
        else CypherMode(false, Map.empty)
      }
      else {
        val nodeSchema = array.head
        val nodeLabel = array.last
        if (noBlankLowerStr.length == basicNodeLength + nodeSchema.length * 2 + nodeLabel.length + 1) {
          val oc = originCypher.toLowerCase()
          val trueNodeSchemaIndex = oc.indexOf(s"$nodeSchema")
          val trueNodeLabelIndex = oc.toLowerCase().indexOf(s"$nodeLabel")
          val schema = originCypher.slice(trueNodeSchemaIndex, trueNodeSchemaIndex + nodeSchema.length)
          val label = originCypher.slice(trueNodeLabelIndex, trueNodeLabelIndex + nodeLabel.length)
          CypherMode(true, Map("isCountNode" -> "yes", "nodeSchema" -> schema, "nodeLabel" -> label))
        }
        else CypherMode(false, Map.empty)
      }
    }
    else CypherMode(false, Map.empty)
  }

  def getRelNodeLength(str: String): Int = {
    val left = str.indexOf("(")
    val right = str.indexOf(")")
    str.slice(left + 1, right).replace(" ", "").length
  }
}

case class CypherMode(isGoCount: Boolean, schema: Map[String, String]) {}
