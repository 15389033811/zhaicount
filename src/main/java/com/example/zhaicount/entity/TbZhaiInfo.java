package com.example.zhaicount.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author feiyyu
 * @since 2022-07-28
 */
@TableName("tb_zhai_info")
public class TbZhaiInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String guName;

    private String type;

    private String market;

    /**
     * 溢价率
     */
    private Double premiumRatio;

    /**
     * 上市日期
     */
    private String listingData;

    private String code;

    /**
     * 涨幅
     */
    private Double upRate;

    private Double balance;

    private String guCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGuName() {
        return guName;
    }

    public void setGuName(String guName) {
        this.guName = guName;
    }

    public String getGuCode() {
        return guCode;
    }

    public void setGuCode(String guCode) {
        this.guCode = guCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public Double getPremiumRatio() {
        return premiumRatio;
    }

    public void setPremiumRatio(Double premiumRatio) {
        this.premiumRatio = premiumRatio;
    }

    public String getListingData() {
        return listingData;
    }

    public void setListingData(String listingData) {
        this.listingData = listingData;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getUpRate() {
        return upRate;
    }

    public void setUpRate(Double upRate) {
        this.upRate = upRate;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "TbZhaiInfo{" +
                "id=" + id +
                ", name=" + name +
                ", guName=" + guName +
                ", type=" + type +
                ", market=" + market +
                ", premiumRatio=" + premiumRatio +
                ", listingData=" + listingData +
                ", code=" + code +
                ", upRate=" + upRate +
                ", balance=" + balance +
                "}";
    }
}
