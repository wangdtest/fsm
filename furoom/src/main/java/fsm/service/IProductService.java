package fsm.service;

import java.util.List;
import java.util.Map;

import fsm.model.Product;
import fsm.model.ProductAction;
import fsm.model.User;

public interface IProductService {
	public Map<String, Object> queryAllByTypeAndCityAndStateAndOrder(int loanType, String city, int state, int orderBy, int offset, int recnum) throws Exception;
	public Product find(Integer id);
	public Integer createProduct(String title, int loanType, String profits, String requires) throws Exception;
	public void updateProduct(Integer productId, String title, int loanType, String profits, String requires) throws Exception;
	public void addProductAction(int ind, Integer productId, String title, String description, int estimatedTime) throws Exception;
	
	
	public void applyAuditProduct(Integer productId) throws Exception;
	public void auditProduct(Integer productId, boolean pass) throws Exception;
	
	public List<User> queryAllAgentsByProduct(Integer id) throws Exception;
	public List<Product> queryAllMyProductByState(int state) throws Exception;
	public List<Product> queryAllByOrgAndState(Integer organizationId, int state) throws Exception;
	public List<Product> queryAllByOrgAndCityAndState(Integer organizationId, String cityCode, int state) throws Exception;
	public List<Product> queryAllByState(int state);
	public void addRelationTo(Integer id) throws Exception;
	public void removeRelationTo(Integer id) throws Exception;
}
