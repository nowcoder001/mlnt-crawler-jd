package cn.mlnt.jd.util;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Component
public class HttpUtils {

    private PoolingHttpClientConnectionManager cm;

    public HttpUtils() {
        this.cm = new PoolingHttpClientConnectionManager();
        // 设置最大连接数
        this.cm.setMaxTotal(100);
        // 设置每个主机的最大连接数
        this.cm.setDefaultMaxPerRoute(10);
    }

    /**
     * 根据请求地址下载页面数据
     * @param url
     * @return 页面数据
     */
    public String doGetHtml(String url) {
        // 获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(this.cm).build();

        // 创建httpGet对象，设置url地址
        HttpGet httpGet = new HttpGet(url);

        // 设置请求信息
        httpGet.setConfig(this.getConfig());

        //设置请求Request Headers中的User-Agent，告诉京东说这是浏览器访问
        httpGet.addHeader("User-Agent","Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Mobile Safari/537.36");

        CloseableHttpResponse response = null;

        try {
            // 使用HttpClient发起请求，获取响应
            response = httpClient.execute(httpGet);

            // 解析响应，返回结果
            if(response.getStatusLine().getStatusCode() == 200) {
                String content = "";
                // 判断响应体Entity是否不为空，如果不为空就可以使用EntityUtils
                if(response.getEntity() != null) {
                    content = EntityUtils.toString(response.getEntity(), "utf8");
                    return content;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭response
           if(response != null) {
               try {
                   response.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        }
        // 返回空字符串
        return "";
    }

    /**
     * 下载图片
     * @param url
     * @return 图片名称
     */
    public String doGetImage(String url) {
        // 获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(this.cm).build();

        // 创建httpGet对象，设置url地址
        HttpGet httpGet = new HttpGet(url);

        // 设置请求信息
        httpGet.setConfig(this.getConfig());

        CloseableHttpResponse response = null;

        try {
            // 使用HttpClient发起请求，获取响应
            response = httpClient.execute(httpGet);

            // 解析响应，返回结果
            if(response.getStatusLine().getStatusCode() == 200) {
                // 判断响应体Entity是否不为空
                if(response.getEntity() != null) {
                    // 下载图片
                    // 获取图片的后缀
                    String  extName = url.substring(url.lastIndexOf("."));

                    // 创建图片名，重命名图片
                    String picName = UUID.randomUUID().toString()+extName;

                    // 下载图片
                    // 声明OutPutStream
                    OutputStream outputStream = new FileOutputStream(new File("C:\\Users\\18476\\Desktop\\images\\"+picName));

                    response.getEntity().writeTo(outputStream);

                    // 返回图片名称
                    return picName;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭response
            if(response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 如果下载失败，返回空字符串
        return "";
    }


    /**
     * 设置请求信息
     * @return
     */
    private RequestConfig getConfig() {
        RequestConfig config = RequestConfig.custom()
                // 创建链接的最长时间
                .setConnectTimeout(1000)
                // 获取连接到最长时间
                .setConnectionRequestTimeout(500)
                // 数据传输的最长时间
                .setSocketTimeout(10000)
                .build();
        return config;
    }

    public static void main(String[] args) throws IOException {
        HttpUtils httpUtils = new HttpUtils();
        String itemInfo = httpUtils.doGetHtml("https://item.jd.com/100009082466.html");
        String title = Jsoup.parse(itemInfo).select("div#itemName").text();
        System.out.println(Jsoup.parse(itemInfo).select("div#itemName"));
        System.out.println(title);
    }
}
