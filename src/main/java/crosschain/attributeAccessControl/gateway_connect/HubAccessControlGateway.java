package crosschain.attributeAccessControl.gateway_connect;

import crosschain.attributeAccessControl.contract.Contract_Paillier;
import crosschain.attributeAccessControl.contract.entity.*;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import paillierp.PaillierThreshold;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class HubAccessControlGateway {

    private Connect connect;
    private Gateway gateway;
    private Network network;
    private Contract contract;
    private static HubAccessControlGateway hubAccessControlGateway;
    private static ThreshPaillier threshPaillier;

    private final static Logger logger = Logger.getLogger(HubAccessControlGateway.class.getName());

    public static void main(String[] args) throws Exception {
        //不应该加密一次一个threshPaillier
        threshPaillier=new ThreshPaillier();
//
        hubAccessControlGateway = new HubAccessControlGateway();
        //install合约时设置的这个名字

        hubAccessControlGateway.connect("AccessControlContract");
        hubAccessControlGateway.testAddPolicy();


        //再调用accessControl
        //todo 考虑该请求中属性不存在合约的情况

        //交通管理局 traffic control bureau   部长 minister   路测设备 roadside uint  路灯 street lamp
        SubjectAttribute subjectAttributeRequest=new SubjectAttribute("0001","traffic control bureau", "minister","10010","2");
        ObjectAttribute objectAttributeRequest=new ObjectAttribute("0003","roadside uint", "street lamp","30030","3");
        String operationRequest="CrossChainRead";
        String operationRequest_encry=hubAccessControlGateway.operationEncryption(operationRequest);
        Request request =new Request(subjectAttributeRequest,objectAttributeRequest,operationRequest_encry);


//        ObjectAttribute objectAttributeRequest1=new ObjectAttribute("0003","roadside uint", "street lamp","30030","3");
//        ObjectAttribute objectAttributeRequest2=new ObjectAttribute("0003","roadside uint", "street lamp","30030","3");
//        ObjectAttribute objectAttributeRequest3=new ObjectAttribute("0003","roadside uint", "street lamp","30030","3");
//        ObjectAttribute objectAttributeRequest4=new ObjectAttribute("0003","roadside uint", "street lamp","30030","3");
//        ObjectAttribute objectAttributeRequest5=new ObjectAttribute("0003","roadside uint", "street lamp","30030","3");
//        ObjectAttribute objectAttributeRequest6=new ObjectAttribute("0003","roadside uint", "street lamp","30030","3");
//        ObjectAttribute objectAttributeRequest7=new ObjectAttribute("0003","roadside uint", "street lamp","30030","3");
//        ObjectAttribute objectAttributeRequest8=new ObjectAttribute("0003","roadside uint", "street lamp","30030","3");
//        ObjectAttribute objectAttributeRequest9=new ObjectAttribute("0003","roadside uint", "street lamp","30030","3");
//        ObjectAttribute objectAttributeRequest10=new ObjectAttribute("0003","roadside uint", "street lamp","30030","3");
//        ObjectAttribute objectAttributeRequest11=new ObjectAttribute("0003","roadside uint", "street lamp","30030","3");
//        ObjectAttribute objectAttributeRequest12=new ObjectAttribute("0003","roadside uint", "street lamp","30030","3");

        long startMili=System.currentTimeMillis();// 当前时间对应的毫秒数
        SubjectAttribute subjectAttributeRequest_ = hubAccessControlGateway.subjectAttributeEncryption(subjectAttributeRequest);
        ObjectAttribute objectAttributeRequest_ =hubAccessControlGateway.objectAttributeEncryption(objectAttributeRequest);

//        ObjectAttribute objectAttributeRequest_1 =hubAccessControlGateway.objectAttributeEncryption(objectAttributeRequest1);
//        ObjectAttribute objectAttributeRequest_2=hubAccessControlGateway.objectAttributeEncryption(objectAttributeRequest2);
//        ObjectAttribute objectAttributeRequest_3=hubAccessControlGateway.objectAttributeEncryption(objectAttributeRequest3);
//
//        ObjectAttribute objectAttributeRequest_4 =hubAccessControlGateway.objectAttributeEncryption(objectAttributeRequest4);
//        ObjectAttribute objectAttributeRequest_5=hubAccessControlGateway.objectAttributeEncryption(objectAttributeRequest5);
//        ObjectAttribute objectAttributeRequest_6=hubAccessControlGateway.objectAttributeEncryption(objectAttributeRequest6);
//
//        ObjectAttribute objectAttributeRequest_7 =hubAccessControlGateway.objectAttributeEncryption(objectAttributeRequest7);
//        ObjectAttribute objectAttributeRequest_8=hubAccessControlGateway.objectAttributeEncryption(objectAttributeRequest8);
//        ObjectAttribute objectAttributeRequest_9=hubAccessControlGateway.objectAttributeEncryption(objectAttributeRequest9);
//
//        ObjectAttribute objectAttributeRequest_10 =hubAccessControlGateway.objectAttributeEncryption(objectAttributeRequest10);
//        ObjectAttribute objectAttributeRequest_11=hubAccessControlGateway.objectAttributeEncryption(objectAttributeRequest11);
//        ObjectAttribute objectAttributeRequest_12=hubAccessControlGateway.objectAttributeEncryption(objectAttributeRequest12);

        long endMili=System.currentTimeMillis();//结束时间
        System.out.println("/**总耗时为："+(endMili-startMili)+"毫秒");

        ComputeResult computeResult= hubAccessControlGateway.callCompute(subjectAttributeRequest_,objectAttributeRequest_,operationRequest_encry);

        Boolean result=hubAccessControlGateway.accessControl(computeResult);
        if(result){
            //跨链操作 可以访问
        }
        else{
            //拒绝访问请求
        }


    }
    public void testAddPolicy() throws InterruptedException, TimeoutException, ContractException, IOException {
        //先addPolicy

        //level数字越小 等级越高

        //交通管理局 traffic control bureau   部长 minister   路测设备 roadside uint  路灯 street lamp
        //非敏感数据 nonsensitive data
        //主体访问客体
        SubjectAttribute subjectAttribute1=new SubjectAttribute("0001","traffic control bureau", "minister","10010","2");
        ObjectAttribute objectAttribute1=new ObjectAttribute("0002","roadside uint","nonsensitive data","20020","3");
        String operation1="CrossChainRead";
        String operation1_encry=hubAccessControlGateway.operationEncryption(operation1);
        Environment environment1=new Environment("2023-4-13","us-101");

        SubjectAttribute subjectAttribute_1 = hubAccessControlGateway.subjectAttributeEncryption(subjectAttribute1);
        ObjectAttribute objectAttribute_1 = hubAccessControlGateway.objectAttributeEncryption(objectAttribute1);

        String policyID1=hubAccessControlGateway.callAddPolicy(subjectAttribute_1,objectAttribute_1,operation1_encry,environment1);

        Thread.currentThread().sleep(1000);

        //交通管理局 traffic control bureau   部长 minister   路测设备 roadside uint  路灯 street lamp
        SubjectAttribute subjectAttribute2=new SubjectAttribute("0001","traffic control bureau", "minister","10010","2");
        ObjectAttribute objectAttribute2=new ObjectAttribute("0003","roadside uint", "street lamp","30030","3");
        String operation2="CrossChainRead";
        String operation2_encry=hubAccessControlGateway.operationEncryption(operation2);
        Environment environment2=new Environment("2023-4-13","us-101");

        SubjectAttribute subjectAttribute_2 = hubAccessControlGateway.subjectAttributeEncryption(subjectAttribute2);
        ObjectAttribute objectAttribute_2 = hubAccessControlGateway.objectAttributeEncryption(objectAttribute2);

        String policyID2= hubAccessControlGateway.callAddPolicy(subjectAttribute_2,objectAttribute_2,operation2_encry,environment2);

        Thread.currentThread().sleep(1000);

        //交通企业 transport enterprises  经理 manager
        SubjectAttribute subjectAttribute3=new SubjectAttribute("0002","transport enterprises","manager","20002","3");
        ObjectAttribute objectAttribute3=new ObjectAttribute("0003","roadside uint", "street lamp","30030","3");
        String operation3="CrossChainRead";
        String operation3_encry=hubAccessControlGateway.operationEncryption(operation3);
        Environment environment3=new Environment("2023-4-13","us-101");


        SubjectAttribute subjectAttribute_3 = hubAccessControlGateway.subjectAttributeEncryption(subjectAttribute3);
        ObjectAttribute objectAttribute_3 = hubAccessControlGateway.objectAttributeEncryption(objectAttribute3);

        String policyID3=hubAccessControlGateway.callAddPolicy(subjectAttribute_3,objectAttribute_3,operation3_encry,environment3);

        Thread.currentThread().sleep(1000);

    }

    //连接gateway
    public void connect(String contractName) throws IOException {
        connect = new Connect();
        this.gateway = connect.connectGateway();
        this.network = connect.getNetwork(gateway);
        System.out.println(network.toString());
        this.contract = connect.getContract(network, contractName);
    }

    //调用合约
    public String callCheckAccess(SubjectAttribute subjectAttribute, ObjectAttribute objectAttribute, String operation) throws Exception {

        PaillierThreshold pubkey= threshPaillier.getDecryptionServers().get(1);
        String N=pubkey.getPublicThresholdKey().getN().toString();

        //加法因子
        BigInteger plaintext1 = new BigInteger("15");
        String ciphertext1 =threshPaillier.encrypt(pubkey,plaintext1).toString();

        System.out.println("subjectAttribute.getSecurityLevel:"+subjectAttribute.getSecurityLevel());
        System.out.println("objectAttribute.getSecurityLevel:"+objectAttribute.getSecurityLevel());
        int a= Contract_Paillier.compare(new BigInteger(subjectAttribute.getSecurityLevel()),new BigInteger(objectAttribute.getSecurityLevel()),new BigInteger(ciphertext1),new BigInteger(N));
        System.out.println("test:"+a);

        byte[] result = contract.submitTransaction("checkAccess", subjectAttribute.getChainID(),subjectAttribute.getOrganization(),subjectAttribute.getRole(),subjectAttribute.getIdentityID(),subjectAttribute.getSecurityLevel(),
                objectAttribute.getChainID(),objectAttribute.getOrganization(),objectAttribute.getType(),objectAttribute.getIdentityID(),objectAttribute.getSecurityLevel(),
                operation,ciphertext1,N);
      //  System.out.println("CheckAccessResult:"+new String(result));
        return new String(result);
    }

    //调用合约
    public ComputeResult callCompute(SubjectAttribute subjectAttribute, ObjectAttribute objectAttribute, String operation) throws Exception {

        PaillierThreshold pubkey= threshPaillier.getDecryptionServers().get(1);
        String N=pubkey.getPublicThresholdKey().getN().toString();

        //加法因子
        BigInteger plaintext1 = new BigInteger("15");
        String ciphertext1 =threshPaillier.encrypt(pubkey,plaintext1).toString();

//        System.out.println("subjectAttribute.getSecurityLevel:"+subjectAttribute.getSecurityLevel());
//        System.out.println("objectAttribute.getSecurityLevel:"+objectAttribute.getSecurityLevel());
//        int a= Contract_Paillier.compare(new BigInteger(subjectAttribute.getSecurityLevel()),new BigInteger(objectAttribute.getSecurityLevel()),new BigInteger(ciphertext1),new BigInteger(N));
//        System.out.println("test:"+a);

        byte[] result = contract.submitTransaction("compute", subjectAttribute.getChainID(),subjectAttribute.getOrganization(),subjectAttribute.getRole(),subjectAttribute.getIdentityID(),subjectAttribute.getSecurityLevel(),
                objectAttribute.getChainID(),objectAttribute.getOrganization(),objectAttribute.getType(),objectAttribute.getIdentityID(),objectAttribute.getSecurityLevel(),
                operation,ciphertext1,N);
        //  System.out.println("CheckAccessResult:"+new String(result));
        ComputeResult computeResult=ComputeResult.deserialize(result);

        System.out.println(computeResult.toString());

        return computeResult;
    }

    //调用合约
    public String callAddPolicy(SubjectAttribute subjectAttribute, ObjectAttribute objectAttribute, String operation, Environment environment) throws InterruptedException, TimeoutException, ContractException {

        byte[] result = contract.submitTransaction("addPolicy", subjectAttribute.getChainID(),subjectAttribute.getOrganization(),subjectAttribute.getRole(),subjectAttribute.getIdentityID(),subjectAttribute.getSecurityLevel(),
                objectAttribute.getChainID(),objectAttribute.getOrganization(),objectAttribute.getType(),objectAttribute.getIdentityID(),objectAttribute.getSecurityLevel(),
                operation,environment.getTime(),environment.getPlace());
        System.out.println("AddPolicyResult:"+new String(result));
        return new String(result);
    }

    //策略加密与属性加密都含有主客体 可以一起 返回加密后的数据
    public SubjectAttribute subjectAttributeEncryption(SubjectAttribute subjectAttribute) throws IOException {

        PaillierThreshold pubkey= threshPaillier.getDecryptionServers().get(1);
        BigInteger N=pubkey.getPublicThresholdKey().getN();


        String subjectAttribute_organization_hash_encry = threshPaillier.encrypt(pubkey,new BigInteger(new String(subjectAttribute.getOrganization()).getBytes())).toString();
        String subjectAttribute_role_hash_encry= threshPaillier.encrypt(pubkey,new BigInteger(new String(subjectAttribute.getRole()).getBytes())).toString();
        String subjectAttribute_securityLevel_encry= threshPaillier.encrypt(pubkey,new BigInteger(subjectAttribute.getSecurityLevel())).toString();

        subjectAttribute.setOrganization(subjectAttribute_organization_hash_encry);
        subjectAttribute.setRole(subjectAttribute_role_hash_encry);
        subjectAttribute.setSecurityLevel(subjectAttribute_securityLevel_encry);


        //返回所有加密后的数据 以及N ，或者说单个对象加密
        return subjectAttribute;

    }

    public ObjectAttribute objectAttributeEncryption(ObjectAttribute objectAttribute) throws IOException {

        PaillierThreshold pubkey= threshPaillier.getDecryptionServers().get(1);
        BigInteger N=pubkey.getPublicThresholdKey().getN();

        String objectAttribute_organization_hash_encry=  threshPaillier.encrypt(pubkey,new BigInteger(new String(objectAttribute.getOrganization()).getBytes())).toString();
        String objectAttribute_type_hash_encry=threshPaillier.encrypt(pubkey,new BigInteger(new String(objectAttribute.getType()).getBytes())).toString();
        String objectAttribute_securityLevel_encry=threshPaillier.encrypt(pubkey,new BigInteger(objectAttribute.getSecurityLevel())).toString();

        objectAttribute.setOrganization(objectAttribute_organization_hash_encry);
        objectAttribute.setType(objectAttribute_type_hash_encry);
        objectAttribute.setSecurityLevel(objectAttribute_securityLevel_encry);

        //返回所有加密后的数据 以及N ，或者说单个对象加密
        return objectAttribute;

    }

    public String operationEncryption(String operation) throws IOException {

        PaillierThreshold pubkey= threshPaillier.getDecryptionServers().get(1);
        BigInteger N=pubkey.getPublicThresholdKey().getN();

        String operation_hash_encry=  threshPaillier.encrypt(pubkey,new BigInteger(operation.getBytes())).toString();

        //返回所有加密后的数据 以及N ，或者说单个对象加密
        return operation_hash_encry;

    }
    public boolean accessControl(ComputeResult computeResult) throws IOException {
        Boolean result=false;

        //挨个解密判断
        BigInteger compareLevelResult=threshPaillier.decrypt(threshPaillier.getDecryptionServers(),new BigInteger(computeResult.getCompareLevelResult()));

        System.out.println("computeResult:"+result);
        if (compareLevelResult.compareTo(BigInteger.valueOf(15)) > 0) {
            System.out.println("M1 > M2");
            System.out.println("不满足数据安全等级，客体数据安全等级高于主体等级 ");
            result=false;
            return result;
        } else if (compareLevelResult.compareTo(BigInteger.valueOf(15)) < 0) {
            //主体等级高于客体等级 可以直接访问
            System.out.println("M1 < M2");
            System.out.println("满足数据安全等级，允许访问");
            result=true;
            return result;

        } else {
            System.out.println("M1 = M2");
            //挨个解密判断
            BigInteger compareOperationResult=threshPaillier.decrypt(threshPaillier.getDecryptionServers(),new BigInteger(computeResult.getCompareOperationResult()));

            //操作相等
            if(compareOperationResult.compareTo(BigInteger.ZERO)==0){
                BigInteger compareOrganizationResult=threshPaillier.decrypt(threshPaillier.getDecryptionServers(),new BigInteger(computeResult.getCompareOrganizationResult()));
                //非跨链
                if(compareOrganizationResult.compareTo(BigInteger.ZERO)==0){
                    System.out.println("非跨链情况，允许访问");
                    result=true;
                    return result;
                }
                else {
                    //跨链   可以把操作也分等级 然后根据操作大小判断
                    System.out.println("满足操作行为，允许访问");
                    result=true;
                    return result;
                }
            }
            else {
                System.out.println("不满足操作行为，拒绝访问");
                result=false;
                return result;
            }
        }

    }
}
