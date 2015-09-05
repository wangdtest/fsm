package fsm.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fsm.dao.IAgentProductDao;
import fsm.dao.IOrganizationDao;
import fsm.dao.IProductActionDao;
import fsm.dao.IProductDao;
import fsm.dao.IUserDao;
import fsm.model.AgentProduct;
import fsm.model.Organization;
import fsm.model.Product;
import fsm.model.ProductAction;
import fsm.model.StateLog;
import fsm.model.User;
import fsm.service.IProductService;
import fsm.service.IUserService;
import fsm.support.service.IStateLogService;
import fsm.tools.Pagination;
@Service
public class ProductServiceImpl implements IProductService {
	@Autowired
	IProductDao productDao;
	@Autowired
	IProductActionDao productActionDao;
	@Autowired
	IOrganizationDao organizationDao;
	@Autowired
	IAgentProductDao apDao;
	@Autowired
	IUserDao userDao;
	@Autowired
	IUserService userService;
	@Autowired
	IStateLogService stateLogService;
	@Override
	public Map<String, Object> queryAllByTypeAndCityAndStateAndOrder(
			int loanType, String city, int state, int orderBy, int offset,
			int recnum) throws Exception{
		List<Product> products = productDao.findAllByTypeAndCityAndState(loanType, city, state);
		
		if(products==null || products.isEmpty()){
			return Pagination.buildResult(null, 0, offset, recnum);
		}
		
		if(orderBy==IProductDao.ORDER_BY_STEPS){
			//TODO:内存中按步骤数排序
		}else if(orderBy==IProductDao.ORDER_BY_COSTTIME){
			//TODO:内存中按预计花费时间排序
		}else if(orderBy==IProductDao.ORDER_BY_FEE){
			//TODO:内存中按费用排序
		}else if(orderBy==IProductDao.ORDER_BY_SUCCESSRATE){
			//TODO:内存中按成功率排序
		}else{
			throw new Exception("不支持的排序值");
		}
		int count = products.size();
		List<Product> res = new ArrayList<Product>();
		for(int i=offset; i<offset+recnum;i++){
			if(i<count)
			{
				Product product = products.get(i);
				Organization org = organizationDao.find(product.getOrganizationId());
				product.setCompanyname(org.getName());
				String[] reqs = product.getRequires()==null?null:product.getRequires().split(";");
				product.setRequiresArray(reqs);
				String[] pros = product.getProfits()==null?null:product.getProfits().split(";");
				product.setProfitsArray(pros);
				res.add(products.get(i));
			}
		}
		
		return Pagination.buildResult(res, count, offset, recnum);
	}


	@Override
	public Product find(Integer id) {
		Product product = productDao.find(id);
		if(product==null)
			return null;
		List<ProductAction> actions = productActionDao.findAll(id);
		if(actions!=null){
			product.setActions(actions);
		}
		Organization org = organizationDao.find(product.getOrganizationId());
		product.setCompanyname(org.getName());
		String[] reqs = product.getRequires()==null?null:product.getRequires().split(";");
		product.setRequiresArray(reqs);
		String[] pros = product.getProfits()==null?null:product.getProfits().split(";");
		product.setProfitsArray(pros);
		return product;
	}

	@Override
	public void updateProduct(Integer productId, String title, int loanType, String profits, String requires) throws Exception{
		User user = userService.getCurrentUser();
		if(user==null || user.getPrivilege()!=User.PRIVILEGE_AGENT_AUDIT_SUCCESS){
			throw new Exception("没有相关权限");
		}
		AgentProduct ap = apDao.find(user.getId(), productId);
		if(ap==null){
			throw new Exception("无法管理相关融资产品");
		}
		productDao.update(productId, title, loanType, profits, requires);
	}
	
	@Override
	public Integer createProduct(String title, int loanType, String profits,
			String requires) throws Exception {
		
		User user = userService.getCurrentUser();
		if(user==null || user.getPrivilege()!=User.PRIVILEGE_AGENT_AUDIT_SUCCESS){
			throw new Exception("没有相关权限");
		}
		
		Product product = new Product();
		product.setCityCode(user.getCity());
		product.setOrganizationId(user.getOrganizationId());
		product.setLoanType(loanType);
		product.setProfits(profits);
		product.setRequires(requires);
		product.setState(Product.STATE_INIT);
		product.setTitle(title);
//		product.setUserId(user.getId());
		productDao.create(product);
		apDao.add(user.getId(), product.getId());
		stateLogService.create(product.getId(), Product.STATE_INIT, StateLog.TYPE_PRODUCT);
		return product.getId();
	}

