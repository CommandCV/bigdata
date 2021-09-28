package com.demo.storm.wordcount;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Yang
 */
public class TestPath {
    public static void main(String[] args) throws IOException {

        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(new FileInputStream("target/classes/file/word"),"UTF-8"));
        String str;
        while ( (str = bufferedReader.readLine()) !=null){
            System.out.println("---------------Begin---------------");
            String[] words = str.split("\t");
            for(String word: words){
                System.out.println("-"+word);
            }
        }
    }
}
