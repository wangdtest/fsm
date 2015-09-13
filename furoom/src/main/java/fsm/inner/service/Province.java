package fsm.inner.service;

import java.util.ArrayList;
import java.util.List;

public class Province {
	private String name;
	private String code;
	List<City> citys = new ArrayList<City>();
	public Province(){
		
	}
	public Province(String name, String code){
		this.name = name;
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<City> getCitys() {
		return citys;
	}
	public void setCitys(List<City> citys) {
		this.citys = citys;
	}
	public void addCity(City city){
		if(!citys.contains(city))
		citys.add(city);
	}
	public void removeCity(City city){
		citys.remove(city);
	}
}
