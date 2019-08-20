package com.garylee.lee_flink.hotItems_demo;

/**
 * Created by GaryLee on 2019-08-19 17:32.
 * 商品点击量
 * 用来做热门商品的排序
 */
public class ItemViewCount {
    public long itemId;//商品ID
    public long windowEnd; //窗口结束时间戳
    public long viewCount;//商品的点击量

    public ItemViewCount(){}

    public ItemViewCount(long itemId, long windowEnd, long viewCount) {
        this.itemId = itemId;
        this.windowEnd = windowEnd;
        this.viewCount = viewCount;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(long windowEnd) {
        this.windowEnd = windowEnd;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }
}
