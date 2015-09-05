package fsm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import fsm.model.AgentProduct;

public interface IAgentProductDao {
	public void add(@Param("userId")Integer userId, @Param("productId")Integer productId);
	public void remove(@Param("userId")Integer userId, @Param("productId")Integer productId);
	public List<AgentProduct> findAllByAgent(@Param("userId")Integer userId);
	public List<AgentProduct> findAllByProduct(@Param("productId")Integer productId);
	public AgentProduct find(@Param("userId")Integer userId, @Param("productId")Integer productId);
}