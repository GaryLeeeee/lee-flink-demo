package com.garylee.lee_flink.hotItems_demo;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.io.PojoCsvInputFormat;
import org.apache.flink.api.java.typeutils.PojoTypeInfo;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by GaryLee on 2019-07-30 16:12.
 * 用来计算实时热门商品
 */
public class HotItems {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //用eventTime处理，指事件发生的时间(还用processingTime，指事件被处理的时间)
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        //为了不乱序，设置并发为1
        env.setParallelism(1);

        //读取'用户行为表'路径，因为没有真实环境就用离线文件吧
        URL fileUrl = HotItems.class.getClassLoader().getResource("UserBehavior.csv");
        Path filePath = Path.fromLocalFile(new File(fileUrl.toURI()));

        PojoTypeInfo<UserBehavior> pojoTypeInfo = (PojoTypeInfo<UserBehavior>)TypeExtractor.createTypeInfo(UserBehavior.class);

        //Java反射出来的字段是随机顺序的,所以需要显示排序~
        String[] fieldOrder = new String[]{"userId","itemId","categoryId","behavior","timestamp"};

        // 接收参数并生成pojo~
       PojoCsvInputFormat<UserBehavior> csvInput = new PojoCsvInputFormat<UserBehavior>(filePath,pojoTypeInfo,fieldOrder);

       env.createInput(csvInput,pojoTypeInfo)//创建数据源，得到DataStream
               //返回时间并生成watermark(水位线)->但超过end_time触发window的计算
               .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<UserBehavior>() {
                   @Override
                   public long extractAscendingTimestamp(UserBehavior userBehavior) {
                       return userBehavior.timestamp * 1000;
                   }
               })
               //过滤数据(这里只获取点击的数据)
               .filter(new FilterFunction<UserBehavior>() {
                   @Override
                   public boolean filter(UserBehavior userBehavior) throws Exception {
                       return userBehavior.behavior.equals("pv");
                   }
               })
               .keyBy("itemId")
               //每隔5分钟输出最近一小时内的数据
               //如[10:00,11:00),[10:05,11:05)
               .timeWindow(Time.minutes(60),Time.minutes(5))
               //聚合操作
               //第一个参数用于统计窗口中的条数(来一条加1)
               //第二个参数将每个key每个窗口聚合后的结果带上其他信息进行输出(此处我们将商品ID、窗口、点击量封装成了ItemViewCount)
               .aggregate(new CountAggregate(),new WindowResultFunction())
               .keyBy("windowEnd")
               .process(new TopNHotItems(3))//top3商品
               .print();

       //这里才是执行的位置
       env.execute("Hot Item Jobs");


    }

    public static class ItemViewCount {
        public long itemId;//商品ID
        public long windowEnd; //窗口结束时间戳
        public long viewCount;//商品的点击量

        public ItemViewCount(long itemId, long windowEnd, long viewCount) {
            this.itemId = itemId;
            this.windowEnd = windowEnd;
            this.viewCount = viewCount;
        }
    }

}
