package cn.myclass.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * HBase测试
 * @author Yang
 */
public class TestHBase {

    private static Configuration configuration=HBaseConfiguration.create();

    private static Connection conn;

    /**
     * 创建命名空间操作
     * @param namespaces 命名空间
     * @throws IOException  异常
     */
    public static void createNamespaces(String namespaces) throws IOException {
        conn = ConnectionFactory.createConnection(configuration);
        //获得权限
        Admin admin=conn.getAdmin();
        NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create(namespaces).build();
        //设置表描述器，设置表名
        admin.createNamespace(namespaceDescriptor);
        //关闭连接
        admin.close();

    }

    /**
     * 创建表操作
     * @param tableName 表名
     * @param columnFamily  列族
     * @throws IOException  异常
     */
    public static void createTable(String tableName,String columnFamily) throws IOException {
        conn = ConnectionFactory.createConnection(configuration);
        //获得权限
        Admin admin=conn.getAdmin();
        //设置表描述器，设置表名
        HTableDescriptor tableDescriptor=new HTableDescriptor(TableName.valueOf(tableName));
        //创建列族描述器，设置列祖名以及版本个数
        HColumnDescriptor columnDescriptor=new HColumnDescriptor(columnFamily);
        columnDescriptor.setMaxVersions(3);
        //将列族添加至表描述器中
        tableDescriptor.addFamily(columnDescriptor);
        //交给admin创建表
        admin.createTable(tableDescriptor);
        //关闭连接
        admin.close();

    }



    /**
     * 向hbase中插入数据
     * @param tableName 表名
     * @param row  行键
     * @param columnFamily  列族
     * @param column  列名
     * @param data  数据
     */
    public static void putRow(String tableName,String row,String columnFamily,String column,String data) throws IOException {
        conn = ConnectionFactory.createConnection(configuration);
        // 创建表对象
        Table table=conn.getTable(TableName.valueOf(tableName));
        // 设置行键
        Put put=new Put(Bytes.toBytes(row));
        // 添加数据格式为  列族---列名---数据
        put.addColumn(Bytes.toBytes(columnFamily),Bytes.toBytes(column),Bytes.toBytes(data));
        // 添加至表中
        table.put(put);
        // 关闭资源
        table.close();
    }

    /**
     * 从hbase中获得表中的一行数据
     * @param tableName 表名
     * @param rowKey  行键
     */
    public static void getRow(String tableName,String rowKey) throws IOException {
        conn = ConnectionFactory.createConnection(configuration);
        // 创建表对象
        Table table=conn.getTable(TableName.valueOf(tableName));
        //创建get对象，设置行键
        Get get=new Get(Bytes.toBytes(rowKey));
        //设置版本数
        get.setMaxVersions(3);
        //获得结果集
        Result result=table.get(get);
        //遍历结果集
        for (Cell cell : result.listCells()) {
            String family = new String(CellUtil.cloneFamily(cell));
            String column = new String(CellUtil.cloneQualifier(cell));
            String value = new String(CellUtil.cloneValue(cell));
            System.out.println("行键:" + rowKey + ",列族:"+family +",列:"+ column +",值:"+ value);
        }
        //关闭连接
        table.close();

    }

    /**
     * 扫描表数据
     * @param tableName 表名
     */
    public static void scanTable(String tableName) throws IOException {
        conn = ConnectionFactory.createConnection(configuration);
        // 创建表对象
        Table table=conn.getTable(TableName.valueOf(tableName));

        //获得scan对象并设置扫描的行键
        Scan scan=new Scan();
        //获得结果集
        ResultScanner scanner=table.getScanner(scan);
        //遍历输出结果集
        for(Result result:scanner){
            for (Cell cell : result.listCells()) {
                String rowKey = new String(CellUtil.cloneRow(cell));
                String family = new String(CellUtil.cloneFamily(cell));
                String column = new String(CellUtil.cloneQualifier(cell));
                String value = new String(CellUtil.cloneValue(cell));
                System.out.println("行键:" + rowKey + ",列族:"+family +",列:"+ column +",值:"+ value);
            }
        }
        table.close();
    }

    /**
     * 删除表
     * @param tableName 表名
     */
    public static void dropTable(String tableName) throws IOException {
        conn = ConnectionFactory.createConnection(configuration);
        // 创建表对象
        TableName table=TableName.valueOf(tableName);
        // 获得权限
        Admin admin=conn.getAdmin();
        // 使表失效后删除
        admin.disableTable(table);
        admin.deleteTable(table);
        admin.close();
    }

    /**
     * 删除数据
     * @param tableName  表名
     * @param row  行键
     * @param columnFamily 列族
     * @param column  列
     */
    public static void deleteColumn(String tableName, String row, String columnFamily, String column) throws IOException {
        conn = ConnectionFactory.createConnection(configuration);
        // 创建表对象
        Table table=conn.getTable(TableName.valueOf(tableName));
        // 获得delete对象，设置行键
        Delete delete=new Delete(Bytes.toBytes(row));
        // 设置删除的列族以及列的信息
        delete.addColumn(Bytes.toBytes(columnFamily),Bytes.toBytes(column));
        // 删除数据
        table.delete(delete);
        // 关闭连接
        table.close();
    }

    public static void main(String[] args) throws IOException {
        //设置zookeeper所在地址
//        configuration.set("hbase.zookeeper.quorum", "node1:2181,node2:2181,node3:2181");
        configuration.set("hbase.zookeeper.quorum", "master:2181,slave1:2181,slave2:2181");

//        dropTable("user_portrait");
//        dropTable("ssy:user_portrait");
//        createNamespaces("ssy");
//        createTable("ssy:log","info");
        //putRow("test","rk001","info","name","tom");
        //putRow("test","rk002","info","name","cat");
        //getRow("test","rk001");
        //getRow("test","rk002");

        //scanTable("test");
        //deleteColumn("test","rk001","info","name");
        scanTable("ssy:user_portrait");
        //dropTable("test");
    }
}
