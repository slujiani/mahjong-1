package websiteschema.mpsegment.graph

trait IShortestPath {

  def getShortestPath(start: Int, end: Int): Path

  def setGraph(graph: IGraph): Unit

}