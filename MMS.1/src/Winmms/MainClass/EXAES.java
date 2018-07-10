package Winmms.MainClass;


public class EXAES 
{
	
	public byte[] Encrypt(byte[] Text, String password) throws Exception
	{
		byte[] result = AES.encry(Text, password);
		if (result != null)
		{
			return result;
		}
		else
		{
			return null;
		}
	}
	
	public byte[] Decrypt(byte[] Text, String password) throws Exception
	{
		byte[] result = AES.decry(Text, password);
		if (result != null)
		{
			return result;
		}
		else
		{
			return null;
		}
	}
}