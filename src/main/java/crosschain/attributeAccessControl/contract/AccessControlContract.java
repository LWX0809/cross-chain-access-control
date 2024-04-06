package crosschain.attributeAccessControl.contract;


import crosschain.attributeAccessControl.contract.entity.*;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.logging.Logger;

@Contract(name = "AccessControlContract")
@Default
public class AccessControlContract implements ContractInterface {

    private final static Logger logger = Logger.getLogger(AccessControlContract.class.getName());
    private final String Version="v1.0.0";
    //    private int policyNum;

    //todo 有问题 一直是空的 初始化之后也不行 与solidity不同  可以像policylist一样设置成对象？ policylist与arr都设置成ctx对象
    //private Map <String,PolicyList> policyListArr;
    //private PolicyList policyList;

    @Override
    public Context createContext(ChaincodeStub stub) {
        return new AccessControlContext(stub);
    }

    public AccessControlContract() {

    }

    @Transaction
    public void init(AccessControlContext ctx){
//        this.policyList=new PolicyList(ctx);
//        this.policyList.setPolicyNum(0);
//        this.policyListArr=new HashMap<String,PolicyList>();
        ChaincodeStub stub=ctx.getStub();
        stub.putStringState("num","0");
        logger.info("AccessControl contract init");
    }

    //参数解析  数据在输入时都应为string int  除了chainID 输入的应该是全部都是密文 且是string类型的密文
    //todo 应该是二维数据 有问题
    @Transaction
    public String addPolicy(AccessControlContext ctx, String subjectAttribute_chainID,String subjectAttribute_organization_hash_encry,String subjectAttribute_role_hash_encry,String subjectAttribute_identityID ,String subjectAttribute_securityLevel_encry,
                            String objectAttribute_chainID,String objectAttribute_organization_hash_encry ,String objectAttribute_type_hash_encry , String objectAttribute_identityID , String objectAttribute_securityLevel_encry ,
                            String operation, String environment_time , String environment_place) throws Exception {

        SubjectAttribute subjectAttribute=new SubjectAttribute(subjectAttribute_chainID,subjectAttribute_organization_hash_encry,subjectAttribute_role_hash_encry,subjectAttribute_identityID,subjectAttribute_securityLevel_encry);

        ObjectAttribute objectAttribute=new ObjectAttribute(objectAttribute_chainID,objectAttribute_organization_hash_encry,objectAttribute_type_hash_encry,objectAttribute_identityID,objectAttribute_securityLevel_encry);

        Environment environment=new Environment(environment_time,environment_place);

        //todo 可能会有bug 是否需要判断policylist为空 或者未初始化 不能加客体编号
//        String policyListArrID=objectAttribute.getChainID()+":"+objectAttribute.getIdentityID()+"_";
//        logger.info("policyListArrID:"+policyListArrID);

//        if(!ctx.policyListArr.containsKey(policyListArrID)){
//            ctx.policyList=new PolicyList(ctx);
//            ctx.policyList.setPolicyNum(0);
//            logger.info("!policyListArr.containsKey(policyListArrID):"+policyListArrID);
//        }
//        else {
//            ctx.policyList = ctx.policyListArr.get(policyListArrID);
//            logger.info("policyListArr.containsKey(policyListArrID):"+policyListArrID);
//        }
//        logger.info("policyList:"+ctx.policyList.toString());
//        logger.info("num:"+ctx.policyList.getPolicyNum());

//        int num=ctx.policyList.getPolicyNum()+1;

//        ChaincodeStub stub=ctx.getStub();
//        String num=stub.getStringState("num");
//        int num1=Integer.parseInt(num);
//        num1++;
//        stub.putStringState("num", String.valueOf(num1));
//        String policyID=objectAttribute.getChainID()+":"+objectAttribute.getIdentityID()+"_"+num1;
        String policyID=objectAttribute.getChainID()+":"+objectAttribute.getIdentityID()+"_"+subjectAttribute.getChainID()+":"+subjectAttribute.getIdentityID();

        Policy policy=new Policy(policyID,subjectAttribute,objectAttribute,operation,environment);

        ctx.policyList.addPolicy(policy);
//        ctx.policyList.setPolicyNum(num);

//        ctx.policyListArr.put(policyListArrID,ctx.policyList);
//        logger.info("addPolicy policyListArr.length : "+ctx.policyListArr.size());

//        logger.info("after add num:"+stub.getStringState("num"));

        return policyID;
    }

    //todo 怎么得到原policy的id
    @Transaction
    public boolean updatePolicy(AccessControlContext ctx, String subjectAttribute_chainID,String subjectAttribute_organization_hash_encry,String subjectAttribute_role_hash_encry,String subjectAttribute_identityID ,String subjectAttribute_securityLevel_encry,
                                String objectAttribute_chainID,String objectAttribute_organization_hash_encry ,String objectAttribute_type_hash_encry , String objectAttribute_identityID , String objectAttribute_securityLevel_encry ,
                                String operation, String environment_time , String environment_place){

        return true;
    }

