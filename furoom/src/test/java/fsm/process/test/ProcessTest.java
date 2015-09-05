package fsm.process.test;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import fsm.dao.IAgentProductDao;
import fsm.dao.IOperationActionDao;
import fsm.dao.IOperationDao;
import fsm.dao.IOrganizationDao;
import fsm.dao.IProductActionDao;
import fsm.dao.IProductDao;
import fsm.dao.IUserDao;
import fsm.model.AgentProduct;
import fsm.model.Operation;
import fsm.model.OperationAction;
import fsm.model.Organization;
import fsm.model.Product;
import fsm.model.ProductAction;
import fsm.model.User;
import fsm.service.IUserService;

public class ProcessTest {
static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	protected static IUserService userService = context.getBean(IUserService.class);
	protected static IUserDao userDao = context.getBean(IUserDao.class);
	protected static IOrganizationDao organizationDao = context.getBean(IOrganizationDao.class);
	protected static IProductDao productDao = context.getBean(IProductDao.class);
	protected static IProductActionDao productActionDao = context.getBean(IProductActionDao.class);
	protected static IOperationDao operationDao = context.getBean(IOperationDao.class);
	protected static IOperationActionDao operationActionDao = context.getBean(IOperationActionDao.class);
	protected static IAgentProductDao apDao = context.getBean(IAgentProductDao.class);
	public static void main(String args[]) throws Exception{
		try{
		initDataBase();
		Integer auditorId1 = createOperator("wd", "13399999999");
		Integer auditorId2 = createOperator("lm", "13499999999");
		
		Integer orgId1 = createOrganization("北大", "北京大学", "100010", "10099900393");
		Integer orgId2 = createOrganization("清华", "清华大学", "100011", "10099900394");
		
		
		List<User> agents = new ArrayList<User>();
		
		Integer agentId1 = registerAgent("zs", "13511111111", orgId1, "001", "001001");
		agents.add(userDao.find(agentId1));
		Integer agentId2 = registerAgent("zs2", "13511111112", orgId1, "001", "001002");
		agents.add(userDao.find(agentId2));
		Integer agentId3 = registerAgent("zs3", "13511111113", orgId1, "001", "001003");
		agents.add(userDao.find(agentId3));
		Integer agentId4 = registerAgent("zs4", "13511111114", orgId1, "001", "001004");
		agents.add(userDao.find(agentId4));
		Integer agentId5 = registerAgent("zs5", "13511111115", orgId1, "002", "002001");
		agents.add(userDao.find(agentId5));
		Integer agentId6 = registerAgent("zs6", "13511111116", orgId1, "002", "002002");
		agents.add(userDao.find(agentId6));
		Integer agentId7 = registerAgent("zs7", "13511111117", orgId1, "002", "002003");
		agents.add(userDao.find(agentId7));
		
		Integer agentId8 = registerAgent("zs8", "13511111118", orgId2, "001", "001001");
		agents.add(userDao.find(agentId8));
		Integer agentId9 = registerAgent("zs9", "13511111119", orgId2, "001", "001002");
		agents.add(userDao.find(agentId9));
		Integer agentId10 = registerAgent("zs10", "13511111120", orgId2, "001", "001003");
		agents.add(userDao.find(agentId10));
		Integer agentId11 = registerAgent("zs11", "13511111121", orgId2, "001", "001004");
		agents.add(userDao.find(agentId11));
		Integer agentId12 = registerAgent("zs12", "13511111122", orgId2, "002", "002001");
		agents.add(userDao.find(agentId12));
		Integer agentId13 = registerAgent("zs13", "13511111123", orgId2, "002", "002002");
		agents.add(userDao.find(agentId13));
		Integer agentId14 = registerAgent("zs14", "13511111124", orgId2, "002", "002003");
		agents.add(userDao.find(agentId14));
		
		
		auditAgent(agentId1, true);
		auditAgent(agentId2, false);
		auditAgent(agentId3, true);
		auditAgent(agentId4, false);
		auditAgent(agentId5, true);
		auditAgent(agentId6, true);
		auditAgent(agentId7, true);
		auditAgent(agentId8, true);
		auditAgent(agentId9, true);
		auditAgent(agentId10, true);
		auditAgent(agentId11, true);
		auditAgent(agentId12, true);
		auditAgent(agentId13, true);
		auditAgent(agentId14, true);
		
		reApplyAgent(agentId2);
		reApplyAgent(agentId4);
		
		auditAgent(agentId2, true);
		auditAgent(agentId4, true);
		
		
		Integer proId1 = createProduct("工薪贷", agentId1, "001001", orgId1, Product.TYPE_XINYONG, "好处多多一;好处多多二;好处多多三", "要求也有一;要求也有二;要求也有三");
		addProductAction(1,proId1, "步骤一", "首先需要干啥干啥", 5);
		addProductAction(2,proId1, "步骤二", "其次需要干啥干啥", 7);
		addProductAction(3,proId1, "步骤三", "最后需要干啥干啥", 3);
		applyAuditProduct(proId1);
		auditProduct(proId1, true);
		
		
		Integer proId2 = createProduct("精英贷", agentId2, "002001", orgId1, Product.TYPE_XIAOFEI, "精英贷好处多多一;精英贷好处多多二;精英贷好处多多三", "精英贷要求也有一;精英贷要求也有二;精英贷要求也有三");
		addProductAction(1,proId2, "步骤一", "精英贷首先需要干啥干啥", 6);
		addProductAction(2,proId2, "步骤二", "精英贷其次需要干啥干啥", 4);
		addProductAction(3,proId2, "步骤三", "精英贷最后需要干啥干啥", 1);
		applyAuditProduct(proId2);
		auditProduct(proId2, false);
		applyAuditProduct(proId2);
		auditProduct(proId2, true);
		
		
		String[] steps = {"步骤一需要干什么","步骤二需要干什么","步骤三需要干什么","步骤四需要干什么","步骤五需要干什么","步骤六需要干什么"};
		String[] profits = {"无需抵押","上班族可申请","放款快","办理便捷","对收入要求低"};
		String[] requires = {"对信用情况有要求","要求每月缴纳社保","要求指定公司类型", "打卡工资大于2000","要求有本地公积金","对年龄有要求"};
		
		for(User agent : agents){
			Random ran = new Random();
			
			String profitstr = profits[ran.nextInt(100)%5]+";"+profits[ran.nextInt(100)%5]+";"+profits[ran.nextInt(100)%5];
			String requirestr = requires[ran.nextInt(100)%6]+";"+requires[ran.nextInt(100)%6]+";"+requires[ran.nextInt(100)%6];
			
			Integer proId_fangdai = createProduct("房贷", agent.getId(), agent.getCity(), agent.getOrganizationId(), Product.TYPE_HOUSE, profitstr, requirestr);
			
			int step = ran.nextInt(6);
			for(int i=0; i<step; i++){
				addProductAction((i+1), proId_fangdai, "步骤"+i, steps[i], ran.nextInt(10));
			}
			applyAuditProduct(proId_fangdai);
			auditProduct(proId_fangdai, true);
			
			profitstr = profits[ran.nextInt(100)%5]+";"+profits[ran.nextInt(100)%5]+";"+profits[ran.nextInt(100)%5];
			requirestr = requires[ran.nextInt(100)%6]+";"+requires[ran.nextInt(100)%6]+";"+requires[ran.nextInt(100)%6];
			Integer proId_jingyingdai = createProduct("经营贷", agent.getId(), agent.getCity(), agent.getOrganizationId(), Product.TYPE_JINGYING, profitstr, requirestr);
			step = ran.nextInt(6);
			for(int i=0; i<step; i++){
				addProductAction((i+1), proId_jingyingdai, "步骤"+i, steps[i], ran.nextInt(10));
			}
			applyAuditProduct(proId_jingyingdai);
			auditProduct(proId_jingyingdai, true);
			
			
			profitstr = profits[ran.nextInt(100)%5]+";"+profits[ran.nextInt(100)%5]+";"+profits[ran.nextInt(100)%5];
			requirestr = requires[ran.nextInt(100)%6]+";"+requires[ran.nextInt(100)%6]+";"+requires[ran.nextInt(100)%6];
			Integer proId_chedai = createProduct("车贷", agent.getId(), agent.getCity(), agent.getOrganizationId(), Product.TYPE_CAR, profitstr, requirestr);
			step = ran.nextInt(6);
			for(int i=0; i<step; i++){
				addProductAction((i+1), proId_chedai, "步骤"+i, steps[i], ran.nextInt(10));
			}
			applyAuditProduct(proId_chedai);
			auditProduct(proId_chedai, true);
			
			
			profitstr = profits[ran.nextInt(100)%5]+";"+profits[ran.nextInt(100)%5]+";"+profits[ran.nextInt(100)%5];
			requirestr = requires[ran.nextInt(100)%6]+";"+requires[ran.nextInt(100)%6]+";"+requires[ran.nextInt(100)%6];
			Integer proId_diyadai = createProduct("抵押贷", agent.getId(), agent.getCity(), agent.getOrganizationId(), Product.TYPE_DIYA, profitstr, requirestr);
			step = ran.nextInt(6);
			for(int i=0; i<step; i++){
				addProductAction((i+1), proId_diyadai, "步骤"+i, steps[i], ran.nextInt(10));
			}
			applyAuditProduct(proId_diyadai);
			auditProduct(proId_diyadai, true);
			
			
			profitstr = profits[ran.nextInt(100)%5]+";"+profits[ran.nextInt(100)%5]+";"+profits[ran.nextInt(100)%5];
			requirestr = requires[ran.nextInt(100)%6]+";"+requires[ran.nextInt(100)%6]+";"+requires[ran.nextInt(100)%6];
			Integer proId_xiaofeidai = createProduct("消费贷", agent.getId(), agent.getCity(), agent.getOrganizationId(), Product.TYPE_XIAOFEI, profitstr, requirestr);
			step = ran.nextInt(6);
			for(int i=0; i<step; i++){
				addProductAction((i+1), proId_xiaofeidai, "步骤"+i, steps[i], ran.nextInt(10));
			}
			applyAuditProduct(proId_xiaofeidai);
			auditProduct(proId_xiaofeidai, true);
			
			
			profitstr = profits[ran.nextInt(100)%5]+";"+profits[ran.nextInt(100)%5]+";"+profits[ran.nextInt(100)%5];
			requirestr = requires[ran.nextInt(100)%6]+";"+requires[ran.nextInt(100)%6]+";"+requires[ran.nextInt(100)%6];
			Integer proId_xinyongdai = createProduct("信用贷", agent.getId(), agent.getCity(), agent.getOrganizationId(), Product.TYPE_XINYONG, profitstr, requirestr);
			step = ran.nextInt(6);
			for(int i=0; i<step; i++){
				addProductAction((i+1), proId_xinyongdai, "步骤"+i, steps[i], ran.nextInt(10));
			}
			applyAuditProduct(proId_xinyongdai);
			auditProduct(proId_xinyongdai, true);
		}
		
		
		Integer userId1 = userRegister("wangxiaopang", "13899977889");
		Integer userId2 = userRegister("wangxiaoshuai", "13899977890");
		
		
		Integer operationId1 = applyOperation(userId1,Product.TYPE_XINYONG,20,24,"001","001001");
		updateOperation(operationId1, proId1, agentId1);
		
		Integer operationId2 = applyOperation(userId2,Product.TYPE_XIAOFEI,15,120,"002","002001");
		updateOperation(operationId2, proId2, agentId2);
		
		auditOperation(operationId1, true);
		auditOperation(operationId2, false);
		reApplyOperation(operationId2);
		auditOperation(operationId2, true);
		
		takeoverOperation(operationId1, agentId1);
		takeoverOperation(operationId2, agentId2);
		handleOperation(operationId1);
		handleOperation(operationId2);
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			System.exit(0);
		}
		
	}
	
