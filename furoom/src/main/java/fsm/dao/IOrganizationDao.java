package fsm.dao;

import java.util.List;
import fsm.model.Organization;

public interface IOrganizationDao {
	public List<Organization> findAll();
	public Organization find(Integer id);
	public void create(Organization organization);
}
