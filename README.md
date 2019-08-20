### Maven创建
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
### 5分钟从零构建第一个Flink应用
入口类,可以用来设置参数和创建数据源以及提交任务.
```java
StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
```
### Flink 零基础实战教程：如何计算实时热门商品    
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
|HotItems|运行类|
