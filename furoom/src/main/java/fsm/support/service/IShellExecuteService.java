package fsm.support.service;

import fsm.service.exception.ShellExecuteException;

public interface IShellExecuteService {
	public void executeMysqlDump() throws ShellExecuteException;
}
