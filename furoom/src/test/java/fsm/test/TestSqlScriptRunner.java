package fsm.test;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import fsm.dao.IUserDao;
import fsm.model.User;
import fsm.service.IUserService;

public class TestSqlScriptRunner {
static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	public static void main(String args[]) throws Exception{
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		String url = "jdbc:mysql://localhost:3306/gpps?useUnicode=true&amp;characterEncoding=UTF-8";
		String username = "gpps";
		String password = "111111";
	    Connection conn = (Connection) DriverManager.getConnection(url, username, password);
	    ScriptRunner runner = new ScriptRunner(conn);
	    runner.runScript(new InputStreamReader(TestSqlScriptRunner.class.getResourceAsStream("init.sql")));
	    conn.close();
		
		System.exit(0);
	}
}
