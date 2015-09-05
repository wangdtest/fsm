package fsm.service.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.stereotype.Service;

import fsm.support.service.ISqlScriptExecuteService;
import fsm.test.TestSqlScriptRunner;
@Service
public class SqlScriptExecuteServiceImpl implements ISqlScriptExecuteService {

	@Override
	public void executeSqlScript() throws Exception{
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		String url = "jdbc:mysql://localhost:3306/gpps?useUnicode=true&amp;characterEncoding=UTF-8";
		String username = "gpps";
		String password = "111111";
	    Connection conn = (Connection) DriverManager.getConnection(url, username, password);
	    ScriptRunner runner = new ScriptRunner(conn);
	    runner.runScript(new InputStreamReader(SqlScriptExecuteServiceImpl.class.getResourceAsStream("init.sql")));
	    conn.close();
	}

}
