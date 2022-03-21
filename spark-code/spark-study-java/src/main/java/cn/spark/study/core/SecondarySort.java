package cn.spark.study.core;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;

import scala.Tuple2;

import java.util.Arrays;

/**
 * 二次排序
 * 1、实现自定义的key，要实现Ordered接口和Serializable接口，在key中实现自己对多个列的排序算法
 * 2、将包含文本的RDD，映射成key为自定义key，value为文本的JavaPairRDD
 * 3、使用sortByKey算子按照自定义的key进行排序
 * 4、再次映射，剔除自定义的key，只保留文本行
 *
 * @author Administrator
 */
public class SecondarySort {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setAppName("SecondarySort")
                .setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);

        sc.setLogLevel("WARN");
        JavaRDD<String> lines = sc.textFile("D://sort.txt");

        JavaPairRDD<SecondarySortKey, String> pairs = lines.mapToPair(

                new PairFunction<String, SecondarySortKey, String>() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public Tuple2<SecondarySortKey, String> call(String line) throws Exception {
                        String[] lineSplited = line.split(" ");
                        SecondarySortKey key = new SecondarySortKey(
                                Integer.valueOf(lineSplited[0]),
                                Integer.valueOf(lineSplited[1]));
                        return new Tuple2<SecondarySortKey, String>(key, line);
                    }

                });

        JavaPairRDD<SecondarySortKey, String> sortedPairs = pairs.sortByKey();

        JavaRDD<String> sortedLines = sortedPairs.map(

                new Function<Tuple2<SecondarySortKey, String>, String>() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public String call(Tuple2<SecondarySortKey, String> v1) throws Exception {
                        return v1._2;
                    }

                });
//		JavaRDD<Tuple2<SecondarySortKey, String>> tuple2JavaRDD = sortedPairs.flatMap(new FlatMapFunction<Tuple2<SecondarySortKey, String>, Tuple2<SecondarySortKey, String>>() {
//			@Override
//			public Iterable<Tuple2<SecondarySortKey, String>> call(Tuple2<SecondarySortKey, String> v) throws Exception {
//				return Arrays.asList(v);
//			}
//		});
//		System.out.println("=================================");
//		tuple2JavaRDD.foreach(new VoidFunction<Tuple2<SecondarySortKey, String>>() {
//			public void call(Tuple2<SecondarySortKey, String> s) throws Exception {
//
//				System.out.println("SecondarySortKey的值" + s._1);
//				System.out.println("排序的值：" + s._2);
//				System.out.println(s._2());
//
//			}
//		});

        System.out.println("=================================");
        sortedLines.foreach(new VoidFunction<String>() {

            private static final long serialVersionUID = 1L;

            @Override
            public void call(String t) throws Exception {
                System.out.println(t);
            }

        });

        sc.close();
    }

}