    //根据请求以及策略 根据id定位策略 传的都是密文
    //参数不能是request类型 前端命令行输入 无法解析
    //同态加法判断 等级需要进行同态加法  非等级直接判断密文就行  注意一个字符串不同时期生成的密文就一定一样吗
    @Transaction
    public boolean checkAccess(AccessControlContext ctx, String subjectAttribute_chainID,String subjectAttribute_organization_hash_encry,String subjectAttribute_role_hash_encry,String subjectAttribute_identityID ,String subjectAttribute_securityLevel_encry,
                               String objectAttribute_chainID,String objectAttribute_organization_hash_encry ,String objectAttribute_type_hash_encry , String objectAttribute_identityID , String objectAttribute_securityLevel_encry , String operation,String cipher,String n) throws Exception {

        SubjectAttribute subjectAttribute=new SubjectAttribute(subjectAttribute_chainID,subjectAttribute_organization_hash_encry,subjectAttribute_role_hash_encry,subjectAttribute_identityID,subjectAttribute_securityLevel_encry);

        ObjectAttribute objectAttribute=new ObjectAttribute(objectAttribute_chainID,objectAttribute_organization_hash_encry,objectAttribute_type_hash_encry,objectAttribute_identityID,objectAttribute_securityLevel_encry);

        Request request=new Request(subjectAttribute,objectAttribute,operation);

//        String policyListArrID=objectAttribute.getChainID()+":"+objectAttribute.getIdentityID()+"_";
//
//        logger.info("accessContract:"+"policyListArrID:"+policyListArrID);
//        logger.info("accessContract:"+"policyListArr.length:"+ctx.policyListArr.size());

//
//        if(!ctx.policyListArr.containsKey(policyListArrID)){
//            logger.info("accessContract:"+"该策略不存在");
//            return false;
//        }
//        ctx.policyList=ctx.policyListArr.get(policyListArrID);
//
//        logger.info("accessContract:"+"policyList:"+ctx.policyList.toString());
//        ChaincodeStub stub=ctx.getStub();
//        String num=stub.getStringState("num");
//        logger.info("accessContract:"+"policyListnum:"+stub.getStringState("num"));
//        int num1=Integer.parseInt(num);

        //策略结果 操作包括读、写、编辑、删除、复制、执行和修改。(不能有删除操作)
//        String operationResult=null;

        //遍历该策略对应的list
        boolean opeResult=false;
        BigInteger subjectAttribute_sl;
        BigInteger objectAttribute_sl;
        BigInteger N=new BigInteger(n);
        BigInteger ciphertext=new BigInteger(cipher);
        Policy policy;
        String policyID;
        //属于该主体 客体的每一个策略都遍历 如果遍历完没有找到对应policy 则不能访问
        //todo 是否会出现客体针对一个主体有多个策略 --证明不能中途停止 必须遍历完  设置客体对应的主体只有一个访问策略
        //todo 逻辑有问题
       // loop: for(int i=1;i<=num1;i++){

            subjectAttribute_sl=new BigInteger(subjectAttribute_securityLevel_encry);
            objectAttribute_sl=new BigInteger(objectAttribute_securityLevel_encry);
            policyID=objectAttribute.getChainID()+":"+objectAttribute.getIdentityID()+"_"+subjectAttribute.getChainID()+":"+subjectAttribute.getIdentityID();

            //todo 判断策略不存在或者为空的情况

//            if(ctx.policyList.getPolicy(policyID)==null){
//                if(i==num1){
//                    logger.info("该策略不存在");
//                    opeResult=false;
//                    break loop;
//                }
//                continue ;
//            }

        logger.info("policyID   :   "+policyID);
        policy=ctx.policyList.getPolicy(policyID);

        logger.info("policy   :   "+policy.getPolicyID()+"      " +policy.getOperation());
            //todo 先判断是否是对应的策略

        int compareResultLevel= Contract_Paillier.compare(subjectAttribute_sl,objectAttribute_sl,ciphertext,N);
        logger.info("compareResultLevel   :   "+compareResultLevel);
            //判断数据安全的等级，若主体（数据访问者）大于客体（数据拥有者） 都可以访问 包括跨链写
            if(compareResultLevel==1){
//                operationResult="CrossChainWrite";
                opeResult=true;
                logger.info("满足数据安全等级");
                return opeResult;
            }
            else if(compareResultLevel==0){//级别相同时 对比访问策略
                //后续的密文不需要进行加法计算 直接字符串对比就行
                //todo 有问题 在操作对比那里
                if(policy.getOperation().equals(request.getOperation())){  //后续涉及nlp？
                    if(policy.getObjectAttribute().getOrganization().equals(request.getSubjectAttribute().getOrganization())){
                        //非跨链
                        logger.info("非跨链情况");
                        opeResult=true;
                        return opeResult;
                    }
                    else{
                        //判断主体角色与客体等级

                        //判断组织等级

                        //判断操作 only write or read  ，update相当于write  ，delete不允许
                        if(request.getOperation().equals("CrossChainWrite")||request.getOperation().equals("CrossChainUpdate")){
                            if(policy.getOperation().equals("CrossChainRead")){
                                logger.info("该数据只允许读操作");
                                opeResult=false;
                                return opeResult;
                            }
                            else{
                                //操作允许
                                logger.info("允许操作");
                                opeResult=true;
                                return opeResult;
                            }

                        }
                        else if(request.getOperation().equals("CrossChainRead")){
                            if(policy.getOperation().equals("CrossChainRead")||policy.getOperation().equals("CrossChainWrite")||policy.getOperation().equals("CrossChainUpdate")){
                                logger.info("允许操作");
                                opeResult=true;
                                return opeResult;
                            }
                            else {
                                logger.info("不合法的操作");
                                opeResult=false;
                                return opeResult;
                            }

                        }
                        else if(request.getOperation().equals("CrossChainDelete")){
                            logger.info("跨链不允许删除操作");
                            opeResult=false;
                            return opeResult;
                        }
                        else{
                            logger.info("没有操作数据");
                            return opeResult;
                        }
                    }
                }
            }
            else { //客体数据级别更高，不能访问 任何操作都不行 包括跨链读
                opeResult=false;
//                operationResult="null";
                logger.info("不满足数据安全等级，客体数据安全等级高于主体等级");
                return opeResult;
            }


        return opeResult;
    }

