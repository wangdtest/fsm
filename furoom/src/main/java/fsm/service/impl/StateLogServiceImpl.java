package fsm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fsm.dao.IStateLogDao;
import fsm.model.StateLog;
import fsm.support.service.IStateLogService;
@Service
public class StateLogServiceImpl implements IStateLogService {
	@Autowired
	IStateLogDao stateLogDao;
	@Override
	public void create(Integer refId, int target, int type) {
		StateLog stateLog = new StateLog();
		stateLog.setCreatetime(System.currentTimeMillis());
		stateLog.setRefid(refId);
		stateLog.setTarget(target);
		stateLog.setType(type);
		stateLogDao.create(stateLog);
	}

	@Override
	public void change(Integer refId, int source, int target, int type) {
		StateLog stateLog = new StateLog();
		stateLog.setCreatetime(System.currentTimeMillis());
		stateLog.setRefid(refId);
		stateLog.setSource(source);
		stateLog.setTarget(target);
		stateLog.setType(type);
		stateLogDao.create(stateLog);
	}

}
