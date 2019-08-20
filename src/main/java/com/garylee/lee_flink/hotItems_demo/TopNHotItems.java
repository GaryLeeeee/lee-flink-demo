package com.garylee.lee_flink.hotItems_demo;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by GaryLee on 2019-08-19 17:57.
 * 求某个窗口TopN的热门点击商品,key为窗口时间戳，输出为TopN字符串
 */
public class TopNHotItems extends KeyedProcessFunction<Tuple,ItemViewCount,String>{

    //自己设置TopN
    private final int topSize;

    public TopNHotItems(int topSize) {
        this.topSize = topSize;
    }

    //商品状态，等一个窗口数据都有时，再进行TopN
    private ListState<ItemViewCount> itemState;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        ListStateDescriptor<ItemViewCount> itemStateDesc = new ListStateDescriptor<ItemViewCount>(
                "itemState-state",ItemViewCount.class
        );
        itemState = getRuntimeContext().getListState(itemStateDesc);
    }

    @Override
    public void processElement(ItemViewCount input, Context context, Collector<String> collector) throws Exception {
        //将每条数据保存到状态(局部变量)中
        itemState.add(input);

        //注册windowEnd+1的EventTime Timer
        //运行到这里说明收齐了windowEnd窗口的所有商品数据
        context.timerService().registerEventTimeTimer(input.windowEnd + 1);

    }

    @Override
    /**
     * 定时返回TopN
     */
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
        //所有商品对象
        List<ItemViewCount> allItems = new ArrayList<>();
        for(ItemViewCount item : itemState.get()){
            allItems.add(item);
        }
        //返回一次后就可以清楚数据了，释放空间，以保存下一次窗口的数据
        itemState.clear();
        //按照点击量排序
        allItems.sort(new Comparator<ItemViewCount>() {
            @Override
            public int compare(ItemViewCount o1, ItemViewCount o2) {
                return (int)(o2.viewCount - o1.viewCount);
            }
        });
        //排序后格式化成String
        StringBuilder result = new StringBuilder();
        result.append("=============================\n");
        //-1是干啥的？？
        result.append("时间:").append(new Timestamp(timestamp-1)).append("\n");
        //获取前n大个商品
        for(int i=0;i<topSize;i++){
            ItemViewCount item = allItems.get(i);
            //No1: 商品ID=12345 浏览量=1234
            result.append("No").append(i+1).append(":")
                    .append(" 商品ID=").append(item.itemId)
                    .append(" 浏览量=").append(item.viewCount)
                    .append("\n");
        }
        result.append("=============================\n");

        //结果不要输出太快
        Thread.sleep(1000);
        //输出格式化的字符串
        out.collect(result.toString());
    }
}
