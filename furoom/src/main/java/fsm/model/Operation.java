package fsm.model;

import java.util.ArrayList;
import java.util.List;

public class Operation {
	public static final int STATE_APPLY = 0;
	public static final int STATE_AUDIT_PASS = 1;
	public static final int STATE_AUDIT_REFUSE = 2;
	public static final int STATE_PROCESSING = 3;
	public static final int STATE_DONE = 4;
	public static final int STATE_EXCEPTION = 5;
	
	private Integer id;
	private int state = STATE_APPLY;
	private Integer productId;
	private Integer userId;
	private Integer agentId;
	private int loanType=Product.TYPE_HOUSE;
	private int amount=0;//单位是万
	private int period=0;//单位是月
	private String province;
	private String city;
	private long createtime;
	private long lastmodifytime;
	private List<OperationAction> actions = new ArrayList<OperationAction>();
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
	public Integer getProductId() {
		return productId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public int getLoanType() {
		return loanType;
	}
	public void setLoanType(int loanType) {
		this.loanType = loanType;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
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
	public long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}
	public long getLastmodifytime() {
		return lastmodifytime;
	}
	public void setLastmodifytime(long lastmodifytime) {
		this.lastmodifytime = lastmodifytime;
	}
	public List<OperationAction> getActions() {
		return actions;
	}
	public void setActions(List<OperationAction> actions) {
		this.actions = actions;
	}
	public Integer getAgentId() {
		return agentId;
	}
	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}
}
