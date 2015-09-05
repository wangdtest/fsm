package fsm.model;

import java.util.ArrayList;
import java.util.List;

public class Product {
	public static final int STATE_INIT = 0;
	public static final int STATE_APPLY = 1;
	public static final int STATE_AUDIT_PASS = 2;
	public static final int STATE_AUDIT_REFUSE = 3;
	public static final int STATE_INVALID = 4;
	
	public static final int TYPE_HOUSE = 0;
	public static final int TYPE_CAR = 1;
	public static final int TYPE_XIAOFEI = 2;
	public static final int TYPE_JINGYING = 3;
	public static final int TYPE_DIYA = 4;
	public static final int TYPE_XINYONG = 5;
	//主键ID
	private Integer id;
	private String title;
	private int state = STATE_INIT;
	private int loanType = TYPE_HOUSE;
//	private Integer userId;
	private String cityCode;
	private Integer organizationId;
	private String profits;
	private String requires;
	
	//辅助对象，不持久化
	private List<ProductAction> actions = new ArrayList<ProductAction>();
	private String companyname;
	private int related = 0;
	private String[] profitsArray;
	private String[] requiresArray;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
//	public Integer getUserId() {
//		return userId;
//	}
//	public void setUserId(Integer userId) {
//		this.userId = userId;
//	}
	public List<ProductAction> getActions() {
		return actions;
	}
	public void setActions(List<ProductAction> actions) {
		this.actions = actions;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public Integer getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}
	public String getProfits() {
		return profits;
	}
	public void setProfits(String profits) {
		this.profits = profits;
	}
	public String getRequires() {
		return requires;
	}
	public void setRequires(String requires) {
		this.requires = requires;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getLoanType() {
		return loanType;
	}
	public void setLoanType(int loanType) {
		this.loanType = loanType;
	}
	public String getCompanyname() {
		return companyname;
	}
	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}
	public String[] getProfitsArray() {
		return profitsArray;
	}
	public void setProfitsArray(String[] profitsArray) {
		this.profitsArray = profitsArray;
	}
	public String[] getRequiresArray() {
		return requiresArray;
	}
	public void setRequiresArray(String[] requiresArray) {
		this.requiresArray = requiresArray;
	}
	public int getRelated() {
		return related;
	}
	public void setRelated(int related) {
		this.related = related;
	}
}