	@Override
	public void addProductAction(int ind, Integer productId, String title,
			String description, int estimatedTime) throws Exception {
		User user = userService.getCurrentUser();
		if(user==null || user.getPrivilege()!=User.PRIVILEGE_AGENT_AUDIT_SUCCESS){
			throw new Exception("没有相关权限");
		}
		Product product = productDao.find(productId);
		if(product==null){
			throw new Exception("没有相关融资产品");
		}
		AgentProduct ap = apDao.find(user.getId(), product.getId());
		if(ap==null){
			throw new Exception("无法管理相关融资产品");
		}
		
		ProductAction paction = new ProductAction();
		paction.setInd(ind);
		paction.setDescription(description);
		paction.setEstimatedTime(estimatedTime);
		paction.setProductId(productId);
		paction.setTitle(title);
		productActionDao.create(paction);
		stateLogService.create(paction.getId(), 1, StateLog.TYPE_PRODUCTACTION);
	}

	@Override
	public void applyAuditProduct(Integer productId) throws Exception {
		User user = userService.getCurrentUser();
		if(user==null || user.getPrivilege()!=User.PRIVILEGE_AGENT_AUDIT_SUCCESS){
			throw new Exception("没有相关权限");
		}
		Product product = productDao.find(productId);
		if(product==null){
			throw new Exception("没有相关融资产品");
		}
		AgentProduct ap = apDao.find(user.getId(), product.getId());
		if(ap==null){
			throw new Exception("无法管理相关融资产品");
		}
		int sourceState = product.getState();
		if(sourceState!=Product.STATE_INIT && sourceState!=Product.STATE_AUDIT_REFUSE){
			throw new Exception("产品不可申请审核");
		}
		productDao.changeState(productId, Product.STATE_APPLY);
		stateLogService.change(productId, sourceState, Product.STATE_APPLY, StateLog.TYPE_PRODUCT);
	}

