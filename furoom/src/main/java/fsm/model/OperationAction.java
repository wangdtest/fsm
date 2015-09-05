package fsm.model;

public class OperationAction {
	public static final int STATE_INIT = 0;
	public static final int STATE_PROCESSING = 1;
	public static final int STATE_DONE = 2;
	public static final int STATE_EXCEPTION = 3;
	
	
	private Integer id;
	private Integer operationId;
	private String title;
	private String description;
	private long createTime;
	private long lastmodifyTime;
	private int state=STATE_INIT;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getOperationId() {
		return operationId;
	}
	public void setOperationId(Integer operationId) {
		this.operationId = operationId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public long getLastmodifyTime() {
		return lastmodifyTime;
	}
	public void setLastmodifyTime(long lastmodifyTime) {
		this.lastmodifyTime = lastmodifyTime;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
}
