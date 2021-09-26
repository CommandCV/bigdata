package cn.myclass.flightcount;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 飞行会员数据分析任务 飞行数据实体类
 * @author Yang
 */
public class FlightCountBean implements Writable {

    private String memberId;
    private String gender;
    private int age;
    private String workCity;
    private String workProvince;
    private int flightCount;
    private int partitionId;

    public FlightCountBean() {
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setWorkCity(String workCity) {
        this.workCity = workCity;
    }

    public void setWorkProvince(String workProvince) {
        this.workProvince = workProvince;
    }

    public void setFlightCount(int flightCount) {
        this.flightCount = flightCount;
    }

    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        Text.writeString(dataOutput, memberId);
        Text.writeString(dataOutput, gender);
        dataOutput.writeInt(age);
        Text.writeString(dataOutput, workCity);
        Text.writeString(dataOutput, workProvince);
        dataOutput.writeInt(flightCount);
        dataOutput.writeInt(partitionId);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.memberId = Text.readString(dataInput);
        this.gender = Text.readString(dataInput);
        this.age = dataInput.readInt();
        this.workCity = Text.readString(dataInput);
        this.workProvince = Text.readString(dataInput);
        this.flightCount = dataInput.readInt();
        this.partitionId = dataInput.readInt();
    }

    @Override
    public String toString() {
        return memberId + "\t" + gender + "\t" + age + "\t" + workCity + "\t" + workProvince + "\t" + flightCount + "\t" + partitionId;
    }
}
