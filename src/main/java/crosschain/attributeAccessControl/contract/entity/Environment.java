package crosschain.attributeAccessControl.contract.entity;

import crosschain.attributeAccessControl.contract.state.State;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType()
public class Environment {

    //时间
    @Property()
    private String time;

    //地点 街道 (没必要)
    @Property()
    private String place;

    public Environment() {
        super();
    }

    public Environment(String time, String place) {
        this.time = time;
        this.place = place;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

}
