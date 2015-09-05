package com.furoom.security;

import java.util.ArrayList;
import java.util.List;

import com.furoom.xml.annotation.CollectionStyleType;
import com.furoom.xml.annotation.XMLMapping;

public class StaticRolesConfig {
	List<Role> roles = new ArrayList<Role>();
	boolean enabled=false;
	@XMLMapping(collectionStyle = CollectionStyleType.FLAT, childTag = "Role")
	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
