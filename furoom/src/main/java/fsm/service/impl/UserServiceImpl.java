package fsm.service.impl;

import static com.furoom.utils.ObjectUtil.checkNullObject;
import static com.furoom.utils.StringUtil.checkNullAndTrim;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.furoom.utils.GraphValidateCode;
import com.furoom.utils.StringUtil;

import fsm.dao.IStateLogDao;
import fsm.dao.IUserAccountDao;
import fsm.dao.IUserDao;
import fsm.model.StateLog;
import fsm.model.User;
import fsm.model.UserAccount;
import fsm.service.IUserService;
import fsm.service.exception.FrozenException;
import fsm.service.exception.InviteException;
import fsm.service.exception.SMSException;
import fsm.service.exception.ValidateCodeException;
import fsm.support.service.IMessageSupportService;
import fsm.support.service.IStateLogService;
@Service
public class UserServiceImpl implements IUserService {
	@Autowired
	IUserDao userDao;
	@Autowired
	IUserAccountDao userAccountDao;
	@Autowired
	IMessageSupportService messageService;
	@Autowired
	IStateLogService stateLogService;
	protected Logger logger=Logger.getLogger(UserServiceImpl.class);
	@Override
	public void login(String loginId, String password,
			String graphValidateCode) throws LoginException,
			ValidateCodeException {
		checkGraphValidateCode(graphValidateCode);
		HttpSession session =getCurrentSession();
		loginId=checkNullAndTrim("loginId", loginId);
		password=getProcessedPassword(checkNullAndTrim("password", password)+PASSWORDSEED);
		User user=userDao.findByLoginIdAndPassword(loginId, password);
		if(user==null)
			throw new LoginException("用户名或密码错误!!");
		
		session.setAttribute(SESSION_ATTRIBUTENAME_USER, user);
	}
	
	@Override
	public void loginAgent(String loginId,String password,String graphValidateCode) throws LoginException,ValidateCodeException{
		checkGraphValidateCode(graphValidateCode);
		HttpSession session =getCurrentSession();
		loginId=checkNullAndTrim("loginId", loginId);
		password=getProcessedPassword(checkNullAndTrim("password", password)+PASSWORDSEED);
		User user=userDao.findByLoginIdAndPassword(loginId, password);
		if(user==null)
			throw new LoginException("用户名或密码错误!!");
		if(user.getPrivilege()!=User.PRIVILEGE_AGENT_APPLY && user.getPrivilege()!=User.PRIVILEGE_AGENT_AUDIT_REFUSE && user.getPrivilege()!=User.PRIVILEGE_AGENT_AUDIT_SUCCESS){
			throw new LoginException("没有信贷经理权限！！");
		}
		
		session.setAttribute(SESSION_ATTRIBUTENAME_USER, user);
	}
	
