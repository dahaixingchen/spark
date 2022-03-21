package com.feifei.spark.streaming

import java.util

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, OffsetAndMetadata, OffsetCommitCallback}
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Duration, StreamingContext}

object StreamingExactlyOnce {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[3]").setAppName("manualOffset")
    //开启内部的被压机制，根据spark处理的速度有节奏的拉取Kafka的数据
    conf.set("spark.streaming.backpressure.enabled", "true")

    conf.set("spark.streaming.kafka.maxRatePerPartition", "300") //运行时状态每个分区拉取的条数
    conf.set("spark.streaming.receiver.maxRate", "3") //运行时状态，在2.3.4版本中不起作用

    //如果为true，Spark会StreamingContext在JVM关闭时正常关闭，而不是立即关闭。
    conf.set("spark.streaming.stopGracefullyOnShutdown", "true")

    val ssc = new StreamingContext(conf, Duration(1000))
    ssc.sparkContext.setLogLevel("error")

    var map: Map[String, Object] = Map(
      (ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "node01:9092"),
      ConsumerConfig.GROUP_ID_CONFIG -> "bula33",
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> "false",
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest"
    )

    val kafkaDStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream(ssc
      , LocationStrategies.PreferConsistent
      , ConsumerStrategies.Subscribe(List("ooxx")
        , map))

    kafkaDStream.map(recode => {
      val k: String = recode.key()
      val v: String = recode.value()
      val o: Long = recode.offset()
      val p: Int = recode.partition()
      (k, (v, o, p))
    }).print()

    //手动维护offset，实现精准一次消费
    kafkaDStream.foreachRDD(rdd => {
      val ranges: Array[OffsetRange] = rdd.asInstanceOf[HasOffsetRanges].offsetRanges


      //带有callback返回值的方式异步提交
      kafkaDStream.asInstanceOf[CanCommitOffsets].commitAsync(ranges, new OffsetCommitCallback {
        override def onComplete(offsets: util.Map[TopicPartition, OffsetAndMetadata], exception: Exception): Unit = {
          if (exception != null) {
            println("出现异常情况的处理。。。。")
          }
          if (offsets != null) {
            ranges.foreach(println)
            println("----------------------------")
            val iter: util.Iterator[TopicPartition] = offsets.keySet().iterator()
            while (iter.hasNext) {
              val k: TopicPartition = iter.next()
              val v: OffsetAndMetadata = offsets.get(k)
              println(s"partition：${k.partition()}...offset：${v.offset()} ...metadata：${v.metadata()}")

            }
          }
        }
      })

      //把数据收集到drive端，这步表示
      rdd.map(r=>(r.key(),r.value())).collect()

      /**
       * 开启事务：把上面的collect结果和下面的提交offset动作作为一个事务来提交
        * 提交事务
        * 提交offset
        * commit事务
       */
    })


    ssc.start()
    ssc.awaitTermination()
  }


}
