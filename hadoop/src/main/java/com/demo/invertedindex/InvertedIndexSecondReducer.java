package com.demo.invertedindex;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

/**
 * 倒排索引任务 Reducer类
 * 第二次Reduce，将值按照文本出现的次数按照逆序排序
 * @author Yang
 */
public class InvertedIndexSecondReducer extends Reducer<Text, Text, Text, Text> {

    private Map<String, Long> map = new HashMap<>(3);

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for(Text value:values){
            String[] arr = value.toString().split("->");
            String fileName = arr[0];
            String count = arr[1];
            map.put(fileName, Long.valueOf(count));
        }
        Map<String, Long> resultMap = new LinkedHashMap<>(3);
        map.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(comparingByValue()))
                .collect(Collectors.toList())
                .forEach(element -> resultMap.put(element.getKey(), element.getValue()));
        StringBuilder stringBuilder = new StringBuilder();
        for(Map.Entry<String, Long> entrySet: resultMap.entrySet()){
            stringBuilder.append(entrySet.getKey())
                    .append("->")
                    .append(entrySet.getValue())
                    .append(",");
        }
        String value = stringBuilder.toString();
        context.write(key, new Text(value.substring(0, value.length() -1)));
        map.clear();
    }
}