	@Override
	public void loginOperator(String loginId,String password,String graphValidateCode) throws LoginException,ValidateCodeException{
		checkGraphValidateCode(graphValidateCode);
		HttpSession session =getCurrentSession();
		loginId=checkNullAndTrim("loginId", loginId);
		password=getProcessedPassword(checkNullAndTrim("password", password)+PASSWORDSEED);
		User user=userDao.findByLoginIdAndPassword(loginId, password);
		if(user==null)
			throw new LoginException("用户名或密码错误!!");
		if(user.getPrivilege()!=User.PRIVILEGE_OPERATOR){
			throw new LoginException("没有业务员权限！！");
		}
		
		session.setAttribute(SESSION_ATTRIBUTENAME_USER, user);
	}
	
	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public User createInner(User user) throws LoginException{
		try{
		user.setLoginId(checkNullAndTrim("loginId", user.getLoginId()));
		user.setPassword(getProcessedPassword(user.getPassword()+PASSWORDSEED));
		user.setCreatetime(System.currentTimeMillis());
		user.setTel(checkNullAndTrim("tel", user.getTel()));
		if(isLoginIdExist(user.getLoginId()))
			throw new LoginException("LoginId is existed");
		if(isPhoneNumberExist(user.getTel()))
			throw new LoginException("Tel is existed");
		UserAccount account=new UserAccount();
		userAccountDao.create(account);
		user.setAccountId(account.getId());
		userDao.create(user);
		stateLogService.create(user.getId(), user.getPrivilege(), StateLog.TYPE_USER);
		}
		catch(IllegalArgumentException e){
			throw e;
		}catch(LoginException e){
			throw e;
		}catch(Exception e){
			throw new LoginException(e.getMessage());
		}
		
		return user;
	}
	
	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public User registerInner(User user) throws LoginException{
		
		if(user.getPrivilege()!=User.PRIVILEGE_AGENT_APPLY && user.getPrivilege()!=User.PRIVILEGE_USER){
			throw new LoginException("非法的操作");
		}
		
		try{
		user.setLoginId(checkNullAndTrim("loginId", user.getLoginId()));
		user.setPassword(getProcessedPassword(user.getPassword()+PASSWORDSEED));
		user.setCreatetime(System.currentTimeMillis());
		user.setTel(checkNullAndTrim("tel", user.getTel()));
		if(isLoginIdExist(user.getLoginId()))
			throw new LoginException("LoginId is existed");
		if(isPhoneNumberExist(user.getTel()))
			throw new LoginException("Tel is existed");
		UserAccount account=new UserAccount();
		userAccountDao.create(account);
		user.setAccountId(account.getId());
		userDao.create(user);
		user.setPassword(null);
		getCurrentSession().setAttribute(SESSION_ATTRIBUTENAME_USER, user);
		stateLogService.create(user.getId(), user.getPrivilege(), StateLog.TYPE_USER);
		}
		catch(IllegalArgumentException e){
			throw e;
		}catch(LoginException e){
			throw e;
		}catch(Exception e){
			throw new LoginException(e.getMessage());
		}
		
		return user;
	}
	
	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public User register(User user,String messageValidateCode, String graphValidateCode) throws ValidateCodeException,IllegalArgumentException, LoginException{
		
		if(user.getPrivilege()!=User.PRIVILEGE_AGENT_APPLY && user.getPrivilege()!=User.PRIVILEGE_USER){
			throw new LoginException("非法的操作");
		}
		
		checkGraphValidateCode(graphValidateCode);
		try{
			checkMessageValidateCode(messageValidateCode);	
			
			user.setLoginId(checkNullAndTrim("loginId", user.getLoginId()));
			user.setPassword(getProcessedPassword(checkNullAndTrim("password", user.getPassword())+PASSWORDSEED));
			user.setCreatetime(System.currentTimeMillis());
			user.setTel(checkNullAndTrim("tel", user.getTel()));
			if(isLoginIdExist(user.getLoginId()))
				throw new LoginException("LoginId is existed");
			if(isPhoneNumberExist(user.getTel()))
				throw new LoginException("Tel is existed");
			UserAccount account=new UserAccount();
			userAccountDao.create(account);
			user.setAccountId(account.getId());
			userDao.create(user);
			user.setPassword(null);
			getCurrentSession().setAttribute(SESSION_ATTRIBUTENAME_USER, user);
			stateLogService.create(user.getId(), user.getPrivilege(), StateLog.TYPE_USER);
			
			}
			catch(ValidateCodeException e){
				throw e;
			}
			catch(IllegalArgumentException e){
				throw e;
			}catch(LoginException e){
				throw e;
			}catch(Exception e){
				throw new LoginException(e.getMessage());
			}
			
			return user;
	}

	@Override
	public void changePassword(String loginId, String password,
			String messageValidateCode) throws ValidateCodeException,
			LoginException {
		checkMessageValidateCode(messageValidateCode);
		User user=userDao.findByLoginId(loginId);
		
		if(user==null)
			throw new LoginException("用户不存在");
		userDao.changePassword(user.getId(), getProcessedPassword(checkNullAndTrim("password", password)+PASSWORDSEED));

	}

	@Override
	public void loginOut() {
		HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		session.removeAttribute(SESSION_ATTRIBUTENAME_USER);
	}

