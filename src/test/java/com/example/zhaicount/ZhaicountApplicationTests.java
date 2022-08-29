package com.example.zhaicount;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.zhaicount.dao.TbZhaiInfoMapper;
import com.example.zhaicount.dto.GuResp;
import com.example.zhaicount.dto.YeResp;
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
import java.util.ArrayList;
import java.util.HashMap;

@SpringBootTest
class ZhaicountApplicationTests {
    @Autowired
    TbZhaiInfoMapper tbZhaiInfoMapper;

    static String detailCodeUrl = "https://datacenter-web.eastmoney.com/api/data/v1/get?reportName=RPT_F10_CORETHEME_CONTENT&columns=MAINPOINT_CONTENT&filter=";

    @Test
    void contextLoads() throws IOException {

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
                    queryCode = zhaiRespItem.getConvertStockCode();
                    TbZhaiInfo tbZhaiInfo = new TbZhaiInfo();
                    tbZhaiInfo.setName(zhaiRespItem.getSecurityNameAbbr());
                    tbZhaiInfo.setGuName(zhaiRespItem.getSecurityShortName());
                    tbZhaiInfo.setType(getType(queryCode));
                    tbZhaiInfo.setCode(zhaiRespItem.getSecurityCode());
                    tbZhaiInfo.setPremiumRatio(zhaiRespItem.getTransferPremiumRatio());
                    tbZhaiInfo.setListingData(zhaiRespItem.getListingDate());
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
    Double getYuE(String zhaiCode) throws UnsupportedEncodingException {
        String yeUrl = "https://datacenter-web.eastmoney.com/api/data/v1/get?columns=ALL&sortColumns=date&sortTypes=2&source=WEB&reportName=RPTA_WEB_KZZ_LS&pageNumber=1&pageSize=1&filter=(";
        yeUrl += URLEncoder.encode("zcode=\"" + zhaiCode + "\"", "utf-8") + ")";
        String resp = HttpRequest.get(yeUrl).body();
        YeResp yeResp = JSON.parseObject(resp, YeResp.class);
        if (yeResp.getResult() == null) {
            return -1D;
        }
        Integer pageEnd = yeResp.getResult().getPages();
        yeUrl = "https://datacenter-web.eastmoney.com/api/data/v1/get?columns=ALL&sortColumns=date&sortTypes=2&source=WEB&reportName=RPTA_WEB_KZZ_LS&pageNumber="
                + pageEnd + "&pageSize=1&filter=(";
        yeUrl += URLEncoder.encode("zcode=\"" + zhaiCode + "\"", "utf-8") + ")";
        resp = HttpRequest.get(yeUrl).body();
        yeResp = JSON.parseObject(resp, YeResp.class);
        Double reultNum = yeResp.getResult().getData().get(0).getSYFE();

        if (reultNum == null) {
            return -1D;
        }
        return reultNum / 100000000;
    }

    String getType(String guCode) throws IOException {
        JsonObject jsonObject = null;
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("query", "所属概念")
                    .addFormDataPart("question", guCode)
                    .addFormDataPart("perpage", "1")
                    .addFormDataPart("condition",
                            "[{\"indexName\":\"所属概念\",\"indexProperties\":[],\"source\":\"new_parser\",\"type\":\"index\",\"indexPropertiesMap\":{},\"reportType\":\"null\",\"chunkedResult\":\"所属概念\",\"valueType\":\"_所属概念\",\"domain\":\"abs_股票领域\",\"uiText\":\"所属概念\",\"sonSize\":0,\"queryText\":\"所属概念\",\"relatedSize\":0,\"tag\":\"所属概念\"}]")
                    .addFormDataPart("query_type", "stock")
                    .addFormDataPart("comp_id", "6257151")
                    .addFormDataPart("source", "Ths_iwencai_Xuangu")
                    .addFormDataPart("uuid", "24087")
                    .addFormDataPart("sort_key", "最新涨跌幅")
                    .addFormDataPart("sort_order", " desc")
                    .addFormDataPart("iwc_token", "0ac9667916589336331293513")
                    .build();
            Request request = new Request.Builder()
                    .url("http://www.iwencai.com/unifiedwap/unified-wap/v2/stock-pick/find")
                    .method("POST", body)
                    .addHeader("Host", "www.iwencai.com")
                    .addHeader("Origin", "http://www.iwencai.com")
                    .addHeader("Referer",
                            "http://www.iwencai.com/unifiedwap/result?w=%E6%89%80%E5%B1%9E%E6%A6%82%E5%BF%B5&querytype=stock")
                    .addHeader("hexin", "AzRnd5p46oYBAX5KMqIbkoGgBfmjDVj3mjHsO86VwL9COdon9h0oh")
                    .addHeader("Content-Type", "application/json;charset=utf-8")
                    .build();
            Response response = client.newCall(request).execute();
            jsonObject = new JsonParser().parse(response.body().string()).getAsJsonObject();
            return jsonObject.get("data").getAsJsonObject().get("data").getAsJsonObject().get("datas").getAsJsonArray()
                    .get(0).getAsJsonObject().get("所属概念").toString().replaceAll("\"", "");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(jsonObject.toString());
            return "";
        }
    }

