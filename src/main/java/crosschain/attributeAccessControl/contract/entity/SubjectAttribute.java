package crosschain.attributeAccessControl.contract.entity;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public class SubjectAttribute {

    //主体 数据访问者

    //链ID
    @Property()
    private String chainID;

    //所属机构
    @Property()
    private String organization;

    //角色
    @Property()
    private String role;

    //身份标识
    @Property()
    private String identityID;

    //安全等级
    @Property()
    private String securityLevel;

    public SubjectAttribute() {
        super();
    }

    public SubjectAttribute(String chainID, String organization, String role, String identityID, String securityLevel) {
        this.chainID = chainID;
        this.organization = organization;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