	@Override
	public void sendMessageValidateCode(String phone) throws FrozenException {
		HttpSession session=getCurrentSession();
		if(session.getAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODESENDTIME)!=null)
		{
			if((Long)(session.getAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODESENDTIME))+MESSAGEVALIDATECODEINTERVAL>System.currentTimeMillis())
				throw new FrozenException("验证码二十分钟内有效，五分钟内不可重复发送");
		}
			
		String validateCode=getRandomValidateCode(5);
		System.out.println("messageValidateCode="+validateCode);
		session.setAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODE, validateCode);
		session.setAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODESENDTIME, System.currentTimeMillis());
		
		List<String> tels = new ArrayList<String>();
		tels.add(phone);
		try{
			messageService.sendSMS(tels, "您的验证码是："+validateCode);
		}catch(SMSException e){
			logger.error(e.getMessage());
		}
	}

	@Override
	public void writeGraphValidateCode(OutputStream os) throws IOException {
		GraphValidateCode validateCode=new GraphValidateCode(160, 40, 4, 40);
		getCurrentSession().setAttribute(SESSION_ATTRIBUTENAME_GRAPHVALIDATECODE, validateCode.getCode());
		validateCode.write(os);
	}

	@Override
	public boolean isLoginIdExist(String loginId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPhoneNumberExist(String phoneNumber) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isIdentityCardExist(String identityCard) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmailExist(String email) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getProcessedTel(String loginId) {
		try{
			Random ran = new Random();
			Thread.sleep(ran.nextInt(1000));
		}catch(Exception e){
			
		}
		return "123123123132132";
	}

	@Override
	public User getCurrentUser(){
		HttpSession session = getCurrentSession();
		Object user = session.getAttribute(SESSION_ATTRIBUTENAME_USER);
		if(user==null){
			return null;
		}else
		{
			return (User)user;
		}
	}
	
	@Override
	public HttpSession getCurrentSession() {
		 HttpSession session=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		 return session;
	}
	
	@Override
	public List<User> findAllByPrivilege(int privilege){
		List<User> user = userDao.findAllByPrivilege(privilege);
		if(user==null){
			return new ArrayList<User>();
		}else{
			return user;
		}
	}
	
	@Override
	public void auditAgent(Integer userId, boolean ispass) throws Exception{
		User agent = userDao.find(userId);
		if(agent==null || agent.getPrivilege()!=User.PRIVILEGE_AGENT_APPLY){
			throw new Exception("无法审核信贷经理");
		}
		if(ispass==true)
			userDao.changePrivilege(userId, User.PRIVILEGE_AGENT_AUDIT_SUCCESS);
		else
			userDao.changePrivilege(userId, User.PRIVILEGE_AGENT_AUDIT_REFUSE);
	}
	
	protected void checkMessageValidateCode(String messageValidateCode) throws ValidateCodeException
	{
		HttpSession session =getCurrentSession();
		messageValidateCode=checkNullAndTrim("messageValidateCode", messageValidateCode);
		String originalMessageValidateCode=String.valueOf(checkNullObject("originalMessageValidateCode", session.getAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODE)));
		
		long messageValidateCodeSendTime=(Long)session.getAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODESENDTIME);
		
		
		
		if(messageValidateCodeSendTime+MESSAGEVALIDATECODEEXPIRETIME<System.currentTimeMillis()){
			//如果验证码过期，删除掉内存中的验证码并报过期的异常
			session.removeAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODE);
			session.removeAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODESENDTIME);
			throw new ValidateCodeException("验证码过期");
		}else if(!originalMessageValidateCode.equals(messageValidateCode)){
			//如果用户只是输错了验证码，不删除掉内存中的验证码，只是报验证码不正确异常
			throw new ValidateCodeException("短信验证码错误,请输入正确验证码，短信验证码20分钟内有效");
		}else{
			//如果用户输入正确，则删除掉内存中的验证码，不报异常
			session.removeAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODE);
			session.removeAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODESENDTIME);
		}
	}
	
	
	protected boolean onlyCheckGraphValidateCode(String graphValidateCode) throws ValidateCodeException{
		HttpSession session =getCurrentSession();
		graphValidateCode=checkNullAndTrim("graphValidateCode", graphValidateCode);
		String originalGraphValidateCode=String.valueOf(checkNullObject("originalGraphValidateCode", session.getAttribute(SESSION_ATTRIBUTENAME_GRAPHVALIDATECODE)));
		if(!originalGraphValidateCode.toLowerCase().equals(graphValidateCode.toLowerCase()))
			throw new ValidateCodeException("图片验证码不正确");
		
		return true;
	}
	
	protected void checkGraphValidateCode(String graphValidateCode) throws ValidateCodeException
	{
		HttpSession session =getCurrentSession();
		graphValidateCode=checkNullAndTrim("graphValidateCode", graphValidateCode);
		String originalGraphValidateCode=String.valueOf(checkNullObject("originalGraphValidateCode", session.getAttribute(SESSION_ATTRIBUTENAME_GRAPHVALIDATECODE)));
		session.removeAttribute(SESSION_ATTRIBUTENAME_GRAPHVALIDATECODE);//用过一次即删除
		if(!originalGraphValidateCode.toLowerCase().equals(graphValidateCode.toLowerCase()))
			throw new ValidateCodeException("图片验证码不正确");
	}
	
	protected String getProcessedPassword(String password)
	{
		String psw = checkNullAndTrim("password", password)+PASSWORDSEED;
		byte[] pswarr = null;
		try{
			pswarr = psw.getBytes("utf-8");
		}catch(UnsupportedEncodingException e){
			
		}
		return DigestUtils.md5Hex(pswarr);
	}
	
	private char[] codeSequence = {'1', '2', '3', '4', '5', '6', '7', '8', '9','0'};
	private String getRandomValidateCode(int length)
	{
		StringBuffer randomCode = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
			randomCode.append(strRand);
		}
		return randomCode.toString();
	}

}
