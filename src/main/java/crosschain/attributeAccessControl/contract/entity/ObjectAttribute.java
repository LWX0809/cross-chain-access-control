package crosschain.attributeAccessControl.contract.entity;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType()
public class ObjectAttribute {

    //客体 数据拥有者

    //链ID
    @Property()
    private String chainID;

    //发布人所属机构
    @Property()
    private String organization;

    //数据类型
    @Property()
    private String type;

    //标识
    @Property()
    private String identityID;

    //安全等级
    @Property()
    private String securityLevel;

    public ObjectAttribute() {
        super();
    }

    public ObjectAttribute(String chainID, String organization, String type, String identityID, String securityLevel) {
        this.chainID = chainID;
        this.organization = organization;
        this.type = type;
        this.identityID = identityID;
        this.securityLevel = securityLevel;
    }

    public String getChainID() {
        return chainID;
    }

    public void setChainID(String chainID) {
        this.chainID = chainID;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentityID() {
        return identityID;
    }

    public void setIdentityID(String identityID) {
        this.identityID = identityID;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }
}
