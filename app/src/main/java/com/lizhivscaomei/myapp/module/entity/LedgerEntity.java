package com.lizhivscaomei.myapp.module.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "ledger")
public class LedgerEntity {
    @Column(name = "id",isId = true,autoGen = true)
    private int id;
    @Column(name = "date", property = "NOT NULL")
    private String date;//日期
    @Column(name = "total_amount", property = "NOT NULL")
    private float totalAmount;//总金额
    @Column(name = "total_weight", property = "NOT NULL")
    private float totalWeight;//总重量
    private float price;//价格

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public float getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(float totalWeight) {
        this.totalWeight = totalWeight;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
