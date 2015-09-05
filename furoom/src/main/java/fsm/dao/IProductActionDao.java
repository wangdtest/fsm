package fsm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import fsm.model.ProductAction;

public interface IProductActionDao {
	public List<ProductAction> findAll(@Param("productId")Integer productId);
	public ProductAction find(Integer id);
	public void create(ProductAction action);
	public void remove(Integer id);
}
