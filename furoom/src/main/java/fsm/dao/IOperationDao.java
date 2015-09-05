package fsm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import fsm.model.Operation;

public interface IOperationDao {
	public int countByUserIdAndStateAndType(@Param("userId")Integer userId, @Param("state")int state, @Param("loanType")int loanType);
	public List<Operation> findAllByUserIdAndStateAndType(@Param("userId")Integer userId, @Param("state")int state, @Param("loanType")int loanType, @Param("offset")int offset, @Param("recnum")int recnum);
	
	public List<Operation> findAllByAgentIdAndStateAndType(@Param("agentId")Integer agentId, @Param("state")int state, @Param("loanType")int loanType);
	
	public Operation find(Integer id);
	public void create(Operation action);
	public void changeState(@Param("id")Integer id, @Param("state")int state, @Param("lastmodifytime")long lastmodifytime);
	public void update(@Param("id")Integer id, @Param("productId")Integer productId, @Param("agentId")Integer agentId, @Param("loanType")int loanType, @Param("amount")int amount, @Param("period")int period, @Param("province")String province, @Param("city")String city, @Param("lastmodifytime")long lastmodifytime);
}
