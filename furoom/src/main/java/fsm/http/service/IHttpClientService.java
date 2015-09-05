package fsm.http.service;

import java.util.Map;

public interface IHttpClientService {
	public  String post(String url, Map<String, String> params) ;
}
