package com.garylee.lee_flink.hotItems_demo;

import org.apache.flink.api.common.functions.AggregateFunction;

/**
 * 统计窗口中的条数
 */
public  class CountAggregate implements AggregateFunction<UserBehavior,Long,Long> {


    @Override
    public Long createAccumulator() {
        return 0L;
    }

    @Override
    public Long add(UserBehavior userBehavior, Long aLong) {
        //每次有新的数据就+1
        return aLong + 1;
    }

    @Override
    public Long getResult(Long aLong) {
        //返回结果
        return aLong;
    }

    @Override
    public Long merge(Long aLong, Long acc1) {
        return aLong+acc1;
    }
}