	/**
	 * 清库重新建表
	 * 
	 * */
	public static void initDataBase() throws Exception{
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		String url = "jdbc:mysql://localhost:3306/fsm?useUnicode=true&amp;characterEncoding=UTF-8";
		String username = "gpps";
		String password = "111111";
	    Connection conn = (Connection) DriverManager.getConnection(url, username, password);
	    ScriptRunner runner = new ScriptRunner(conn);
	    runner.runScript(new InputStreamReader(ProcessTest.class.getResourceAsStream("init.sql")));
	    conn.close();
	}
	
	public static Integer createOperator(String name, String tel) throws Exception{
		User usr = new User();
		usr.setCreatetime(System.currentTimeMillis());
		usr.setLoginId(tel);
		usr.setName(name);
		usr.setPassword("111111");
		usr.setPrivilege(User.PRIVILEGE_OPERATOR);
		usr.setSex("man");
		usr.setTel(tel);
		User user = userService.createInner(usr);
		return user.getId();
	}
	
	public static Integer createOrganization(String name, String fullname, String code, String license){
		Organization org = new Organization();
		org.setCode(code);
		org.setFullName(fullname);
		org.setLicense(license);
		org.setName(name);
		organizationDao.create(org);
		return org.getId();
	}
	
	
	public static Integer registerAgent(String name, String tel, Integer orgId, String province, String city) throws Exception{
		User usr = new User();
		usr.setCreatetime(System.currentTimeMillis());
		usr.setLoginId(tel);
		usr.setName(name);
		usr.setPassword("111111");
		usr.setPrivilege(User.PRIVILEGE_AGENT_APPLY);
		usr.setSex("man");
		usr.setTel(tel);
		usr.setOrganizationId(orgId);
		usr.setProvince(province);
		usr.setCity(city);
		User user = userService.registerInner(usr);
		return user.getId();
	}
	
