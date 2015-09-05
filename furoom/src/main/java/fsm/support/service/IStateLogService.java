package fsm.support.service;

public interface IStateLogService {
	public void create(Integer refId, int target, int type);
	public void change(Integer refId, int source, int target, int type);
}
