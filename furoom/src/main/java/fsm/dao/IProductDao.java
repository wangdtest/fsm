package fsm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import fsm.model.Product;

public interface IProductDao {
	
	public static final int ORDER_BY_FEE = 0;
	public static final int ORDER_BY_SUCCESSRATE = 1;
	public static final int ORDER_BY_COSTTIME = 2;
	public static final int ORDER_BY_STEPS = 3;
	
	public int countAllByState(@Param("state")int state);
	public List<Product> findAllByState(@Param("offset")int offset,@Param("recnum")int recnum,@Param("state")int state);
	public int countAllByCityAndState(@Param("cityCode")String cityCode,@Param("state")int state);
	public List<Product> findAllByCityAndState(@Param("offset")int offset,@Param("recnum")int recnum, @Param("cityCode")String cityCode,@Param("state")int state);
	public int countAllByOrganizationIdAndState(Integer organizationId,@Param("state")int state);
	public List<Product> findAllByOrganizationIdAndState(@Param("offset")int offset,@Param("recnum")int recnum, @Param("organizationId")Integer organizationId,@Param("state")int state);
	public int countAllByTypeAndCityAndState(@Param("loanType")int loanType, @Param("cityCode")String cityCode, @Param("state")int state);
	public List<Product> findAllByTypeAndCityAndState(@Param("loanType")int loanType, @Param("cityCode")String cityCode, @Param("state")int state);
	
	public List<Product> findAllByOrganizationIdAndCityAndState(@Param("organizationId")Integer organizationId, @Param("cityCode")String cityCode, @Param("state")int state);
	
	public Product find(Integer id);
	public void create(Product product);
	public void update(@Param("id")Integer id, @Param("title")String title, @Param("loanType")int loanType, @Param("profits")String profits, @Param("requires")String requires);
	public void changeState(@Param("id")Integer id, @Param("state")int state);
}
