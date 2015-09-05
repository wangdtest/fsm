package com.furoom.xml;

import com.furoom.support.ClassInfo;
import com.furoom.support.ClassInfoPool;

public class XMLClassInfoPool extends ClassInfoPool {

	public XMLClassInfoPool() {
	}
	
	public synchronized <C> XMLClassInfo<C> getClassInfo(Class<C> c, boolean buildPropertyIndex){
		ClassInfo<C> ci = null;
		if ( (ci = caches.get(c)) == null ){
			caches.put(c, ci = new XMLClassInfo<C>(c));
		}
		if (!ci.isPropertyIndexBuilt() && buildPropertyIndex){
			ci.buildPropertyIndex();
		}
		return (XMLClassInfo<C>)ci;
	}
}
