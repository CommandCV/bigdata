package cn.myclass.ReviewTags;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 提取评论生成标签
 * @author Yang
 */
public class ReviewTags {

    public static String extractTags(String jsonString) {
        // 将json字符串转化成对象
        JSONObject object = JSON.parseObject(jsonString);
        // 判断字符串是否空且是否包含有exInfoList(提取的信息列表)字段
        if (object == null || !object.containsKey("extInfoList")) {
            return "";
        }
        // 获取提取到的信息，并进行空值处理
        JSONArray array = object.getJSONArray("extInfoList");
        if (array == null) {
            return "";
        }
        //
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (obj != null && obj.containsKey("title") && "contentTags".equals(obj.getString("title"))
                    && obj.containsKey("values")) {
                JSONArray arr = obj.getJSONArray("values");
                if (arr == null) {
                    continue;
                }
                // 将评论的数据进行拼接
                boolean begin = true;
                for (int j = 0; j < arr.size(); j++) {
                    if (begin) {
                        begin = false;
                    } else {
                        sb.append(",");
                    }
                    sb.append(arr.getString(j));
                }
            }
        }
        return sb.toString();

    }

    public static void main(String[] args) {
        String s = "{\"reviewPics\":" +
                "[{\"picId\":2405538806,\"url\":\"http://p0.where.net/shaitu/7c10019c62947d01ded80cc698c77c90217708.jpg\",\"status\":1}," +
                "{\"picId\":2405442602,\"url\":\"http://p0.meituan.net/shaitu/d41ef06f5d16d5d3cbc871765ff93130270451.jpg\",\"status\":1}]," +
                "\"extInfoList\":[{\"title\":\"contentTags\",\"values\":[\"回头客\",\"上菜快\",\"环境优雅\",\"性价比高\",\"菜品不错\"],\"desc\":\"\",\"defineType\":0}," +
                "{\"title\":\"tagIds\",\"values\":[\"493\",\"232\",\"24\",\"300\",\"1\"],\"desc\":\"\",\"defineType\":0}]," +
                "\"expenseList\":null,\"reviewIndexes\":[1,2],\"scoreList\":null}";
        System.out.println(extractTags(s));
        System.out.println(extractTags(""));
        System.out.println(extractTags(null));
    }
}
