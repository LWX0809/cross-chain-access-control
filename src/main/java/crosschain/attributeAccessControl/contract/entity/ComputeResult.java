package crosschain.attributeAccessControl.contract.entity;

import crosschain.attributeAccessControl.contract.state.State;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONArray;
import org.json.JSONObject;

import static java.nio.charset.StandardCharsets.UTF_8;

@DataType()
public class ComputeResult extends State {
    ////等级差异 与加法因子对比
    @Property()
    private String compareLevelResult;

    //操作差异 与0对比 BigInteger.ZERO
    @Property()
    private String compareOperationResult;

    //组织差异 相等就是非跨链 不相等就是跨链
    @Property()
    private String compareOrganizationResult;

    public ComputeResult() {
        super();
    }

    public ComputeResult(String compareLevelResult, String compareOperationResult, String compareOrganizationResult) {
        this.compareLevelResult = compareLevelResult;
        this.compareOperationResult = compareOperationResult;
        this.compareOrganizationResult = compareOrganizationResult;
    }

    public String getCompareLevelResult() {
        return compareLevelResult;
    }

    public void setCompareLevelResult(String compareLevelResult) {
        this.compareLevelResult = compareLevelResult;
    }

    public String getCompareOperationResult() {
        return compareOperationResult;
    }

    public void setCompareOperationResult(String compareOperationResult) {
        this.compareOperationResult = compareOperationResult;
    }

    public String getCompareOrganizationResult() {
        return compareOrganizationResult;
    }

    public void setCompareOrganizationResult(String compareOrganizationResult) {
        this.compareOrganizationResult = compareOrganizationResult;
    }

    public static byte[] serialize(ComputeResult computeResult) {
        return State.serialize(computeResult);
    }

    public static ComputeResult deserialize(byte[] data) {
        JSONObject json = new JSONObject(new String(data, UTF_8));
        String compareLevelResult = json.getString("compareLevelResult");
        String compareOperationResult = json.getString("compareOperationResult");

        String compareOrganizationResult=json.getString("compareOrganizationResult");
        return new ComputeResult(compareLevelResult,compareOperationResult,compareOrganizationResult);
    }

    public static String[] convertJsonArrayToString(JSONArray jsonArray) {
        String[] stringArray = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            stringArray[i] = jsonArray.getString(i);
        }
        return stringArray;
    }

    @Override
    public String toString() {
        return "ComputeResult{" +
                "compareLevelResult='" + compareLevelResult + '\'' +
                ", compareOperationResult='" + compareOperationResult + '\'' +
                ", compareOrganizationResult='" + compareOrganizationResult + '\'' +
                '}';
    }
}