	public static void auditAgent(Integer userId, boolean pass) throws Exception{
		
		User user = userDao.find(userId);
		if(user.getPrivilege()!=User.PRIVILEGE_AGENT_APPLY){
			throw new Exception("不可审核");
		}
		if(pass==true)
		{
			userDao.changePrivilege(userId, User.PRIVILEGE_AGENT_AUDIT_SUCCESS);
		}
		else{
			userDao.changePrivilege(userId, User.PRIVILEGE_AGENT_AUDIT_REFUSE);
		}
	}
	
	public static void reApplyAgent(Integer userId) throws Exception{
		User user = userDao.find(userId);
		if(user.getPrivilege()!=User.PRIVILEGE_AGENT_AUDIT_REFUSE){
			throw new Exception("不可重新申请");
		}
		userDao.changePrivilege(userId, User.PRIVILEGE_AGENT_APPLY);
	}
	
	public static Integer createProduct(String title, Integer agentId, String cityCode, Integer organizationId,int loanType, String profits, String requires){
		Product product = new Product();
		product.setCityCode(cityCode);
		product.setOrganizationId(organizationId);
		product.setLoanType(loanType);
		product.setProfits(profits);
		product.setRequires(requires);
		product.setState(Product.STATE_INIT);
		product.setTitle(title);
//		product.setUserId(agentId);
		productDao.create(product);
		
		apDao.add(agentId, product.getId());
		
		return product.getId();
	}
	
