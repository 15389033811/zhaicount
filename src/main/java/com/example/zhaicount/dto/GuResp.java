package com.example.zhaicount.dto;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

@Data
public class GuResp {
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


        @Data
        public static class Datum {
            @JSONField(name = "MAINPOINT_CONTENT")
            private String mainpointContent;

            @JSONField(name = "KEYWORD")
            private String keyWord;
        }
    }


}





