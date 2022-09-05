package com.example.zhaicount;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.zhaicount.dao.TbZhaiInfoMapper;
import com.example.zhaicount.dto.GuResp;
import com.example.zhaicount.dto.YeResp;
import org.assertj.core.data.MapEntry;
import org.springframework.web.server.WebFilter;
import com.example.zhaicount.dto.ZhaiResp;
import com.example.zhaicount.entity.TbZhaiInfo;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.tomcat.jni.Time;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.filechooser.FileSystemView;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootTest
class ZhaicountApplicationTests {
    @Autowired
    TbZhaiInfoMapper tbZhaiInfoMapper;

    static String detailCodeUrl = "https://datacenter-web.eastmoney.com/api/data/v1/get?reportName=RPT_F10_CORETHEME_CONTENT&columns=MAINPOINT_CONTENT&filter=";

    static String hexin = "A9-ZSzbOwIOgYMSfwuKuDs54aDhsRBM3TZA3wnEtebDVIPEmeRTDNl1oxwiC";


    @Test
    void contextLoads() throws Exception {
        Map<String, TbZhaiInfo> extendMapInfo = getexeclInfo();
        // Map<String, String> typeMap = getTypeInfo();
        Integer pageNumber = 1;
        String zhaiListUrl = "https://datacenter-web.eastmoney.com/api/data/v1/get?sortColumns=PUBLIC_START_DATE&sortTypes=-1&pageSize=100&pageNumber="
                + pageNumber
                + "&reportName=RPT_BOND_CB_LIST&columns=ALL&quoteColumns=f2~01~CONVERT_STOCK_CODE~CONVERT_STOCK_PRICE%2Cf235~10~SECURITY_CODE~TRANSFER_PRICE%2Cf236~10~SECURITY_CODE~TRANSFER_VALUE%2Cf2~10~SECURITY_CODE~CURRENT_BOND_PRICE%2Cf237~10~SECURITY_CODE~TRANSFER_PREMIUM_RATIO%2Cf239~10~SECURITY_CODE~RESALE_TRIG_PRICE%2Cf240~10~SECURITY_CODE~REDEEM_TRIG_PRICE%2Cf23~01~CONVERT_STOCK_CODE~PBV_RATIO&quoteType=0";
        HttpRequest respreq = HttpRequest.get(zhaiListUrl);
        System.out.println(respreq.code());
        // System.out.println(resp);
        String resp = respreq.body();
        ZhaiResp zhaiResp = JSON.parseObject(resp, ZhaiResp.class);

        for (pageNumber = 1; pageNumber <= zhaiResp.getResult().getPages(); pageNumber++) {
            zhaiListUrl = "https://datacenter-web.eastmoney.com/api/data/v1/get?sortColumns=PUBLIC_START_DATE&sortTypes=-1&pageSize=100&pageNumber="
                    + pageNumber
                    + "&reportName=RPT_BOND_CB_LIST&columns=ALL&quoteColumns=f2~01~CONVERT_STOCK_CODE~CONVERT_STOCK_PRICE%2Cf235~10~SECURITY_CODE~TRANSFER_PRICE%2Cf236~10~SECURITY_CODE~TRANSFER_VALUE%2Cf2~10~SECURITY_CODE~CURRENT_BOND_PRICE%2Cf237~10~SECURITY_CODE~TRANSFER_PREMIUM_RATIO%2Cf239~10~SECURITY_CODE~RESALE_TRIG_PRICE%2Cf240~10~SECURITY_CODE~REDEEM_TRIG_PRICE%2Cf23~01~CONVERT_STOCK_CODE~PBV_RATIO&quoteType=0";
            String pagePesp = HttpRequest.get(zhaiListUrl).body();
            zhaiResp = JSON.parseObject(pagePesp, ZhaiResp.class);
            System.out.println(zhaiResp.getMessage());
            String queryCode = "";
            for (ZhaiResp.Result.Datum zhaiRespItem : zhaiResp.getResult().getData()) {
                try {
                    // Thread.sleep(3000);
                    queryCode = zhaiRespItem.getConvertStockCode();
                    TbZhaiInfo tbZhaiInfo = new TbZhaiInfo();
                    tbZhaiInfo.setName(zhaiRespItem.getSecurityNameAbbr());
                    tbZhaiInfo.setGuName(zhaiRespItem.getSecurityShortName());
                    // tbZhaiInfo.setType(typeMap.get(queryCode));
                    // tbZhaiInfo.setType(getType(zhaiRespItem.getSecucode()));
                    tbZhaiInfo.setGuCode(zhaiRespItem.getConvertStockCode());
                    tbZhaiInfo.setCode(zhaiRespItem.getSecurityCode());
                    tbZhaiInfo.setPremiumRatio(zhaiRespItem.getTransferPremiumRatio());
                    tbZhaiInfo.setListingData(zhaiRespItem.getListingDate());
                    if (extendMapInfo.containsKey(tbZhaiInfo.getCode())) {
                        tbZhaiInfo.setBalance(extendMapInfo.get(tbZhaiInfo.getCode()).getBalance());
                        tbZhaiInfo.setUpRate(extendMapInfo.get(tbZhaiInfo.getCode()).getUpRate());
                    }
                    if (zhaiRespItem.getSecucode().indexOf(".SH") != -1) {
                        tbZhaiInfo.setMarket("沪");
                    } else {
                        tbZhaiInfo.setMarket("深");
                    }
                    tbZhaiInfoMapper.insert(tbZhaiInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // System.out.println(zhaiResp);
    }

    @Test
    void updateYue() throws Exception {
        Map<String, TbZhaiInfo> extendMapInfo = getexeclInfo();
        for (Map.Entry<String, TbZhaiInfo> entry : extendMapInfo.entrySet()) {
            tbZhaiInfoMapper.update(entry.getValue(), new LambdaQueryWrapper<TbZhaiInfo>().eq(TbZhaiInfo::getCode,entry.getKey()));
        }
    }

//
//    @Test
//    Double getYuE(String zhaiCode) throws UnsupportedEncodingException {
//        String yeUrl = "https://datacenter-web.eastmoney.com/api/data/v1/get?columns=ALL&sortColumns=date&sortTypes=2&source=WEB&reportName=RPTA_WEB_KZZ_LS&pageNumber=1&pageSize=1&filter=(";
//        yeUrl += URLEncoder.encode("zcode=\"" + zhaiCode + "\"", "utf-8") + ")";
//        String resp = HttpRequest.get(yeUrl).body();
//        YeResp yeResp = JSON.parseObject(resp, YeResp.class);
//        if (yeResp.getResult() == null) {
//            return -1D;
//        }
//        Integer pageEnd = yeResp.getResult().getPages();
//        yeUrl = "https://datacenter-web.eastmoney.com/api/data/v1/get?columns=ALL&sortColumns=date&sortTypes=2&source=WEB&reportName=RPTA_WEB_KZZ_LS&pageNumber="
//                + pageEnd + "&pageSize=1&filter=(";
//        yeUrl += URLEncoder.encode("zcode=\"" + zhaiCode + "\"", "utf-8") + ")";
//        resp = HttpRequest.get(yeUrl).body();
//        yeResp = JSON.parseObject(resp, YeResp.class);
//        Double reultNum = yeResp.getResult().getData().get(0).getSYFE();
//
//        if (reultNum == null) {
//            return -1D;
//        }
//        return reultNum / 100000000;
//    }

    @Test
    void setType(){
        List<TbZhaiInfo> tbZhaiInfoList = tbZhaiInfoMapper.selectList(new LambdaQueryWrapper<TbZhaiInfo>().isNull(TbZhaiInfo::getType).or().eq(TbZhaiInfo::getType,""));
        String x1 = "";
        System.out.println(tbZhaiInfoList.size());
        for (TbZhaiInfo e:
        tbZhaiInfoList) {
            e.setType(getType(e.getGuCode()));
            x1 = x1 +","+e.getGuCode();
            tbZhaiInfoMapper.updateById(e);
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }
        }

        System.out.println(x1);
    }

    @Test
    void setType1() throws IOException {
        JsonObject jsonObject = null;
        List<TbZhaiInfo> tbZhaiInfoList = tbZhaiInfoMapper.selectList(new LambdaQueryWrapper<TbZhaiInfo>().isNull(TbZhaiInfo::getType).or().eq(TbZhaiInfo::getType, ""));
//        List<TbZhaiInfo> tbZhaiInfoList = tbZhaiInfoMapper.selectList(new LambdaQueryWrapper<TbZhaiInfo>());
        String x = "";
        int num = 1;
        for (TbZhaiInfo tbZhaiInfo :
                tbZhaiInfoList) {
            if (x.split(",").length <= 15) {
                x += tbZhaiInfo.getGuCode() + ",";
                num++;
            } else {
                updateTypeStrs(x);
            }
        }
        updateTypeStrs(x);
    }

    void updateTypeStrs(String x) throws IOException {
        JsonObject jsonObject = null;
        x = x.substring(0, x.length() - 1);
        if (x.length()==0){
            return;
        }
        System.out.println("成功拼接"+x);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"question\": \"" + x + "\",\n    \"perpage\": 50,\n    \"page\": 1,\n    \"secondary_intent\": \"stock\",\n    \"log_info\": \"{\\\"input_type\\\":\\\"typewrite\\\"}\",\n    \"source\": \"Ths_iwencai_Xuangu\",\n    \"version\": \"2.0\",\n    \"query_area\": \"\",\n    \"block_list\": \"\",\n    \"add_info\": \"{\\\"urp\\\":{\\\"scene\\\":1,\\\"company\\\":1,\\\"business\\\":1},\\\"contentType\\\":\\\"json\\\",\\\"searchInfo\\\":true}\",\n    \"rsh\": \"594794277\"\n}");
        Request request = new Request.Builder()
                .url("http://www.iwencai.com/customized/chart/get-robot-data")
                .method("POST", body)
                .addHeader("Origin", "http://www.iwencai.com")
                .addHeader("Referer", "http://www.iwencai.com/unifiedwap/result?w=603609&querytype=stock&addSign=1661932545444")
                .addHeader("hexin-v", hexin)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        String respStr = response.body().string();
        jsonObject = new JsonParser().parse(respStr).getAsJsonObject();
        JsonObject zhaiTypeStr = jsonObject.get("data").getAsJsonObject().get("answer")
                .getAsJsonArray().get(0).getAsJsonObject().get("txt").getAsJsonArray().get(0)
                .getAsJsonObject().get("content").getAsJsonObject().get("components")
                .getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonObject();
        Set<String> zhaiKeySet = zhaiTypeStr.keySet();
        for (String zhaiKey :
                zhaiKeySet) {
            System.out.println(zhaiKey);
            if (zhaiTypeStr.get(zhaiKey).getAsJsonArray().get(0).getAsJsonObject().get("股票代码") == null
                    ||zhaiTypeStr.get(zhaiKey).getAsJsonArray().get(0).getAsJsonObject().get("所属概念") ==null){
                continue;
            }

            String gucode = zhaiTypeStr.get(zhaiKey).getAsJsonArray().get(0).getAsJsonObject().get("股票代码").getAsString();
            System.out.println("股票代码"+gucode.split("\\.")[0]);

            String type = zhaiTypeStr.get(zhaiKey).getAsJsonArray().get(0).getAsJsonObject().get("所属概念").getAsString();
            TbZhaiInfo tb = new TbZhaiInfo();
            tb.setType(type);

            tbZhaiInfoMapper.update(tb, new LambdaQueryWrapper<TbZhaiInfo>().eq(TbZhaiInfo::getGuCode, gucode.split("\\.")[0]));
        }

        x = "";
    }

    String getType(String guCode) {
        JsonObject jsonObject = null;
        try {
            System.out.println(guCode);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\n    \"question\": \""+guCode+"\",\n    \"perpage\": 50,\n    \"page\": 1,\n    \"secondary_intent\": \"stock\",\n    \"log_info\": \"{\\\"input_type\\\":\\\"typewrite\\\"}\",\n    \"source\": \"Ths_iwencai_Xuangu\",\n    \"version\": \"2.0\",\n    \"query_area\": \"\",\n    \"block_list\": \"\",\n    \"add_info\": \"{\\\"urp\\\":{\\\"scene\\\":1,\\\"company\\\":1,\\\"business\\\":1},\\\"contentType\\\":\\\"json\\\",\\\"searchInfo\\\":true}\",\n    \"rsh\": \"594794277\"\n}");
            Request request = new Request.Builder()
                    .url("http://www.iwencai.com/customized/chart/get-robot-data")
                    .method("POST", body)
                    .addHeader("Origin", "http://www.iwencai.com")
                    .addHeader("Referer", "http://www.iwencai.com/unifiedwap/result?w=603609&querytype=stock&addSign=1661932545444")
                    .addHeader("hexin-v", hexin)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            String respStr = response.body().string();
            jsonObject = new JsonParser().parse(respStr).getAsJsonObject();
            String zhaiTypeStr = jsonObject.get("data").getAsJsonObject().get("answer")
                    .getAsJsonArray().get(0).getAsJsonObject().get("txt").getAsJsonArray().get(0)
                    .getAsJsonObject().get("content").getAsJsonObject().get("components")
                    .getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonArray().get(0)
                    .getAsJsonObject().get("所属概念").getAsString();
            System.out.println(zhaiTypeStr);
            return zhaiTypeStr;
        } catch (Exception e) {
            return "";
        }
    }

    Map<String, String> getTypeInfo() throws Exception {
        Integer page = 1;
        JsonObject jsonObject = null;
        HashMap<String, String> mapInfo = new HashMap<>();
        for (page = 1; page <= 50; page++) {

            Thread.sleep(5000);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType,
                    "{\r\n    \"question\": \"所属概念\",\r\n    \"perpage\": 100,\r\n    \"page\": " + page
                            + ",\r\n    \"secondary_intent\": \"stock\",\r\n    \"log_info\": \"{\\\"input_type\\\":\\\"click\\\"}\",\r\n    \"source\": \"Ths_iwencai_Xuangu\",\r\n    \"version\": \"2.0\",\r\n    \"query_area\": \"\",\r\n    \"block_list\": \"\",\r\n    \"add_info\": \"{\\\"urp\\\":{\\\"scene\\\":1,\\\"company\\\":1,\\\"business\\\":1},\\\"contentType\\\":\\\"json\\\",\\\"searchInfo\\\":true}\",\r\n    \"rsh\": \"Ths_iwencai_Xuangu_gg9mwx2kqn7vb4uhzq0g2is0ymq3ilwg\"\r\n}");
            Request request = new Request.Builder()
                    .url("http://www.iwencai.com/customized/chart/get-robot-data")
                    .method("POST", body)
                    .addHeader("Origin", "http://www.iwencai.com")
                    .addHeader("Referer",
                            "http://www.iwencai.com/unifiedwap/result?w=%E6%89%80%E5%B1%9E%E6%A6%82%E5%BF%B5&querytype=stock")
                    .addHeader("Cookie",
                            "ta_random_userid=fpk9l3fjna; WafStatus=0; cid=7d2495c2cac32ab307c01fdbcecf02cd1656166981; ComputerID=7d2495c2cac32ab307c01fdbcecf02cd1656166981; other_uid=Ths_iwencai_Xuangu_gg9mwx2kqn7vb4uhzq0g2is0ymq3ilwg; PHPSESSID=4f4f92ebe29c21cc4e9cdb87aec3b6e5; iwencaisearchquery=%E5%8F%AF%E4%BA%A4%E6%98%93%E7%9A%84%E5%8F%AF%E8%BD%AC%E5%80%BA; wencai_pc_version=1; v=Aw5dwcx64XczQlVTTzPS15UBX-_Vj9KJ5FOGbThXepHMm6BRoB8imbTj1ngL")
                    .addHeader("hexin", "Aw5dwcx64XczQlVTTzPS15UBX-_Vj9KJ5FOGbThXepHMm6BRoB8imbTj1ngL")
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();
            String respStr = response.body().string();
            try {
                jsonObject = new JsonParser().parse(respStr).getAsJsonObject();
            } catch (Exception e) {
                System.out.println("------" + page);
                System.out.println(e.toString());
                System.out.println(respStr);
                page--;
                continue;
            }
            JsonArray zhaiJsonList = jsonObject.get("data").getAsJsonObject().get("answer")
                    .getAsJsonArray().get(0).getAsJsonObject().get("txt").getAsJsonArray().get(0)
                    .getAsJsonObject().get("content").getAsJsonObject().get("components")
                    .getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonObject().get("datas")
                    .getAsJsonArray();
            for (JsonElement jsonElement : zhaiJsonList) {
                if (jsonElement.getAsJsonObject().get("code") == null
                        || jsonElement.getAsJsonObject().get("所属概念") == null) {
                    continue;
                }
                System.out.println(jsonElement.getAsJsonObject().get("code").getAsString());
                System.out.println(jsonElement.getAsJsonObject().get("所属概念").getAsString());
                mapInfo.put(jsonElement.getAsJsonObject().get("code").getAsString(),
                        jsonElement.getAsJsonObject().get("所属概念").getAsString());
            }
        }
        System.out.println(mapInfo.size());
        return mapInfo;
    }