    @Test
    void getexeclInfo() throws Exception {
        Integer page = 1;
        JsonObject jsonObject = null;
        HashMap<String, TbZhaiInfo> mapInfo = new HashMap<>();
        for (page = 1; page <= 6; page++) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType,
                    "{\r\n    \"question\": \"可转债\",\r\n    \"perpage\": 100,\r\n    \"page\": " + page
                            + ",\r\n    \"secondary_intent\": \"conbond\",\r\n    \"log_info\": \"{\\\"input_type\\\":\\\"typewrite\\\"}\",\r\n    \"source\": \"Ths_iwencai_Xuangu\",\r\n    \"version\": \"2.0\",\r\n    \"query_area\": \"\",\r\n    \"block_list\": \"\",\r\n    \"add_info\": \"{\\\"urp\\\":{\\\"scene\\\":1,\\\"company\\\":1,\\\"business\\\":1},\\\"contentType\\\":\\\"json\\\",\\\"searchInfo\\\":true}\",\r\n    \"rsh\": \"Ths_iwencai_Xuangu_gg9mwx2kqn7vb4uhzq0g2is0ymq3ilwg\"\r\n}");
            Request request = new Request.Builder()
                    .url("http://www.iwencai.com/customized/chart/get-robot-data")
                    .method("POST", body)
                    .addHeader("Origin", "http://www.iwencai.com")
                    .addHeader("Referer",
                            "http://www.iwencai.com/unifiedwap/result?w=%E5%8F%AF%E8%BD%AC%E5%80%BA&querytype=conbond&addSign=1661787010195")
                    .addHeader("hexin-v", "A3wvnxLgMwOFmQff3W9AgZMbTRErdSO2oho0Y1bzi15KPhIPfoXwL_IpBOWl")
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
                System.out.println(jsonElement.getAsJsonObject().get("code").getAsString());
                // System.out.println(jsonElement.getAsJsonObject());
                // jsonElement.getAsJsonObject().get("code").getAsString();
                tbZhaiInfo.setBalance(
                        jsonElement.getAsJsonObject().get("可转债@最新变动后余额[20220830]").getAsDouble() / Math.pow(1,
                                8));
                System.out.println(tbZhaiInfo.getBalance());
                tbZhaiInfo.setUpRate(jsonElement.getAsJsonObject().get("可转债@涨跌幅[20220830]").getAsDouble());
                System.out.println(tbZhaiInfo.getUpRate());

                mapInfo.put(jsonElement.getAsJsonObject().get("code").getAsString(),
                        tbZhaiInfo);
            }

        }
    }

    @Test
    void parseExcel() throws Exception {
        // 1、获取文件的路径
        // 1.1、从桌面获取文件
        FileSystemView fsv = FileSystemView.getFileSystemView();
        String filePath = "/Users/nut/code/java/zhaicount/2022-07-28 (1).xls";
        // 1.2、从绝对路径获取文件
        // String filePath = "D:\\testexcel.xls";

        // 2、通过流获取本地文件
        FileInputStream fileInputStream = new FileInputStream(filePath);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        POIFSFileSystem fileSystem = new POIFSFileSystem(bufferedInputStream);

        // 3、创建工作簿对象，并获取工作表1
        HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);
        HSSFSheet sheet = workbook.getSheet("1");

        // 4、从工作表中获取行数，并遍历
        int lastRowIndex = sheet.getLastRowNum();
        System.out.println("总行数为：" + lastRowIndex);
        ArrayList<HashMap> list = new ArrayList<>();
        for (int i = 1; i <= lastRowIndex; i++) {
            // 4.1 获取每行的数据
            HSSFRow row = sheet.getRow(i);
            if (row == null) {
                break;
            }

            row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
            String zhaiCode = row.getCell(0).getStringCellValue();
            if (zhaiCode.indexOf(".") == -1) {
                continue;
            }
            String zhaiStr = zhaiCode.substring(0, zhaiCode.indexOf("."));

            TbZhaiInfo tbZhaiInfo = tbZhaiInfoMapper
                    .selectOne(new LambdaQueryWrapper<TbZhaiInfo>().eq(TbZhaiInfo::getCode, zhaiStr).last("limit 1"));
            if (tbZhaiInfo == null) {
                continue;
            }
            // 更新增长率
            row.getCell(3).setCellType(Cell.CELL_TYPE_STRING);
            String ratio = row.getCell(3).getStringCellValue();
            if (ratio.equals("--")) {
                tbZhaiInfo.setUpRate(-1000D);
            } else {
                tbZhaiInfo.setUpRate(Double.parseDouble(row.getCell(3).getStringCellValue()));
            }

            // 更新余额
            row.getCell(16).setCellType(Cell.CELL_TYPE_STRING);
            String balance = row.getCell(16).getStringCellValue();

            if (balance.indexOf("亿") != -1) {
                tbZhaiInfo.setBalance(Double.parseDouble(balance.substring(0, balance.indexOf("亿"))));
            } else if (balance.indexOf("万") != -1) {
                tbZhaiInfo.setBalance(
                        Double.parseDouble(balance.substring(0, balance.indexOf("万")).replaceAll(",", "")) * 0.0001);
            } else {
                tbZhaiInfo.setBalance(0D);
            }

            tbZhaiInfoMapper.updateById(tbZhaiInfo);
        }

        // 6、关闭资源、输出封装数据
        bufferedInputStream.close();
        System.out.println(list.toString());

    }

}
