package com.garylee.lee_flink.count_demo;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

/**
 * Created by GaryLee on 2019-07-30 15:32.
 * 单词计数器
 */
public class SocketWindowWordCount {
    public static void main(String[] args) throws Exception {
        //创建environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //设置每一行是一段数据?
        DataStream<String> text = env.socketTextStream("localhost",9000,"\n");
        //解析数据
        //tuple2表示有2个参数的数组，tuple3则有三个...
        DataStream<Tuple2<String,Integer>> windowCounts = text
                .flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
                    @Override
                    public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
                        //将每一行按照中间的空格分割成单词(\s匹配空格)
                        for(String word:s.split("\\s")){
                            //of返回一个tuple2实例(直接new也可以)
                            //value1用来记载次数(每个word就是一次，用于后面的聚合)
                            collector.collect(Tuple2.of(word,1));
                        }
                    }
                }).keyBy(0)//数组第一个作为key(在这里是word),用来group by
                .timeWindow(Time.seconds(5))//五秒统计一次
                .sum(1);//将次数加起来，统计五秒内每个word出现次数

        //结果打印到控制台
        windowCounts.print().setParallelism(1);

        //加上这句才能执行
        env.execute("socket window wordCount ...");



    }
}
