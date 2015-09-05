package fsm.service.exception;

public class ProductSoldOutException extends Exception{
	public ProductSoldOutException()
	{
		super();
	}
	public ProductSoldOutException(String msg)
	{
		super(msg);
	}
}
