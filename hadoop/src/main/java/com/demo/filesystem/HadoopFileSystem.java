package com.demo.filesystem;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * hdfs工具类
 * @author Yang
 */
public class HadoopFileSystem {
    /**
     * 创建对象
     */
    private static FileSystem fs;

    /**
     * 设置FileSystem的参数，生成对象
     * @param uri 文件系统路径
     */
    public static void setFileSystem(String uri) throws URISyntaxException, IOException {
        fs=FileSystem.get(new URI(uri), new Configuration());
    }

    /**
     * 返回FileSystem对象
     */
    protected static FileSystem getFileSystem(){
        return  fs;
    }

    /**
     * 在HDFS上创建文件
     * -通过FileSystem打开一条流，但是什么数据也不传
     * @param path 文件路径
     */
    public static void createFile(String path) throws IOException {
        //定义路径
        Path dfs=new Path(path);
        //创建路径
        FSDataOutputStream out=fs.create(dfs,true);
        System.out.println("创建文件成功");
        out.close();
    }

    /**
     * 在HDFS上创建目录
     * @param path 文件路径
     */
    private static void createDir(String path) throws IOException {
        fs.mkdirs(new Path(path));
        System.out.println("创建目录成功");
    }

    /**
     * 删除HDFS上的目录（级联删除）
     * @param path 文件路径
     */
    public static void deleteDir(String path) throws IOException {
        fs.delete(new Path(path),true);
        System.out.println("删除(级联)成功");
    }

    /**
     * 上传文件到HDFS
     * -通过本地输入流打开本地文件，再通过IO流工具类将输入流数据拷贝到hdfs输出流中
     * @param localFilePath 本地文件路径
     * @param targetPath 保存到HDFS上的目标路径
     */
    public static void putFile(String localFilePath, String targetPath) throws IOException {
        // 手动实现
        InputStream in = new BufferedInputStream(new FileInputStream(localFilePath));
        FSDataOutputStream out = fs.create(new Path(targetPath));
        IOUtils.copyBytes(in, out, 4096);
        // 使用提供的API
        //fs.copyFromLocalFile(new Path(localFilePath),new Path(targetPath));
        System.out.println("上传成功");
        out.close();
        in.close();
    }

    /**
     * 从hdfs上下载文件
     * -通过输入流打开hdfs的文件，再通过IO流工具类将流数据拷贝到本地输出流中
     * @param targetFilePath hdfs上要下载的目标文件路径
     * @param localPath 要保存到本地的路径
     */
    public static void getFile(String targetFilePath,String localPath) throws IOException {
        // 手动实现
        FSDataInputStream in = fs.open(new Path(targetFilePath));
        FileOutputStream out = new FileOutputStream(new File(localPath));
        IOUtils.copyBytes(in, out, 4096, true);
        // 使用API实现
        //fs.copyToLocalFile(new Path(targetFilePath),new Path(localPath));
        System.out.println("下载成功");
        out.close();
        in.close();
    }

    /**
     * 遍历hdfs上的文件
     * -从hdfs获取列表状态，然后一个个输出信息
     * @param path 文件路径
     */
    public static void getFileList(String path) throws IOException {
        FileStatus[] statuses=fs.listStatus(new Path(path));
        for(FileStatus f:statuses){
            // 输出目录或文件相关信息
            if(f.isDirectory()){
                System.out.println("目录"+f.getPath().toUri().getPath());
            }else{
                System.out.println("文件"+f.getPath().toUri().getPath());
            }
        }
    }

    /**
     * 查看HDFS上的文件的内容
     * -通过输入流打开hdfs的文件，通过io工具类拷贝到标准输出流中
     * @param path 文件路径
     */
    public static void catFile(String path) throws IOException {
        FSDataInputStream inputStream=fs.open(new Path(path));
        IOUtils.copyBytes(inputStream, System.out, 4096, false);
        IOUtils.closeStream(inputStream);
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        setFileSystem("hdfs://hadoop:9000");
        putFile("hadoop/src/main/resources/file/air_data.txt", "hdfs://hadoop:9000/air/air_data.txt");
    }
}
