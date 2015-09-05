package fsm.test;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import fsm.model.Product;
import fsm.service.IProductService;

public class TestService {
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	public static void main(String args[]) throws Exception{
		
		testProductService();
		System.exit(0);
	}
	public static void testProductService() throws Exception{
		IProductService productService = context.getBean(IProductService.class);
		Map<String, Object> res = productService.queryAllByTypeAndCityAndStateAndOrder(2, "002001", 2, 1, 0, 10);
		System.out.println(res.get("total"));
		List<Product> pros = (List<Product>)res.get("result");
		for(Product pro : pros){
			System.out.println(pro.getTitle());
		}
	}
}
