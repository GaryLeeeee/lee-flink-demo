package com.garylee.lee_flink.hotItems_demo;

/**
 * Created by GaryLee on 2019-07-30 16:17.
 * 用户行为记录
 */
public  class UserBehavior {
    long userId;        //用户ID
    long itemId;        //商品ID
    int categoryId;     //商品分类ID
    String behavior;    //用户行为，包括("pv","buy","cart","fav")，对应("点击","购买","加购","收藏")
    long timestamp;     //行为发生的时间戳(s)

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
