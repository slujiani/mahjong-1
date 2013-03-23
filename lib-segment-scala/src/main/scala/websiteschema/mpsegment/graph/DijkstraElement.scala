package websiteschema.mpsegment.graph

import collection.mutable.TreeSet
import collection.mutable.HashSet

class Element(vertex: Int, distance: Int) extends Comparable[Element] {

  def getDistance = distance
  def getVertex = vertex

  override def compareTo(other: Element): Int = {
    if (distance == other.getDistance) {
      return if (vertex != other.getVertex) -1 else 0
    }
    return if (distance >= other.getDistance) 1 else -1
  }
}

object DijkstraElement {

  def apply(num: Int) = {
    val ele = new DijkstraElement();
    ele.size = num;
    ele.S = TreeSet[Int]()
    ele.reachedVertexSet = new TreeSet[Element]()
    ele.D = new Array[Int](ele.size)
    ele
  }
}

/**
 * "Dijkstra Shortest Path" defined 3 elements.
 */
class DijkstraElement {

  var S: TreeSet[Int] = null;
  // Set of vertexes which already found the shortest route.
  var reachedVertexSet: TreeSet[Element] = null
  var D: Array[Int] = null;
  // The distance of shortest routes.
  var size = 1024

  def init() {
    S.clear()
    reachedVertexSet.clear()
    for (i <- 0 until size) {
      D(i) = Int.MaxValue
    }
  }

  def hasFoundShortestPathTo(vertex: Int): Boolean = {
    return S.contains(vertex)
  }

  def reached(vertex: Int) {
    val ele = new Element(vertex, D(vertex))
    reachedVertexSet.add(ele)
  }

  def getDistanceOfPathTo(vertex: Int): Int = {
    return D(vertex)
  }

  def setDistanceOfPathTo(vertex: Int, distance: Int) {
    D(vertex) = distance
  }

  def foundShortestPathOf (vertex: Int) {
    S.add(vertex)
  }

  def findNewShortestPath(): Int = {
    val nextVertex = getResolvedVertex()
    if (nextVertex >= 0) {
      foundShortestPathOf(nextVertex)
    }
    return nextVertex
  }

  def getResolvedVertex(): Int = {
    if (!reachedVertexSet.isEmpty) {
      val ele = reachedVertexSet.head
      reachedVertexSet.remove(ele)
      return ele.getVertex
    }
    return -1
  }

}
