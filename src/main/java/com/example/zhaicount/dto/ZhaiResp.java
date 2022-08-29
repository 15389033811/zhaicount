package com.example.zhaicount.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ZhaiResp {
        private String version;
        private Result result;
        private boolean success;
        private String message;
        private long code;
        @Data
        public static class Result {
                private long pages;
                private Datum[] data;
                private long count;
            
                public long getPages() { return pages; }
                public void setPages(long value) { this.pages = value; }
            
                public Datum[] getData() { return data; }
                public void setData(Datum[] value) { this.data = value; }
            
                public long getCount() { return count; }
                public void setCount(long value) { this.count = value; }

                @Data
                public static class Datum {
                        //转债code
                        @JSONField(name = "SECURITY_CODE")
                        private String securityCode;

                        //转债code+市场
                        @JSONField(name = "SECUCODE")
                        private String secucode;
                        @JSONField(name = "SECURITY_NAME_ABBR")
                        private String securityNameAbbr;

                        //正股code
                        @JSONField(name = "CONVERT_STOCK_CODE")
                        private String convertStockCode;
                        @JSONField(name = "SECURITY_SHORT_NAME")
                        private String securityShortName;

                        //溢价率
                        @JSONField(name = "TRANSFER_PREMIUM_RATIO")
                        private Double transferPremiumRatio;

                        //上市日期
                        @JSONField(name = "LISTING_DATE")
                        private String listingDate;

}
            }
        
    
    }



