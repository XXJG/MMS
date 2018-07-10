package Winmms.MainClass;

import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;

public class EXRSA 
{
	public byte[] Encrypt(byte[] ByteText, PublicKey key) throws Exception
	{ 
		  Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		  cipher.init(Cipher.ENCRYPT_MODE,key);
		  
		  byte[] cipherText = cipher.doFinal(ByteText);
		  
		  return cipherText;	
	}
	public byte[] Decrypt(byte[] BytecipherText,PrivateKey key) throws Exception
	{
		  
		  Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		  cipher.init(Cipher.DECRYPT_MODE,key);
		  byte[] newByteText = cipher.doFinal(BytecipherText);
		  
		  return newByteText;
	}
}
