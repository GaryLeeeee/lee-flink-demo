package com.garylee.lee_flink.hotItems_demo;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * Created by GaryLee on 2019-08-19 17:32.
 * 输入窗口的结果
 */
public class WindowResultFunction implements WindowFunction<Long,ItemViewCount,Tuple,TimeWindow>{
    
    @Override
    /** 
    * @Description:
    * @key:窗口主键
    * @timeWindow:窗口
    * @iterable:窗口结果
    * @collector:输出结果为ItemViewCount
    * @Date: 2019/08/19 17:35
    */
    public void apply(Tuple key, TimeWindow timeWindow, Iterable<Long> iterable, Collector<ItemViewCount> collector) throws Exception {
        //元组tuple后面数字是多少就说明有几个值
        Long itemId = ((Tuple1<Long>) key).f0;
        Long count = iterable.iterator().next();
        collector.collect(new ItemViewCount(itemId,timeWindow.getEnd(),count));
    }
}
