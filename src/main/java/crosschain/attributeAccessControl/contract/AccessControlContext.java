package crosschain.attributeAccessControl.contract;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.HashMap;
import java.util.Map;

public class AccessControlContext extends Context {

    public PolicyList policyList;
//    public Map<String,PolicyList> policyListArr;

    public AccessControlContext(ChaincodeStub stub) {
        super(stub);
        this.policyList = new PolicyList(this);
//        this.policyList.setPolicyNum(0);
//        this.policyListArr=new HashMap<String,PolicyList>();
    }
}
