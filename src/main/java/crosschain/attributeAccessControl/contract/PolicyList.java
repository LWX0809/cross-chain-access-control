package crosschain.attributeAccessControl.contract;

import crosschain.attributeAccessControl.contract.entity.Policy;
import crosschain.attributeAccessControl.contract.state.StateList;
import org.hyperledger.fabric.contract.Context;

public class PolicyList {

    private StateList stateList;
    private int policyNum=0;

    public PolicyList(Context ctx) {
        this.stateList = StateList.getStateList(ctx, PolicyList.class.getSimpleName(), Policy::deserialize);
//        this.policyNum=0;
    }
    public PolicyList addPolicy(Policy policy) {
        stateList.addState(policy);
//        policyNum++;
        return this;
    }
    public Policy getPolicy(String policyID) {
        return (Policy) this.stateList.getState(policyID);
    }

    public PolicyList updatePolicy(Policy policy) {
        this.stateList.updateState(policy);
        return this;
    }

    public int getPolicyNum() {
        return policyNum;
    }

    public void setPolicyNum(int policyNum) {
        this.policyNum = policyNum;
    }
}
