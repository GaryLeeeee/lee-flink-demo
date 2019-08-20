## Maven创建
`其中的groupId,artifactId,version,package可自定义`
```
mvn archetype:generate \
    -DarchetypeGroupId=org.apache.flink \
    -DarchetypeArtifactId=flink-quickstart-java \
    -DarchetypeVersion=1.6.1 \
    -DgroupId=my-flink-project \
    -DartifactId=my-flink-project \
    -Dversion=0.1 \
    -Dpackage=myflink \
    -DinteractiveMode=false
```
## 5分钟从零构建第一个Flink应用
入口类,可以用来设置参数和创建数据源以及提交任务.
```java
StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
```
## Flink 零基础实战教程：如何计算实时热门商品    
```
D:.                   
│  
├─src  
│  └─main  
│      ├─java.com.garylee.lee_flink  
│      │                    │  BatchJob.java  
│      │                    │  StreamingJob.java  
│      │                    │  
│      │                    ├─count_demo  
│      │                    │      SocketWindowWordCount.java  
│      │                    │  
│      │                    └─hotItems_demo  
│      │                            CountAggregate.java  
│      │                            HotItems.java  
│      │                            ItemViewCount.java  
│      │                            TopNHotItems.java  
│      │                            UserBehavior.java  
│      │                            WindowResultFunction.java  
│      │  
│      └─resources  
│              log4j.properties  
│              UserBehavior.csv  
│  
└─pom.xml  
```
|文件|作用|
| ------------- |:-------------:|
|UserBehavior|用户行为记录(pojo)|
|CountAggregate|实现AggregateFunction接口,统计窗口中的条数 |
|ItemViewCount| 记录商品点击量,方便排序|
|TopNHotItems|继承KeyedProcessFunction类,求某个窗口TopN的热门点击商品|
|WindowResultFunction|实现WindowFunction接口,输出窗口的结果|
|HotItems|启动类|
|BatchJob|批处理|
|StreamingJob|流处理|

## flink提供的函数等
### `TopNHotItems`继承`KeyedProcessFunction`    
* `ProcessFunction`是一个low-level api，用来实现更高级的功能。主要提供了定时器timer的功能(支持event-time和processing-time)    
* `processElement`每收到一个`input`，就会注册一个`windowEnd+1`定时器(flink会自动忽略同时间的重复注册)。     
    收到了`windowEnd+1`则意味着收到`windowEnd+1`的Watermark,即收齐了windowEnd下所有商品窗口统计值。      
* `onTimer`定时处理获取的信息生成TopN并格式化字符串输出。      
* `ListState`用来保存每个`input`的状态，保证状态数据的不丢失和完整性。      
### 启动类     
* `AscendingTimestampExtractor`通过这个实现抽取时间戳及生成watermark(水位线，代表这之前的数据都获取了)本例的数据是有序的，具体情况具体分析，可能无序。  
```java
DataStream<UserBehavior> timedData = dataSource
    .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<UserBehavior>() {
      @Override
      public long extractAscendingTimestamp(UserBehavior userBehavior) {
        // 原始数据单位秒，将其转成毫秒
        return userBehavior.timestamp * 1000;
      }
    });
```
* `FilterFunction`有时候只需要筛选某些重要数据，就需要过滤一下   
```java
DataStream<UserBehavior> pvData = timedData
    .filter(new FilterFunction<UserBehavior>() {
      @Override
      public boolean filter(UserBehavior userBehavior) throws Exception {
        // 过滤出只有点击的数据
        return userBehavior.behavior.equals("pv");
      }
    });
```
*   `CountAggregate`实现`AggregateFunction`接口来统计窗口中的数据   
    `WindowResultFunction`实现`WindowFunction`接口来输出窗口的结果   
    `.keyBy`是分组    
    `.timeWindow`设置滑动窗口(该例子就是每5分钟统计一小时的数据)   
    `.aggregate`做聚合 ，可以提前聚合掉数据，来减轻state的存储压力   
    `.apply`比起`.aggregate`会将窗口所有数据都存储下来    
    

```java
DataStream<ItemViewCount> windowedData = pvData
    .keyBy("itemId")
    .timeWindow(Time.minutes(60), Time.minutes(5))
    .aggregate(new CountAggregate(), new WindowResultFunction());
```