	public static void addProductAction(int index, Integer productId, String title, String description, int estimatedTime){
		ProductAction paction = new ProductAction();
		paction.setInd(index);
		paction.setDescription(description);
		paction.setEstimatedTime(estimatedTime);
		paction.setProductId(productId);
		paction.setTitle(title);
		productActionDao.create(paction);
	}
	
	public static void applyAuditProduct(Integer productId) throws Exception{
		Product product = productDao.find(productId);
		if(product.getState()!=Product.STATE_INIT && product.getState()!=Product.STATE_AUDIT_REFUSE){
			throw new Exception("产品不可申请审核");
		}
		productDao.changeState(productId, Product.STATE_APPLY);
	}
	
	public static void auditProduct(Integer productId, boolean pass) throws Exception{
		Product product = productDao.find(productId);
		if(product.getState()!=Product.STATE_APPLY){
			throw new Exception("产品不可审核");
		}
		if(pass==true){
			productDao.changeState(productId, Product.STATE_AUDIT_PASS);
		}else{
			productDao.changeState(productId, Product.STATE_AUDIT_REFUSE);
		}
	}
	public static Integer userRegister(String name, String tel) throws Exception{
		User usr = new User();
		usr.setCreatetime(System.currentTimeMillis());
		usr.setLoginId(tel);
		usr.setName(name);
		usr.setPassword("111111");
		usr.setPrivilege(User.PRIVILEGE_USER);
		usr.setSex("man");
		usr.setTel(tel);
		User user = userService.registerInner(usr);
		return user.getId();
	}
	
