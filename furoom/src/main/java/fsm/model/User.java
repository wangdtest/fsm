package fsm.model;

import com.furoom.security.Permit;

public class User implements Permit {
	public static final int PRIVILEGE_USER = 0;//普通用户
	public static final int PRIVILEGE_AGENT_APPLY = 10;//信贷经理
	public static final int PRIVILEGE_AGENT_AUDIT_SUCCESS = 11;//信贷经理
	public static final int PRIVILEGE_AGENT_AUDIT_REFUSE = 12;//信贷经理
	public static final int PRIVILEGE_OPERATOR = 30;//分公司业务员
	public static final int PRIVILEGE_ADMIN = 100;//超级管理员
	
	public static final String SEX_MAN = "man";
	public static final String SEX_WOMAN = "woman";
	
	
	private Integer id;//主键ID
	private String name;//名称
	private String sex=SEX_MAN;
	private String tel;//手机
	private String email;//email
	private String loginId;//登录名
	private String password;//md5加密密码
	private Integer accountId;//账户ID
	private Integer appendixId; //附加资料ID，根据privilege的不同，关联不同类型的实体
	
	private Integer organizationId;
	private String province;
	private String city;
	
	private long createtime=System.currentTimeMillis();//创建时间
	
	private int privilege=PRIVILEGE_USER;//用户角色
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public int getPrivilege() {
		return privilege;
	}

	public void setPrivilege(int privilege) {
		this.privilege = privilege;
	}
	public Integer getAppendixId() {
		return appendixId;
	}

	public void setAppendixId(Integer appendixId) {
		this.appendixId = appendixId;
	}
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