    Map<String, TbZhaiInfo> getexeclInfo() throws Exception {
        Integer page = 1;
        JsonObject jsonObject = null;
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyyMMdd");
        String dateNow = dateFormat.format(date);
        HashMap<String, TbZhaiInfo> mapInfo = new HashMap<>();
        for (page = 1; page <= 5; page++) {
            Thread.sleep(2000);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType,
                    "{\r\n    \"question\": \"可转债\",\r\n    \"perpage\": 200,\r\n    \"page\": " + page
                            + ",\r\n    \"secondary_intent\": \"conbond\",\r\n    \"log_info\": \"{\\\"input_type\\\":\\\"typewrite\\\"}\",\r\n    \"source\": \"Ths_iwencai_Xuangu\",\r\n    \"version\": \"2.0\",\r\n    \"query_area\": \"\",\r\n    \"block_list\": \"\",\r\n    \"add_info\": \"{\\\"urp\\\":{\\\"scene\\\":1,\\\"company\\\":1,\\\"business\\\":1},\\\"contentType\\\":\\\"json\\\",\\\"searchInfo\\\":true}\",\r\n    \"rsh\": \"Ths_iwencai_Xuangu_gg9mwx2kqn7vb4uhzq0g2is0ymq3ilwg\"\r\n}");
            Request request = new Request.Builder()
                    .url("http://www.iwencai.com/customized/chart/get-robot-data")
                    .method("POST", body)
                    .addHeader("Origin", "http://www.iwencai.com")
                    .addHeader("Referer",
                            "http://www.iwencai.com/unifiedwap/result?w=%E5%8F%AF%E8%BD%AC%E5%80%BA")
                    .addHeader("Cookie",
                            "ta_random_userid=85efgq659g; WafStatus=0; cid=7e00e64ca6d16c0ffb8a6ae1ea46f9a11656567343; " +
                                    "ComputerID=7e00e64ca6d16c0ffb8a6ae1ea46f9a11656567343; other_uid=Ths_iwencai_Xuangu_i6p20lcvh2prip3pq4ll6vnnquib0pfd; guideState=1;" +
                                    " PHPSESSID=93c54aa461e0042ca0abbd311483333e; wencai_pc_version=1; v=A2IkcJuZdajVj2n0fIp79buntePBs2OIGLVa8az7jFmB7wxdlEO23ehHqg1_")
                    .addHeader("hexin", "A2IkcJuZdajVj2n0fIp79buntePBs2OIGLVa8az7jFmB7wxdlEO23ehHqg1_")
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            String respStr = response.body().string();
            jsonObject = new JsonParser().parse(respStr).getAsJsonObject();
            // String xz = jsonObject.get("data").getAsJsonObject().get("answer")
            // .getAsJsonArray().get(0).getAsJsonObject().get("txt").getAsJsonArray().get(0)
            // .getAsJsonObject().get("content").getAsJsonObject().get("components")
            // .getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonObject().get("datas")
            // .getAsJsonArray().get(0).getAsJsonObject().get("code").getAsString();
            // System.out.println(xz);
            JsonArray zhaiJsonList = jsonObject.get("data").getAsJsonObject().get("answer")
                    .getAsJsonArray().get(0).getAsJsonObject().get("txt").getAsJsonArray().get(0)
                    .getAsJsonObject().get("content").getAsJsonObject().get("components")
                    .getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonObject().get("datas")
                    .getAsJsonArray();
            for (JsonElement jsonElement : zhaiJsonList) {
                TbZhaiInfo tbZhaiInfo = new TbZhaiInfo();
                System.out.println("-------" + page);
                if (jsonElement.getAsJsonObject().get("code") == null) {
                    continue;
                }
                System.out.println(jsonElement.getAsJsonObject().get("code").getAsString());
                // System.out.println(jsonElement.getAsJsonObject());
                // jsonElement.getAsJsonObject().get("code").getAsString();
                if (jsonElement.getAsJsonObject().get("可转债@最新变动后余额["+dateNow+"]") != null) {
                    tbZhaiInfo.setBalance(
                            jsonElement.getAsJsonObject().get("可转债@最新变动后余额["+dateNow+"]").getAsDouble() / Math.pow(10,
                                    8));
                    System.out.println(tbZhaiInfo.getBalance());
                } else {
                    tbZhaiInfo.setBalance(0D);
                }
                if (jsonElement.getAsJsonObject().get("可转债@涨跌幅["+dateNow+"]") != null) {
                    tbZhaiInfo.setUpRate(jsonElement.getAsJsonObject().get("可转债@涨跌幅["+dateNow+"]").getAsDouble());
                    System.out.println(tbZhaiInfo.getUpRate());
                } else {
                    tbZhaiInfo.setUpRate(0D);
                }
                mapInfo.put(jsonElement.getAsJsonObject().get("code").getAsString(),
                        tbZhaiInfo);
            }

        }
        System.out.println(mapInfo.size());
        return mapInfo;
    }

}
