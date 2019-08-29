package com.hang.service;

import com.hang.constant.SchoolConstant;
import com.hang.dao.CourseDAO;
import com.hang.pojo.data.CourseDO;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.hang.constant.SchoolConstant.LOGIN_URL;
import static com.hang.constant.SchoolConstant.HUANCHON_URL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author free-go
 * @date 2019/7/19
 * @function: 网络爬虫服务层
 */
@Service
public class CourseCrawlerService {

    @Autowired
    private CourseDAO courseDAO;

    public  Integer flag = 0;
    /**
     * 登陆到教务系统
     *
     * @param USERNAME 用户名
     * @param PASSWORD 密码
     */
    public HttpClient login(String USERNAME, String PASSWORD) {
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost httpost = new HttpPost(LOGIN_URL);
        HttpPost post=new HttpPost(HUANCHON_URL);
        String con=null;
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        nvps.add(new BasicNameValuePair("USERNAME", USERNAME));
        nvps.add(new BasicNameValuePair("PASSWORD", PASSWORD));
        nvps.add(new BasicNameValuePair("useDogCode", ""));
        nvps.add(new BasicNameValuePair("x", "37"));
        nvps.add(new BasicNameValuePair("y", "11"));

        /*设置字符*/
        httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

        /*尝试登陆*/
        HttpResponse response;

        /*尝试缓冲登陆*/
        HttpResponse httpResponse;

        try {
            //执行登陆
            response = httpclient.execute(httpost);
            //执行缓冲登陆
            httpResponse=httpclient.execute(post);
            HttpEntity httpEntity = httpResponse.getEntity();
            con = EntityUtils.toString(httpEntity, "utf-8");
            //关闭登陆结果集
            response.getEntity().getContent().close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = Jsoup.parse(con);
        Elements elements=doc.select("menu[parentid=0]");
        //登不上官网
        if (Objects.isNull(elements)) {
            flag=2;
        }
        return httpclient;
    }

    /**
     * 将课程周数格式转换
     */
    public String Seq(String str) {
        //字符处理去掉"（周）"
        str = str.replaceAll("\\(", "").replaceAll("周\\)", "");
        String[] split = str.split(",");
        String seq = "-";
        for (String i : split
        ) {
            String[] delimiter = i.split("-");
            if (delimiter.length == 1) {
                for (int j = Integer.valueOf(delimiter[0]); j <= Integer.valueOf(delimiter[0]); j++) {
                    seq = seq + j + "-";
                }
            } else {
                for (int j = Integer.valueOf(delimiter[0]); j <= Integer.valueOf(delimiter[1]); j++) {
                    seq = seq + j + "-";
                }
            }
        }
        return seq;
    }


    public HttpEntity getCurriculum(HttpClient httpClient, String xueqi, String USERNAME) {
        //爬取周数URL,后面发现加重了服务器压力，便爬取个人全部课表
        String SURL = "http://jwcxxcx.ccsu.cn/jwxt/tkglAction.do?method=goListKbByXs&istsxx=no&xnxqh=" + xueqi + "&zc=&xs0101id=" + USERNAME;
        HttpGet httpGet = new HttpGet(SURL);
        HttpResponse re;
        try {
            re = httpClient.execute(httpGet);
            HttpEntity en = re.getEntity();
            return en;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析并存储课程数据
     *
     * @param i
     * @param element
     * @param jie
     * @param xueqi
     */
    public void Save(int i, Element element, String jie, String xueqi, String USERNAME, String classTime) {
        CourseDO course = new CourseDO();
        String[] array = element.text().split(" ");
        if (array.length > 1) {
            for (int j = 0; j < array.length / 5; j++) {
                course.setJwcAccount(USERNAME);
                course.setWeekday(String.valueOf(i));
                course.setClassTime(classTime);
                course.setSection(jie);
                course.setSubjectName(array[0 + 5 * j]);
                course.setClassName(array[1 + 5 * j]);
                course.setTeacher(array[2 + 5 * j]);
                course.setWeekSeq(Seq(array[3 + 5 * j]));
                course.setWeekStr(array[3 + 5 * j]);
                course.setLocation(array[4 + 5 * j]);
                course.setXnxqh(xueqi);
                courseDAO.addCourse(course);
            }
        }
    }

    /**
     * 爬取课程功能调用器
     *
     * @apiNote flag代表登陆状态
     */
    public Integer turnToCourse(String USERNAME, String PASSWORD) {
        //如果数据库里有当前学期的课程就不需要在绑定学号的时候重复写数据了
        SchoolConstant schoolConstant = new SchoolConstant();
        String xueqi = schoolConstant.getTerm();
        List<CourseDO> courseDOS = courseDAO.selectAllCourseByJwcAccountAndSemester(USERNAME,xueqi);
        HttpEntity en = getCurriculum(login(USERNAME, PASSWORD), xueqi, USERNAME);
        String con = null;
        try {
            con = EntityUtils.toString(en, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = Jsoup.parse(con);
        if (doc != null&&flag!=2) {
            flag = 1;
            //i代表周一至周日
            if (courseDOS.size() == 0) {
                for (int i = 1; i < 8; i++) {
                    //  "1-"至"5-"代表1至5大节课
                    Element element = doc.getElementById("1-" + i + "-2");
                    Save(i, element, "1-2", xueqi, USERNAME, "08:00");
                }
                for (int i = 1; i < 8; i++) {
                    Element element = doc.getElementById("2-" + i + "-2");
                    Save(i, element, "3-4", xueqi, USERNAME, "10:00");
                }
                for (int i = 1; i < 8; i++) {
                    Element element = doc.getElementById("3-" + i + "-2");
                    Save(i, element, "5-6", xueqi, USERNAME, "14:00");
                }
                for (int i = 1; i < 8; i++) {
                    Element element = doc.getElementById("4-" + i + "-2");
                    Save(i, element, "7-8", xueqi, USERNAME, "16:00");
                }
                for (int i = 1; i < 8; i++) {
                    Element element = doc.getElementById("5-" + i + "-2");
                    Save(i, element, "9-10", xueqi, USERNAME, "19:00");
                }
            }
        }
        return flag;
    }
}