package crosschain.attributeAccessControl.contract.entity;

import crosschain.attributeAccessControl.contract.state.State;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONArray;
import org.json.JSONObject;

import static java.nio.charset.StandardCharsets.UTF_8;

@DataType()
public class Policy extends State {
    //策略id 可以由客体（数据拥有者）标识拼接
    @Property()
    private String policyID;


    //主体属性
    @Property()
    private SubjectAttribute subjectAttribute;

    //客体属性
    @Property()
    private ObjectAttribute objectAttribute;

    //操作 allow deney ...
    @Property()
    private String operation;

    //环境属性
    @Property()
    private Environment environment;

    public Policy() {
        super();
    }

    public Policy(String policyID, SubjectAttribute subjectAttribute, ObjectAttribute objectAttribute, String operation, Environment environment) {
        this.policyID = policyID;
        this.subjectAttribute = subjectAttribute;
        this.objectAttribute = objectAttribute;
        this.operation = operation;
        this.environment = environment;
        this.key = State.makeKey(new String[] { policyID });
    }

    public String getPolicyID() {
        return policyID;
    }

    public void setPolicyID(String policyID) {
        this.policyID = policyID;
    }

    public SubjectAttribute getSubjectAttribute() {
        return subjectAttribute;
    }

    public void setSubjectAttribute(SubjectAttribute subjectAttribute) {
        this.subjectAttribute = subjectAttribute;
    }

    public ObjectAttribute getObjectAttribute() {
        return objectAttribute;
    }

    public void setObjectAttribute(ObjectAttribute objectAttribute) {
        this.objectAttribute = objectAttribute;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Policy setKey() {
        this.key = State.makeKey(new String[] { this.policyID });
        return this;
    }

    public static byte[] serialize(Policy policy) {
        return State.serialize(policy);
    }

    public static Policy deserialize(byte[] data) {
        JSONObject json = new JSONObject(new String(data, UTF_8));

        String policyID = json.getString("policyID");

        //todo
        JSONObject subjectAttribute = (JSONObject) json.get("subjectAttribute");

       // String [] subjectAttribute1=convertJsonArrayToString(subjectAttribute);
//        SubjectAttribute subjectAttributeObj=new SubjectAttribute(subjectAttribute1[0],subjectAttribute1[1],subjectAttribute1[2],subjectAttribute1[3],subjectAttribute1[4]);

        SubjectAttribute subjectAttributeObj=new SubjectAttribute(subjectAttribute.getString("chainID"),subjectAttribute.getString("organization"),subjectAttribute.getString("role"),subjectAttribute.getString("identityID"),subjectAttribute.getString("securityLevel"));

//        JSONArray objectAttribute= (JSONArray) json.get("objectAttribute");
//        String [] objectAttribute1=convertJsonArrayToString(objectAttribute);
//        ObjectAttribute objectAttributeObj=new ObjectAttribute(objectAttribute1[0],objectAttribute1[1],objectAttribute1[2],objectAttribute1[3],objectAttribute1[4]);


        JSONObject objectAttribute= (JSONObject) json.get("objectAttribute");
       // String [] objectAttribute1=convertJsonArrayToString(objectAttribute);
        ObjectAttribute objectAttributeObj=new ObjectAttribute(objectAttribute.getString("chainID"),objectAttribute.getString("organization"),objectAttribute.getString("type"),objectAttribute.getString("identityID"),objectAttribute.getString("securityLevel"));

        String operation = json.getString("operation");

//        JSONArray environment= (JSONArray) json.get("environment");
//        String [] environment1=convertJsonArrayToString(environment);
//        Environment environmentObj=new Environment(environment1[0],environment1[1]);

        JSONObject environment= (JSONObject) json.get("environment");
   //     String [] environment1=convertJsonArrayToString(environment);
        Environment environmentObj=new Environment(environment.getString("time"),environment.getString("place"));

        return new Policy(policyID,subjectAttributeObj,objectAttributeObj,operation,environmentObj);
    }

    public static String[] convertJsonArrayToString(JSONArray jsonArray) {
        String[] stringArray = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            stringArray[i] = jsonArray.getString(i);
        }
        return stringArray;
    }
}
