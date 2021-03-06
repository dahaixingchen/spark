package cn.spark.study.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext

/**
  * @author Administrator
  */
object WordCount {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setMaster("local[2]")
      .setAppName("WordCount")

    val ssc = new StreamingContext(conf, Seconds(5))

    val lines = ssc.socketTextStream("spark1", 9999)
    val words = lines.flatMap {
      _.split(" ")
    }
    val pairs = words.map { word => (word, 1) }
    val wordCounts = pairs.reduceByKey(_ + _)

    wordCounts.print()

    ssc.start()
    ssc.awaitTermination()
  }

}