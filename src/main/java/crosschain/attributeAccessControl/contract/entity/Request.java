package crosschain.attributeAccessControl.contract.entity;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType()
public class Request {

    //主体客体对象已经标识了链id  组织

    //主体属性
    @Property()
    private SubjectAttribute subjectAttribute;

    //客体属性
    @Property()
    private ObjectAttribute objectAttribute;

    @Property()
    private String operation;

    public Request() {
        super();
    }

    public Request(SubjectAttribute subjectAttribute, ObjectAttribute objectAttribute, String operation) {
        this.subjectAttribute = subjectAttribute;
        this.objectAttribute = objectAttribute;
        this.operation = operation;
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
}
