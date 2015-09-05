package fsm.service;

import java.util.List;
import java.util.Map;

import fsm.model.Operation;

public interface IOperationService {
	
	public Map<String, Object> findAllOperationByUserAndStateAndType(Integer userId, int state,int loanType, int offset, int recnum);
	
	public List<Operation> findAllOperationByAgentAndStateAndType(int state, int loanType) throws Exception;
	
	public Operation find(Integer operationId);
	
	/**
	 * 会话相关的服务，必须是登录用户才能执行
	 * 只填写了贷款属性的操作，没有选定具体的贷款产品
	 * 
	 * 
	 * @param loanType 贷款类型
	 * @param amount   贷款金额
	 * @param period   贷款期限
	 * @param province 所在省代码
	 * @param city     所在市代码
	 * */
	public Integer applyOperation(int loanType, int amount, int period, String province, String city) throws Exception;
	/**
	 * 给某个的操作绑定产品，附加的会为该操作创建跟产品步骤一样的操作步骤
	 * @param operationId 操作Id
	 * @param productId   产品Id
	 * 
	 * */
	public void bindAgentAndProductToOperation(Integer operationId, Integer userId, Integer productId) throws Exception;
	
	/**
	 * 业务员审核某个操作请求是否真实
	 * @param operationId 操作Id
	 * @param pass        是否审核通过
	 * 
	 * */
	public void auditOperation(Integer operationId, boolean pass) throws Exception;
	
	/**
	 * 信贷经理接管融资操作
	 * 
	 * @param operationId 操作Id
	 * */
	public void takeoverOperation(Integer operationId) throws Exception;
	
	/**
	 * 修改贷款操作的属性值
	 * @param operationId 操作Id
	 * @param productId   产品Id null代表不修改
	 * @param loanType    贷款类型  -1代表不修改
	 * @param amount      贷款金额  -1代表不修改
	 * @param period      贷款期限  -1代表不修改
	 * @param province    所在省代码 null代表不修改
	 * @param city        所在市代码 null代表不修改
	 * */
	public void updateOperation(Integer operationId,Integer productId, int loanType, int amount, int period, String province, String city) throws Exception;
	
	/**
	 * 重新申请贷款操作
	 * 
	 * @param operationId  操作Id
	 * */
	public void reApplyOperation(Integer operationId) throws Exception;
	
	/**
	 * 信贷经理接管后的operationAction每个步骤的完结，并根据相应情况修改operation的状态。完结状态为STATE_DONE = 4; STATE_EXCEPTION = 5;
	 * 
	 * 
	 * @param operationActionId 操作步骤Id
	 * @param state       		操作步骤的状态
	 * @Param description		描述
	 * 
	 * */
	public void completeOperationAction(Integer operationActionId, int state, String description) throws Exception;
	
}
