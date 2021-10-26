import org.apache.flink.api.java.utils.ParameterTool;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FlinkParamTest {
    public static void main(String[] args) {
        String params = "--flink.app.common.runEnv prod \n--flink.app.common.configCheckPoint.isEnable true \n--flink.app.common.configCheckPoint.interval.seconds  300 \n--flink.app.common.configCheckPoint.timeout.seconds 300 \n--flink.app.feature.statusHighCtrStream.localCache.cacheMaximumSizeParam 2000000 \n--flink.app.feature.statusHighCtrStream.localCache.cacheTtlSeconds 259200 \n--flink.app.statusHighCtrStream.outOfOrderness.seconds 30 \n--flink.app.statusHighCtrStream.kafka.bootstrapServer 10.10.22.7:9092,10.10.22.8:9092,10.10.23.7:9092,10.10.23.8:9092 \n--flink.app.statusHighCtrStream.kafka.groupIdSuffix 20210831 \n--flink.app.statusHighCtrStream.kafka.startupMode latest-offset \n--flink.app.feature.redis.statusHighCtrStream.messageOutOfOrderness.seconds 180 \n--flink.app.feature.redis.statusHighCtrStream.keyTTl.seconds 604800 \n--flink.app.feature.redis.statusHighCtrStream.zSet.maxSize 500 \n--flink.app.feature.statusHighCtrStream.showLimit 20 \n--flink.app.feature.statusHighCtrStream.randomSeed 600 \n--flink.app.feature.statusHighCtrStream.updateZSetIntervalSeconds 60 \n--flink.app.feature.featureTimeDelaySamplePercent 2";
        Pattern r = Pattern.compile("(--\\S+)\\s+(\\S+)");
        List<String> paramList = Arrays.stream(params.split("\n")).map(String::trim).collect(Collectors.toList());
        paramList.forEach(param -> {
            Matcher matcher = r.matcher(param);
//            while (matcher.find()) {
//            }
            matcher.find();
                System.out.println(matcher.group(1));
                System.out.println(matcher.group(2));
        });

        ParameterTool parameterTool = ParameterTool.fromArgs(params.split("\n"));
        System.out.println(parameterTool.toMap());
    }
}
