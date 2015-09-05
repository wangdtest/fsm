package fsm.service.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

import fsm.service.exception.ShellExecuteException;
import fsm.support.service.IShellExecuteService;
import fsm.tools.JavaShellUtil;
@Service
public class ShellExecuteServiceImpl implements IShellExecuteService {

	@Override
	public void executeMysqlDump() throws ShellExecuteException{
		//格式化日期时间，记录日志时使用  
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmSS");  
		StringBuilder sb = new StringBuilder();
		sb.append("mysqldump -uroot -pcalis111 gpps > /root/install/mysqldump/gppsnormal_");
		sb.append(dateFormat.format(new Date())).append(".dump");
		try{
			JavaShellUtil.executeShell(sb.toString());
		}catch(Exception e){
			throw new ShellExecuteException(e.getMessage());
		}
	}

}
