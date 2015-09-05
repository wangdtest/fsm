package fsm.test;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import fsm.dao.IAgentProductDao;
import fsm.dao.IOperationActionDao;
import fsm.dao.IOrganizationDao;
import fsm.dao.IProductActionDao;
import fsm.dao.IProductDao;
import fsm.model.AgentProduct;
import fsm.model.Organization;
import fsm.model.Product;
import fsm.model.ProductAction;

public class TestDao {
static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	public static void main(String args[]) throws Exception{
		
		testAgentProductDao();
		System.exit(0);
	}
	public static void testProductAction(){
		IProductActionDao actionDao = context.getBean(IProductActionDao.class);
//		ProductAction pa = new ProductAction();
//		pa.setDescription("这是一个牛逼的Action啊");
//		pa.setEstimatedTime(10);
//		pa.setProductId(1);
//		pa.setTitle("示例Action");
//		actionDao.create(pa);
		
		ProductAction pa = actionDao.find(1);
		System.out.println(pa.getTitle());
		System.out.println(pa.getDescription());
		
		actionDao.remove(pa.getId());
	}
	public static void testOrganization(){
		IOrganizationDao organizationDao = context.getBean(IOrganizationDao.class);
//		Organization org = new Organization();
//		org.setCode("100010");
//		org.setFullName("中国农业银行");
//		org.setLicense("100090");
//		org.setName("农业银行");
//		organizationDao.create(org);
		
		Organization org = organizationDao.find(1);
		System.out.println(org.getName());
	}
	
	public static void testProduct(){
		IProductDao productDao = context.getBean(IProductDao.class);
//		Product product = new Product();
//		product.setTitle("灰常牛逼的产品");
//		product.setCityCode("001001");
//		product.setOrganizationId(1);
//		product.setProfits("好处一;好处二;好处三");
//		product.setRequires("要求一;要求二;要求三");
//		product.setState(Product.STATE_AUDIT_PASS);
//		product.setUserId(1);
//		productDao.create(product);
		List<Product> pros = productDao.findAllByCityAndState(0, 100, "001001", Product.STATE_AUDIT_PASS);
		for(Product pro : pros){
			System.out.println(pro.getTitle());
		}
		
		pros = productDao.findAllByOrganizationIdAndState(0, 100, 1, Product.STATE_AUDIT_PASS);
		for(Product pro : pros){
			System.out.println(pro.getTitle());
		}
	}
	
	public static void testOperationAction(){
		IOperationActionDao actionDao = context.getBean(IOperationActionDao.class);
		actionDao.changeState(1, 2, "123");
	}
	
	public static void testAgentProductDao(){
		IAgentProductDao apDao = context.getBean(IAgentProductDao.class);
//		apDao.add(1, 1);
//		apDao.add(1, 2);
//		apDao.add(1, 3);
//		apDao.add(1, 5);
//		apDao.add(1, 7);
//		apDao.add(2, 1);
//		apDao.add(2, 3);
//		apDao.add(2, 7);
//		apDao.add(2, 5);
//		apDao.add(3, 1);
//		apDao.add(3, 2);
//		apDao.add(3, 5);
//		apDao.remove(1, 2);
//		apDao.remove(2, 3);
//		apDao.remove(3, 5);
//		apDao.remove(7, 5);
		List<AgentProduct> aps = apDao.findAllByAgent(1);
		for(AgentProduct ap : aps){
			System.out.println(ap.getProductId());
		}
		System.out.println("-----------------------------------");
		aps = apDao.findAllByProduct(5);
		for(AgentProduct ap : aps){
			System.out.println(ap.getUserId());
		}
	}
}
