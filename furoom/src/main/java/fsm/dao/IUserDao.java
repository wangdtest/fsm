package fsm.dao;

import fsm.model.User;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface IUserDao {
	public int countAll();
	public List<User> findAll(@Param("offset")int offset,@Param("recnum")int recnum);
	public List<User> findAllByPrivilege(@Param("privilege")int privilege);
	public User find(Integer id);
	public User findByLoginId(String loginId);
	public User findByLoginIdAndPassword(@Param("loginId") String loginId,@Param("password") String password);
	public User findByAccountID(Integer accountId);
	public void create(User user);
	public void changePrivilege(@Param("id") Integer id,@Param("privilege") int privilege);
	public void changePassword(@Param("id") Integer id,@Param("password") String password);
}