    //计算差值 返回差值密文 线下解密
    //返回list 所有的差异集合 在hub端进行判断
    @Transaction
    public String compute(AccessControlContext ctx, String subjectAttribute_chainID,String subjectAttribute_organization_hash_encry,String subjectAttribute_role_hash_encry,String subjectAttribute_identityID ,String subjectAttribute_securityLevel_encry,
                               String objectAttribute_chainID,String objectAttribute_organization_hash_encry ,String objectAttribute_type_hash_encry , String objectAttribute_identityID , String objectAttribute_securityLevel_encry , String operation,String cipher,String n) throws Exception {

        SubjectAttribute subjectAttribute=new SubjectAttribute(subjectAttribute_chainID,subjectAttribute_organization_hash_encry,subjectAttribute_role_hash_encry,subjectAttribute_identityID,subjectAttribute_securityLevel_encry);

        ObjectAttribute objectAttribute=new ObjectAttribute(objectAttribute_chainID,objectAttribute_organization_hash_encry,objectAttribute_type_hash_encry,objectAttribute_identityID,objectAttribute_securityLevel_encry);

        Request request=new Request(subjectAttribute,objectAttribute,operation);


        BigInteger N=new BigInteger(n);
        BigInteger ciphertext=new BigInteger(cipher);

        //按理说应该是在合约中查找策略的
        BigInteger subjectAttribute_sl=new BigInteger(subjectAttribute_securityLevel_encry);
        BigInteger objectAttribute_sl=new BigInteger(objectAttribute_securityLevel_encry);

        String policyID=objectAttribute.getChainID()+":"+objectAttribute.getIdentityID()+"_"+subjectAttribute.getChainID()+":"+subjectAttribute.getIdentityID();
        logger.info("policyID   :   "+policyID);
        Policy policy=ctx.policyList.getPolicy(policyID);
        logger.info("policy   :   "+policy.getPolicyID()+"      " +policy.getOperation());
        //todo 先判断是否是对应的策略

        String[] result=new String[3];

        //等级差异 与加法因子对比
        String compareLevelResult = Contract_Paillier.compute(subjectAttribute_sl,objectAttribute_sl,ciphertext,N);

        //操作差异 与0对比 BigInteger.ZERO
        String compareOperationResult = Contract_Paillier.sub(new BigInteger(policy.getOperation()),new BigInteger(request.getOperation()),N).toString();

        //组织差异 相等就是非跨链 不相等就是跨链
        String compareOrganizationResult = Contract_Paillier.sub(new BigInteger(policy.getObjectAttribute().getOrganization()),new BigInteger(request.getSubjectAttribute().getOrganization()),N).toString();

        ComputeResult computeResult=new ComputeResult(compareLevelResult,compareOperationResult,compareOrganizationResult);

        String jsonStr = new JSONObject(computeResult).toString();
        return jsonStr;
    }
}
