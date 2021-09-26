package cn.myclass.flightcount;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * 飞行会员数据分析任务 Mapper类
 * @author Yang
 */
public class FlightCountMapper extends Mapper<LongWritable, Text, FlightCountBean, NullWritable> {

    private final int MEMBER_ID = 0;
    private final int GENDER = 3;
    private final int WORK_CITY = 5;
    private final int WORK_PROVINCE = 6;
    private final int WORK_COUNTRY = 7;
    private final int AGE = 8;
    private final int FLIGHT_COUNT = 10;

    private final String CN = "CN";
    private final String HK = "HK";
    private final String REGEX = ",";
    private final String DEFAULT = "未知";

    private FlightCountBean k = new FlightCountBean();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // 如果偏移量为0则跳过
        if(key.get() != 0){
            String[] line = value.toString().split(REGEX);
            // 如果国家不是55中国则跳过
            String country = line[WORK_COUNTRY].trim();
            if(CN.equals(country) || HK.equals(country)){
                String workProvince = FlightCountUtil.cleanUpWorkProvince(line[WORK_PROVINCE].trim());
                // 如果省份为未知则跳过
                if(!DEFAULT.equals(workProvince)){
                    String memberId = line[MEMBER_ID].trim();
                    String gender = line[GENDER].trim();
                    int age = FlightCountUtil.cleanUpNumber(line[AGE].trim());
                    String workCity = FlightCountUtil.cleanUpWorkCity(line[WORK_CITY].trim());
                    int flightCount = FlightCountUtil.cleanUpNumber(line[FLIGHT_COUNT].trim());
                    int partitionId = FlightCountUtil.getPartitionsId(workProvince);
                    // 设置键值
                    k.setMemberId(memberId);
                    k.setGender(gender);
                    k.setAge(age);
                    k.setWorkCity(workCity);
                    k.setWorkProvince(workProvince);
                    k.setFlightCount(flightCount);
                    k.setPartitionId(partitionId);
                    // 写入上下文
                    context.write(k, NullWritable.get());
                }
            }

        }
    }
}
