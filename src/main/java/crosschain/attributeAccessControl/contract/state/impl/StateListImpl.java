package crosschain.attributeAccessControl.contract.state.impl;


import crosschain.attributeAccessControl.contract.state.State;
import crosschain.attributeAccessControl.contract.state.StateDeserializer;
import crosschain.attributeAccessControl.contract.state.StateList;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import java.util.Arrays;
import java.util.logging.Logger;

/*
SPDX-License-Identifier: Apache-2.0
*/

/**
 * StateList provides a named virtual container for a set of ledger states. Each
 * state has a unique key which associates it with the container, rather than
 * the container containing a link to the state. This minimizes collisions for
 * parallel transactions on different states.
 */
public class StateListImpl implements StateList {

    private Context ctx;
    private String name;
    private Object supportedClasses;
    private StateDeserializer deserializer;

    private final static Logger logger = Logger.getLogger(StateListImpl.class.getName());
    /**
     * Store Fabric context for subsequent API access, and name of list
     *
     * @param deserializer
     */
    public StateListImpl(Context ctx, String listName, StateDeserializer deserializer) {
        this.ctx = ctx;
        this.name = listName;
        this.deserializer = deserializer;
    }

    /**
     * Add a state to the list. Creates a new state in worldstate with appropriate
     * composite key. Note that state defines its own key. State object is
     * serialized before writing.
     */
    @Override
    public StateList addState(State state) {

        logger.info("statelist info");
        logger.info("Adding state " + this.name);
        ChaincodeStub stub = this.ctx.getStub();
        logger.info("Stub=" + stub);
        String[] splitKey = state.getSplitKey();
        logger.info("Split key " + Arrays.asList(splitKey));

        CompositeKey ledgerKey = stub.createCompositeKey(this.name, splitKey);
        System.out.println("ledgerkey is ");
        System.out.println(ledgerKey);

        byte[] data = State.serialize(state);
        System.out.println("ctx" + this.ctx);
        System.out.println("stub" + this.ctx.getStub());
        this.ctx.getStub().putState(ledgerKey.toString(), data);

        return this;
    }

    /**
     * Get a state from the list using supplied keys. Form composite keys to
     * retrieve state from world state. State data is deserialized into JSON object
     * before being returned.
     */
    @Override
    public State getState(String key) {

        CompositeKey ledgerKey = this.ctx.getStub().createCompositeKey(this.name, State.splitKey(key));

        byte[] data = this.ctx.getStub().getState(ledgerKey.toString());
        if (data != null) {
            State state = this.deserializer.deserialize(data);
            return state;
        } else {
            return null;
        }
    }

    /**
     * Update a state in the list. Puts the new state in world state with
     * appropriate composite key. Note that state defines its own key. A state is
     * serialized before writing. Logic is very similar to addState() but kept
     * separate becuase it is semantically distinct.
     */
    @Override
    public StateList updateState(State state) {
        CompositeKey ledgerKey = this.ctx.getStub().createCompositeKey(this.name, state.getSplitKey());
        byte[] data = State.serialize(state);
        this.ctx.getStub().putState(ledgerKey.toString(), data);

        return this;
    }

}
