package fsm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import fsm.model.OperationAction;

public interface IOperationActionDao {
	public List<OperationAction> findAll(@Param("operationId")Integer operationId);
	public OperationAction find(Integer id);
	public void create(OperationAction action);
	public void remove(Integer id);
	public void changeState(@Param("id")Integer id, @Param("state")int state, @Param("description")String description);
}
