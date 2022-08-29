package com.example.zhaicount;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.alibaba.fastjson.JSON;
import com.example.zhaicount.dao.TbZhaiInfoMapper;
import com.example.zhaicount.dto.GuResp;
import com.example.zhaicount.dto.ZhaiResp;
import com.example.zhaicount.entity.TbZhaiInfo;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;


public class test {

    @Autowired
    TbZhaiInfoMapper tbZhaiInfoMapper;

    static String detailCodeUrl = "https://datacenter-web.eastmoney.com/api/data/v1/get?reportName=RPT_F10_CORETHEME_CONTENT&columns=MAINPOINT_CONTENT&filter=";

    @Test
    public void test(String[] args) throws HttpRequestException, UnsupportedEncodingException{



    }
}