	public static Integer applyOperation(Integer userId, int loanType, int amount, int period, String province, String city){
		Operation operation = new Operation();
		operation.setAmount(amount);
		operation.setCity(city);
		operation.setCreatetime(System.currentTimeMillis());
		operation.setLoanType(loanType);
		operation.setPeriod(period);
		operation.setProvince(province);
		operation.setState(Operation.STATE_APPLY);
		operation.setUserId(userId);
		operationDao.create(operation);
		return operation.getId();
	}
	
	public static void updateOperation(Integer operationId, Integer productId, Integer agentId) throws Exception{
		Product product = productDao.find(productId);
		User agent = userDao.find(agentId);
		AgentProduct ap = apDao.find(agentId, productId);
		if(ap==null){
			throw new Exception("信贷经理和贷款产品没关系！");
		}
		List<ProductAction> actions = productActionDao.findAll(productId);
		for(ProductAction action : actions){
			OperationAction ac = new OperationAction();
			ac.setCreateTime(System.currentTimeMillis());
			ac.setDescription(action.getDescription());
			ac.setOperationId(operationId);
			ac.setState(OperationAction.STATE_INIT);
			ac.setTitle(action.getTitle());
			operationActionDao.create(ac);
		}
		
		operationDao.update(operationId, productId, agentId, -1, -1, -1, null, null, System.currentTimeMillis());
	}
	
	
	public static void auditOperation(Integer operationId, boolean pass) throws Exception{
		Operation operation = operationDao.find(operationId);
		if(operation.getState()!=Operation.STATE_APPLY){
			throw new Exception("状态不对，无法审核Operation");
		}
		if(pass==true){
			operationDao.changeState(operationId, Operation.STATE_AUDIT_PASS, System.currentTimeMillis());
		}else{
			operationDao.changeState(operationId, Operation.STATE_AUDIT_REFUSE, System.currentTimeMillis());
		}
	}
	
	public static void reApplyOperation(Integer operationId) throws Exception{
		Operation operation = operationDao.find(operationId);
		if(operation.getState()!=Operation.STATE_AUDIT_REFUSE){
			throw new Exception("状态不对，无法重新申请审核Operation");
		}
		operationDao.changeState(operationId, Operation.STATE_APPLY, System.currentTimeMillis());
	}
	
	public static void takeoverOperation(Integer operationId, Integer agentId) throws Exception{
		Operation operation = operationDao.find(operationId);
		if(operation.getState()!=Operation.STATE_AUDIT_PASS){
			throw new Exception("状态不对，无法接管Operation");
		}
		operationDao.changeState(operationId, Operation.STATE_PROCESSING, System.currentTimeMillis());
	}
	
	public static void handleOperation(Integer operationId) throws Exception{
		Operation operation = operationDao.find(operationId);
		if(operation.getState()!=Operation.STATE_PROCESSING){
			throw new Exception("状态不对，无法操作Operation");
		}
		List<OperationAction> actions = operationActionDao.findAll(operationId);
		if(actions!=null){
			for(OperationAction action : actions){
				operationActionDao.changeState(action.getId(), OperationAction.STATE_DONE, "执行完毕");
			}
		}
		operationDao.changeState(operationId, Operation.STATE_DONE, System.currentTimeMillis());
	}
}
