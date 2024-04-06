package crosschain.attributeAccessControl.contract.state;

@FunctionalInterface
public interface StateDeserializer {
    State deserialize(byte[] buffer);
}
