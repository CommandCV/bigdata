package com.myclass.demo.flightcount;

/**
 * 飞行会员数据分析任务 工具类
 * @author Yang
 */
public class FlightCountUtil {

    private static final String NULL = "";
    private static final String POINT = ".";
    private static final String DEFAULT = "未知";
    private static final String CITY_POSTFIX = "市";
    private static final String AREA_POSTFIX = "区";

    private static final String HK = "HK";
    private static final String HONG_KONG = "HONG KONG";
    private static final String HONGKONG = "HONGKONG";

    private static final String[] PROVINCES = new String[]{
            // 地级市
            "北京" , "天津" , "上海" , "重庆" ,
            // 省
            "河北" , "山西" , "辽宁" , "吉林" , "黑龙江" , "江苏" , "浙江" , "安徽" , "福建" , "江西" , "山东" ,
            "河南" , "湖北" , "湖南" , "广东" , "海南" , "四川" , "贵州" , "云南" , "陕西" , "甘肃" , "青海" , "台湾" ,
            // 自治区
            "内蒙古" , "广西" , "西藏" , "宁夏" , "新疆" ,
            // 特别行政区
            "香港" , "澳门"};

    /**
     * 清洗数字
     * 默认为填写的数字，如果为空则为0
     * @param number 数字
     * @return int  处理后的数字
     */
    public static int cleanUpNumber(String number){
        // 如果为空
        if(NULL.equals(number) || POINT.equals(number)){
            return 0;
        }else {
            return Integer.valueOf(number);
        }
    }

    /**
     * 清洗工作城市
     * 默认为填写的城市，如果为空则为未知。
     * 格式为：
     * XX市    ==>  XX
     * XX区    ==>  XX
     * ""      ==>  未知
     *
     * @param workCity 工作城市
     * @return java.lang.String 清洗后的工作城市
     */
    public static String cleanUpWorkCity(String workCity){
        // 如果为空
        if (NULL.equals(workCity) || POINT.equals(workCity)){
            return DEFAULT;
        }else{
            return findCity(workCity);
        }

    }

    /**
     * 清洗工作省份
     * 格式转化规则为：
     * XX市          ==>  XX
     * XX省          ==>  XX
     * XX自治区      ==>  XX
     * XX特别行政区  ==>  XX
     * ""            ==>  未知
     *
     * @param workProvince 工作省份
     * @return java.lang.String 清洗后的工作省份
     */
    public static String cleanUpWorkProvince(String workProvince){
        // 为空
        if (NULL.equals(workProvince) || POINT.equals(workProvince)){
            return DEFAULT;
        // 获得省份
        }else{
            return findProvince(workProvince);
        }
    }

    /**
     * 获得Hive表分区的id
     * @param province 省份
     * @return int 分区Id
     */
    public static int getPartitionsId(String province){
        int length = PROVINCES.length;
        for(int i=0 ;i< length; i++){
            if (PROVINCES[i].equals(province)){
                return i;
            }
        }
        return 0;
    }

    /**
     * 查找城市
     * @param city 城市
     * @return java.lang.String 匹配的城市
     */
    private static String findCity(String city){
        // 如果是区市则返回区市前的关键词
        if (city.contains(CITY_POSTFIX) || city.contains(AREA_POSTFIX)){
            return city.substring(0, city.length() - 1);
        }else if(DEFAULT.equals(processHK(city))){
            return city;
        }else {
            // 特殊的香港
            return processHK(city);
        }
    }

    /**
     * 查找省份
     * @param province 省份
     * @return java.lang.String 匹配的省份
     */
    private static String findProvince(String province){
        // 如果存在省份关键词则直接返回关键词
        for(String p: PROVINCES){
            if(province.contains(p)){
                return p;
            }
        }
        // 特殊的香港
        return processHK(province);
    }

    /**
     * 处理香港
     * @param hk 极大可能的香港
     * @return java.lang.String 处理后的字符串
     */
    private static String processHK(String hk){
        if(hk.equalsIgnoreCase(HK) || hk.equalsIgnoreCase(HONG_KONG) || hk.equalsIgnoreCase(HONGKONG)){
            return "香港";
        }else {
            return DEFAULT;
        }
    }

}
