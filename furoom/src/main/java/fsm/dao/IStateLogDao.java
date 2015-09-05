package fsm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import fsm.model.StateLog;


public interface IStateLogDao {
	public void create(StateLog stateLog);
	public void delete(Integer id);
	public List<StateLog> findByRefId(@Param("type")int type,@Param("refId")Integer refId);
}
