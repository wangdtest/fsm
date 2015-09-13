package fsm.inner.service;

public class City {
	private String name;
	private String code;
	Province province;
	public City(){
		
	}
	public City(String name, String code){
		this.name = name;
		this.code = code;
	}
	public City(String name, String code, Province province){
		this.name = name;
		this.code = code;
		this.province = province;
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
	public Province getProvince() {
		return province;
	}
	public void setProvince(Province province) {
		this.province = province;
	}
	@Override
	public boolean equals(Object obj){
		if(obj==null){
			return false;
		}
		if(this==obj){
			return true;
		}
		if(obj.getClass()!=this.getClass()){
			return false;
		}
		City ocity = (City)obj;
		if(ocity.getCode()==null){
			return false;
		}else if(ocity.getCode().equals(this.getCode())){
			return true;
		}else{
			return false;
		}
	}
	@Override
	public int hashCode() {   
	    return this.code.hashCode();
	}
}
