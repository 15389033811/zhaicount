package com.example.zhaicount.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class YeResp {

    private Result result;
    private boolean success;
    private String message;

    public static class Result {

        private int pages;
        private List<Data> data;
        private int count;
        public void setPages(int pages) {
            this.pages = pages;
        }
        public int getPages() {
            return pages;
        }

        public void setData(List<Data> data) {
            this.data = data;
        }
        public List<Data> getData() {
            return data;
        }

        public void setCount(int count) {
            this.count = count;
        }
        public int getCount() {
            return count;
        }

        public static class Data {
            @JSONField(name = "SYFE")
            private Double SYFE;
            public void setSYFE(Double SYFE) {
                this.SYFE = SYFE;
            }
            public Double getSYFE() {
                return SYFE;
            }

        }
    }

}