	@Override
	public void auditProduct(Integer productId, boolean pass) throws Exception {
		User user = userService.getCurrentUser();
		if(user==null || user.getPrivilege()!=User.PRIVILEGE_OPERATOR){
			throw new Exception("没有相关权限");
		}
		Product product = productDao.find(productId);
//		if(product==null || !product.getCityCode().equals(user.getCity())){
//			throw new Exception("无法审核相关融资产品");
//		}
		if(product.getState()!=Product.STATE_APPLY){
			throw new Exception("产品不可审核");
		}
		int sourceState = product.getState();
		int targetState = -1;
		if(pass==true){
			productDao.changeState(productId, Product.STATE_AUDIT_PASS);
			targetState = Product.STATE_AUDIT_PASS;
		}else{
			productDao.changeState(productId, Product.STATE_AUDIT_REFUSE);
			targetState = Product.STATE_AUDIT_REFUSE;
		}
		stateLogService.change(productId, sourceState, targetState, StateLog.TYPE_PRODUCT);
	}
	@Override
	public List<User> queryAllAgentsByProduct(Integer id) throws Exception{
		List<AgentProduct> aps = apDao.findAllByProduct(id);
		List<User> res = new ArrayList<User>();
		if(aps==null)
			return res;
		for(AgentProduct ap : aps){
			User user = userDao.find(ap.getUserId());
			res.add(user);
		}
		return res;
	}
	@Override
	public List<Product> queryAllMyProductByState(int state) throws Exception{
		List<Product> res = new ArrayList<Product>();
		User user = userService.getCurrentUser();
		if(user==null || (user.getPrivilege()!=User.PRIVILEGE_AGENT_AUDIT_SUCCESS && user.getPrivilege()!=User.PRIVILEGE_AGENT_APPLY && user.getPrivilege()!=User.PRIVILEGE_AGENT_AUDIT_REFUSE)){
			throw new Exception("没有相关权限");
		}else if(user.getPrivilege()==User.PRIVILEGE_AGENT_APPLY || user.getPrivilege()==User.PRIVILEGE_AGENT_AUDIT_REFUSE)
		{
			return res;
		}
		
		List<AgentProduct> aps = apDao.findAllByAgent(user.getId());
		
		if(aps==null)
			return res;
		
		Organization org = organizationDao.find(user.getOrganizationId());
		
		for(AgentProduct ap : aps){
			Product product = productDao.find(ap.getProductId());
			if(state==-1 || product.getState()==state)
			{
				product.setCompanyname(org.getName());
				res.add(product);
			}
		}
		return res;
	}
	@Override
	public List<Product> queryAllByOrgAndState(Integer organizationId, int state) throws Exception{
		List<Product> res = new ArrayList<Product>();
		User user = userService.getCurrentUser();
		if(user==null || (user.getPrivilege()!=User.PRIVILEGE_AGENT_AUDIT_SUCCESS && user.getPrivilege()!=User.PRIVILEGE_AGENT_APPLY && user.getPrivilege()!=User.PRIVILEGE_AGENT_AUDIT_REFUSE)){
			throw new Exception("没有相关权限");
		}
		Organization org = organizationDao.find(organizationId);
		if(org==null){
			throw new Exception("没有相关企业");
		}
		res = productDao.findAllByOrganizationIdAndState(0, 100, organizationId, state);
		if(res==null){
			return new ArrayList<Product>();
		}else{
			for(Product pro : res){
				pro.setCompanyname(org.getName());
			}
			return res;
		}
	}
	@Override
	public List<Product> queryAllByOrgAndCityAndState(Integer organizationId, String cityCode, int state) throws Exception{
		List<Product> res = new ArrayList<Product>();
		User user = userService.getCurrentUser();
		if(user==null || (user.getPrivilege()!=User.PRIVILEGE_AGENT_AUDIT_SUCCESS && user.getPrivilege()!=User.PRIVILEGE_AGENT_APPLY && user.getPrivilege()!=User.PRIVILEGE_AGENT_AUDIT_REFUSE)){
			throw new Exception("没有相关权限");
		}
		Organization org = organizationDao.find(organizationId);
		if(org==null){
			throw new Exception("没有相关企业");
		}
		res = productDao.findAllByOrganizationIdAndCityAndState(organizationId, cityCode, state);
		if(res==null){
			return new ArrayList<Product>();
		}else{
			for(Product pro : res){
				pro.setCompanyname(org.getName());
				AgentProduct ap = apDao.find(user.getId(), pro.getId());
				if(ap!=null){
					pro.setRelated(1);
				}
			}
			return res;
		}
	}
	@Override
	public List<Product> queryAllByState(int state){
		List<Product> pros = productDao.findAllByState(0, 100, state);
		if(pros==null){
			return new ArrayList<Product>();
		}else{
			for(Product pro : pros){
				Organization org = organizationDao.find(pro.getOrganizationId());
				pro.setCompanyname(org.getName());
			}
			return pros;
		}
	}
	@Override
	public void addRelationTo(Integer id) throws Exception{
		User user = userService.getCurrentUser();
		if(user==null || user.getPrivilege()!=User.PRIVILEGE_AGENT_AUDIT_SUCCESS){
			throw new Exception("没有相关权限");
		}
		Product product = productDao.find(id);
		
		if(product==null || product.getState()!=Product.STATE_AUDIT_PASS){
			throw new Exception("产品不可见");
		}
		
		if(product.getOrganizationId()!=user.getOrganizationId() || !product.getCityCode().equals(user.getCity())){
			throw new Exception("无权关联相关融资产品");
		}
		apDao.add(user.getId(), id);
	}
	@Override
	public void removeRelationTo(Integer id) throws Exception{
		User user = userService.getCurrentUser();
		if(user==null || user.getPrivilege()!=User.PRIVILEGE_AGENT_AUDIT_SUCCESS){
			throw new Exception("没有相关权限");
		}
		Product product = productDao.find(id);
		
		if(product==null || product.getState()!=Product.STATE_AUDIT_PASS){
			throw new Exception("产品不可见");
		}
		
		if(product.getOrganizationId()!=user.getOrganizationId() || !product.getCityCode().equals(user.getCity())){
			throw new Exception("无权去掉相关融资产品的关联");
		}
		apDao.remove(user.getId(), id);
	}

}
