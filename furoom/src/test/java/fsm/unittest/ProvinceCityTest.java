package fsm.unittest;

import junit.framework.Assert;

import org.junit.Test;

import fsm.inner.service.City;

public class ProvinceCityTest {
	@Test
	public void TestCity(){
		City city1 = new City("邯郸", "001001");
		City city2 = new City("邢台", "001002");
		City city3 = new City("曲周", "001001");
		Assert.assertEquals(city1, city3);
		Assert.assertNotSame(city1, city2);
		Assert.assertEquals(city1, city3);
	}
}
