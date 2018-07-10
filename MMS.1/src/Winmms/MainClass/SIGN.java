package Winmms.MainClass;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class SIGN 
{
	public byte[] Privatekeysignature(byte[] ByteText,PrivateKey key) throws Exception
	{ 
		  Signature sig = Signature.getInstance("SHA1WithRSA");
		  sig.initSign(key);
		  sig.update(ByteText);
		  byte[] signature = sig.sign();
		  //System.out.println(new String(signature,"UTF8"));		
		  
		  return signature;
	}
	public int PublicKeySignatureVerification(byte[] ByteText, PublicKey key, byte[] signature) 
	throws Exception
	{
		
		Signature sig = Signature.getInstance("SHA1WithRSA");
		sig.initVerify(key);
	    sig.update( ByteText );
	    try
	    {
	        if(sig.verify(signature))
	        {
	        	return 1;
	        }
	        else
	        {
	        	 return 0;
	        }
	    }catch(Exception e)
	    {	    
	        System.out.println("Signature failed/»œ÷§ ß∞‹:" + e.toString());	
	        return -1;
	    }
	}
	
}
