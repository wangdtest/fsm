package fsm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fsm.dao.IAgentProductDao;
import fsm.dao.IOperationActionDao;
import fsm.dao.IOperationDao;
import fsm.dao.IProductActionDao;
import fsm.dao.IProductDao;
import fsm.dao.IUserDao;
import fsm.model.AgentProduct;
import fsm.model.Operation;
import fsm.model.OperationAction;
import fsm.model.Product;
import fsm.model.ProductAction;
import fsm.model.StateLog;
import fsm.model.User;
import fsm.service.IOperationService;
import fsm.service.IUserService;
import fsm.support.service.IStateLogService;
import fsm.tools.Pagination;
@Service
public class OperationServiceImpl implements IOperationService {
	@Autowired
	IOperationDao operationDao;
	@Autowired
	IOperationActionDao actionDao;
	@Autowired
	IProductDao productDao;
	@Autowired
	IProductActionDao productActionDao;
	@Autowired
	IUserDao userDao;
	@Autowired
	IAgentProductDao apDao;
	@Autowired
	IUserService userService;
	@Autowired
	IStateLogService stateLogService;
	@Override
	public Map<String, Object> findAllOperationByUserAndStateAndType(Integer userId,int state,int loanType, int offset, int recnum) {
		int count = operationDao.countByUserIdAndStateAndType(userId, state, loanType);
		if(count==0)
			return Pagination.buildResult(null, count, offset, recnum);
		List<Operation> resList = operationDao.findAllByUserIdAndStateAndType(userId, state, loanType, offset, recnum);
		return Pagination.buildResult(resList,count,offset, recnum);
	}
	
	@Override
	public List<Operation> findAllOperationByAgentAndStateAndType(int state, int loanType) throws Exception{
		User user = userService.getCurrentUser();
		if(user==null){
			throw new Exception("无权操作");
		}
		List<Operation> ops = operationDao.findAllByAgentIdAndStateAndType(user.getId(), state, loanType);
		if(ops==null){
			return new ArrayList<Operation>();
		}else{
			return ops;
		}
	}

	@Override
	public Operation find(Integer operationId) {
		Operation operation = operationDao.find(operationId);
		if(operation==null)
		return null;
		
		List<OperationAction> actions = actionDao.findAll(operationId);
		if(actions!=null){
			operation.setActions(actions);
		}
		return operation;
	}

	@Override
	public Integer applyOperation(int loanType, int amount, int period, String province, String city) throws Exception {
		User user = userService.getCurrentUser();
		if(user==null || user.getPrivilege()!=User.PRIVILEGE_USER){
			throw new Exception("没有相关权限");
		}
		Operation operation = new Operation();
		operation.setAmount(amount);
		operation.setCity(city);
		operation.setProvince(province);
		operation.setCreatetime(System.currentTimeMillis());
		operation.setLoanType(loanType);
		operation.setPeriod(period);
		operation.setUserId(user.getId());
		operation.setState(Operation.STATE_APPLY);
		operationDao.create(operation);
		stateLogService.create(operation.getId(), operation.getState(), StateLog.TYPE_OPERATION);
		return operation.getId();
	}

	@Override
	public void bindAgentAndProductToOperation(Integer operationId, Integer userId, Integer productId) throws Exception {
		User user = userService.getCurrentUser();
		if(user==null || user.getPrivilege()!=User.PRIVILEGE_USER){
			throw new Exception("没有相关权限");
		}
		Operation operation = operationDao.find(operationId);
		if(operation==null || user.getId()!=operation.getUserId()){
			throw new Exception("无权对相关操作执行动作");
		}
		
		User agent = userDao.find(userId);
		if(agent==null || agent.getPrivilege()!=User.PRIVILEGE_AGENT_AUDIT_SUCCESS){
			throw new Exception("信贷经理尚未通过审核，无法绑定");
		}
		Product product = productDao.find(productId);
		if(product==null || product.getState()!=Product.STATE_AUDIT_PASS){
			throw new Exception("产品状态不对，无法绑定");
		}
		
		AgentProduct ap = apDao.find(userId, productId);
		if(ap==null){
			throw new Exception("信贷经理未提供相应的产品");
		}
		
		List<ProductAction> actions = productActionDao.findAll(productId);
		if(actions!=null)
		{
			for(ProductAction action : actions){
				OperationAction ac = new OperationAction();
				ac.setCreateTime(System.currentTimeMillis());
				ac.setDescription(action.getDescription());
				ac.setOperationId(operationId);
				ac.setState(OperationAction.STATE_INIT);
				ac.setTitle(action.getTitle());
				actionDao.create(ac);
			}
		}
		//将产品和产品信贷经理都绑定到贷款操作上
		operationDao.update(operationId, productId, userId, -1, -1, -1, null, null, System.currentTimeMillis());
	}

