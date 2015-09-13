package fsm.inner.service;

import java.util.List;

public interface IInnerProvinceCityService {
	public Province findProvinceByCityCode(String cityCode) throws Exception;
	public Province findProvinceByCode(String provinceCode) throws Exception;
	public List<City> getCitysByProvinceCode(String provinceCode) throws Exception;
	public City findCityByCode(String cityCode) throws Exception;
}