	@Override
	public void auditOperation(Integer operationId, boolean pass) throws Exception {
		User user = userService.getCurrentUser();
		if(user==null || user.getPrivilege()!=User.PRIVILEGE_OPERATOR){
			throw new Exception("没有相关权限");
		}
		Operation operation = operationDao.find(operationId);
		if(operation.getState()!=Operation.STATE_APPLY){
			throw new Exception("状态不对，无法审核Operation");
		}
		if(pass==true){
			operationDao.changeState(operationId, Operation.STATE_AUDIT_PASS, System.currentTimeMillis());
			stateLogService.change(operationId, Operation.STATE_APPLY, Operation.STATE_AUDIT_PASS, StateLog.TYPE_OPERATION);
		}else{
			operationDao.changeState(operationId, Operation.STATE_AUDIT_REFUSE, System.currentTimeMillis());
			stateLogService.change(operationId, Operation.STATE_APPLY, Operation.STATE_AUDIT_REFUSE, StateLog.TYPE_OPERATION);
		}
	}

	@Override
	public void takeoverOperation(Integer operationId) throws Exception {
		User user = userService.getCurrentUser();
		if(user==null || user.getPrivilege()!=User.PRIVILEGE_AGENT_AUDIT_SUCCESS){
			throw new Exception("没有相关权限");
		}
		
		Operation operation = operationDao.find(operationId);
		if(operation==null || operation.getState()!=Operation.STATE_AUDIT_PASS){
			throw new Exception("贷款申请状态不对，无法接管");
		}
		
		if(user.getId()!=operation.getAgentId()){
			throw new Exception("没有相关权限");
		}
		
		operationDao.changeState(operationId, Operation.STATE_PROCESSING, System.currentTimeMillis());
		stateLogService.change(operationId, Operation.STATE_AUDIT_PASS, Operation.STATE_PROCESSING, StateLog.TYPE_OPERATION);
	}

	@Override
	public void updateOperation(Integer operationId, Integer productId,
			int loanType, int amount, int period, String province, String city)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void reApplyOperation(Integer operationId) throws Exception {
		User user = userService.getCurrentUser();
		if(user==null || user.getPrivilege()!=User.PRIVILEGE_USER){
			throw new Exception("没有相关权限");
		}
		
		Operation operation = operationDao.find(operationId);
		if(operation==null || operation.getState()!=Operation.STATE_AUDIT_REFUSE){
			throw new Exception("贷款申请状态不对，无法重新申请");
		}
		
		operationDao.changeState(operationId, Operation.STATE_APPLY, System.currentTimeMillis());
		stateLogService.change(operationId, Operation.STATE_AUDIT_REFUSE, Operation.STATE_APPLY, StateLog.TYPE_OPERATION);
	}
	
	
	@Override
	public void completeOperationAction(Integer operationActionId, int state, String description) throws Exception{
		User user = userService.getCurrentUser();
		if(user==null || user.getPrivilege()!=User.PRIVILEGE_AGENT_AUDIT_SUCCESS){
			throw new Exception("没有相关权限");
		}
		OperationAction action = actionDao.find(operationActionId);
		if(action==null){
			throw new Exception("没有相关步骤");
		}
		int sourceState = action.getState();
		Operation operation = find(action.getOperationId());
		if(operation==null || operation.getState()!=Operation.STATE_PROCESSING){
			throw new Exception("贷款申请状态不对，无法关闭");
		}
		
		if(user.getId()!=operation.getAgentId()){
			throw new Exception("没有相关权限");
		}
		
		if(action.getState()==OperationAction.STATE_INIT&&state==OperationAction.STATE_PROCESSING){
			actionDao.changeState(operationActionId, OperationAction.STATE_PROCESSING, (new Date()).toLocaleString()+description);
		}else if(action.getState()==OperationAction.STATE_PROCESSING&&state==OperationAction.STATE_DONE){
			actionDao.changeState(operationActionId, OperationAction.STATE_DONE, (new Date()).toLocaleString()+description);
			boolean wholeDone = true;
			for(OperationAction oa : operation.getActions()){
				if(oa.getId()==operationActionId){
					continue;
				}else if(oa.getState()!=OperationAction.STATE_DONE){
					wholeDone=false;
					break;
				}
			}
			if(wholeDone){
				operationDao.changeState(operation.getId(), Operation.STATE_DONE, System.currentTimeMillis());
				stateLogService.change(operation.getId(), Operation.STATE_PROCESSING, Operation.STATE_DONE, StateLog.TYPE_OPERATION);
			}
		}else if(action.getState()==OperationAction.STATE_PROCESSING&&state==OperationAction.STATE_EXCEPTION){
			actionDao.changeState(operationActionId, OperationAction.STATE_EXCEPTION, (new Date()).toLocaleString()+description);
			operationDao.changeState(operation.getId(), Operation.STATE_EXCEPTION, System.currentTimeMillis());
			stateLogService.change(operation.getId(), Operation.STATE_PROCESSING, Operation.STATE_EXCEPTION, StateLog.TYPE_OPERATION);
		}else{
			throw new Exception("动作的状态转换不合法");
		}
		
		stateLogService.change(operationActionId, sourceState, action.getState(), StateLog.TYPE_OPERATIONACTION);
	}
}
