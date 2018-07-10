package Winmms.MainClass;

import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class MsgEncrypt 
{
	public byte[] encrypt(String text, String sender, String receiver)
	{
		String Aespassword = String.valueOf(Math.random());
		byte[] byteAMessage;
		byte[] byteAespassword;
		
		try
		{
			byteAMessage = this.get16Bytes( text.getBytes("utf-8") );
			System.out.println("-----------" + byteAMessage.length);
			byteAespassword = Aespassword.getBytes("utf-8");
			UserString.aes = Aespassword + "||" + new String(byteAMessage, "utf-8");
			System.out.println("正文：" + byteAMessage.length);
		}
		catch (Exception ex)
		{
			System.out.println("utf: " + ex.toString());
			return null;
		}
		
		SIGN sign = new SIGN();
		EXRSA rsa = new EXRSA();
		EXAES aes = new EXAES();
		FRSAKey key = new FRSAKey();
		
		PrivateKey privatekey = key.getPrivateKey(sender);
		PublicKey publickey =  key.getPublicKey(receiver);
		
		if (publickey == null)
		{
			System.out.println("get publickey failed!");
			return null;
		}
		
		if (privatekey == null)
		{
			System.out.println("get privatekey failed!");
			return null;
		}
		
		//加密
		System.out.println("sign");
		//对正文进行哈希处理，并对其进行私鈅加密
		
		try
		{
			//
			byte[] byteSign = sign.Privatekeysignature(byteAMessage, privatekey);
			System.out.println("Sign：" + byteSign.length);
		
			//对有认证信息和正文组成的消息进行AES加密
			//byte[] message = byteCat(byteAMessage, UserString.splite, byteSign);
			byte[] byteAesMessage = aes.Encrypt(byteAMessage, Aespassword); 
			System.out.println("byteAesMessage：" + byteAesMessage.length);
			UserString.aes += "||" + UserString.bytesToHexString(byteAesMessage);
			UserString.aes += "||" + UserString.bytesToHexString(byteSign);
			byte[] byteMessageSign = byteCat(byteAesMessage, UserString.splite, byteSign);
			System.out.println("aes");
		
			//对AES的密匙进行RSA公鈅加密
			byte[] byteRSAbyteAespassword = rsa.Encrypt(byteAespassword, publickey);
			System.out.println("byteRSAbyteAespassword：" + byteRSAbyteAespassword.length);
			System.out.println("rsa");
		
			//整合信息
			byte[] sentMessage = byteCat(byteMessageSign, UserString.splite, byteRSAbyteAespassword);		
			System.out.println("*************");
			System.out.println("正文：" + sentMessage.length * 2);
			return sentMessage;
		}
		catch (Exception ex)
		{
			System.out.println("in encrypt failed!");
			return null;
		}
	}
	public String decrypt(byte[] text, String sender, String receiver)
	{
		SIGN sign = new SIGN();
		EXRSA rsa = new EXRSA();
		EXAES aes = new EXAES();
		FRSAKey key = new FRSAKey();
		
		if (!UserString.fileIsExists(UserString.headpath + "mine/privateKey" + receiver + ".dat"))
		{
			//return UserString.headpath + "mine/privateKey" + receiver + ".dat isn't existed!\n";
			return "Your Message was destroyed, please option again!";
		}
		
		if (!UserString.fileIsExists(UserString.headpath + "friends/publicKey" + sender + ".dat"))
		{
			return UserString.headpath + "friends/publicKey" + sender + ".dat isn't existed!\n";
		}
		
		PrivateKey privatekey = key.getPrivateKey(receiver, UserString.headpath + "mine/");
		PublicKey publickey = key.getPublicKey(sender, UserString.headpath + "friends/");
		
		if (publickey == null)
		{
			System.out.println("get publickey failed!");
			return UserString.headpath + "friends/" + sender + ".dat get publickey failed! because it's null";
		}
		
		if (privatekey == null)
		{
			System.out.println("get privatekey failed!");
			return "get privatekey failed! because it's null";
		}
		
		System.out.println("rsa");
		//分离 正文与（认证，aes密钥）
		int indesofsplite = findIndexOfSplite(text, UserString.splite);
		byte[] messagegetBody = getByteFromIndex(text, 0, indesofsplite);
		
		byte[] messagegetAeskeyandSign = getByteFromIndex(text, UserString.splite.length() + indesofsplite
				, text.length - UserString.splite.length() - indesofsplite);
		//分离 认证与aes密钥
		indesofsplite = findIndexOfSplite(messagegetAeskeyandSign, UserString.splite);
		byte[] getSign = getByteFromIndex(messagegetAeskeyandSign, 0, indesofsplite);
		
		byte[] messagegetAeskey = getByteFromIndex(messagegetAeskeyandSign, UserString.splite.length() + indesofsplite
				, messagegetAeskeyandSign.length - UserString.splite.length() - indesofsplite);
		byte[] getbyteAespassword;
		try 
		{
			getbyteAespassword = rsa.Decrypt(messagegetAeskey, privatekey);
			UserString.aess = new String(getbyteAespassword, "utf-8");
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			System.out.println("0:: " + e.toString());
			return "0 : " + e.toString();
		}
		
		
		//获得正文
		System.out.println("aes");
		byte[] getMessage;
		try 
		{
			getMessage = aes.Decrypt(messagegetBody, new String(getbyteAespassword, "utf-8"));
		} 
		catch (Exception e) 
		{
				// TODO Auto-generated catch block
			System.out.println("1:: " + e.toString());
			return "1 : " + e.toString();
		} 
			System.out.println("*************");
		
		//进行认证
		System.out.println("sign");
		int flag;
		try 
		{
			flag = sign.PublicKeySignatureVerification(getMessage, publickey, getSign);
		} 
		catch (Exception e) 
		{
				// TODO Auto-generated catch block
			System.out.println("2:: " + e.toString());
			return "2 : " + e.toString();
		}
		
		if (flag == 1)
		{
			System.out.println("Signature success!");
			try 
			{
				return new String(getMessage, "utf-8");
			} 
			catch (UnsupportedEncodingException e) 
			{
					// TODO Auto-generated catch block
				System.out.println("d3:: " + e.toString());
				return "3 : " + e.toString();
			}
		}
		else if(flag == 0)
		{
			try 
			{
				return UserString.secure_notice_insign + new String(getMessage, "utf-8");
			} 
			catch (UnsupportedEncodingException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "4 : " + e.toString();
			}
		}	
		else
		{
			return " Signature error!";
		}
	}
	private  int findIndexOfSplite(byte[] value, String str)
	{
		int i = 0;
		for (int flag = 1; i < value.length;i++)
		{
			flag = 1;
			for (int j = 0; j < str.length(); j++)
			{
				if (i + j > value.length)
				{
					flag = 0;
					break;
				}
				if (value[i + j] != (byte)str.charAt(j))
				{
					flag = 0;
					break;
				}
			}
			if (flag == 1)
			{
				return i;
			}
		}
		return i;
	}
	
	private  byte[] byteCat(byte[] value1, String insert, byte[] value2)
	{
		byte[] value = new byte[value1.length + insert.length() + value2.length];
		
		for (int i = 0; i < value1.length; i++)
		{
			value[i] = value1[i];
		}
		for (int i = 0; i < insert.length(); i++)
		{
			value[value1.length + i] = (byte)insert.charAt(i);
		}
		for (int i = 0; i < value2.length; i++)
		{
			value[value1.length + insert.length() + i] = value2[i];
		}
		
		return value;
	}
	
	public  byte[] getByteFromIndex(byte[] value, int index, int size)
	{
		byte[] result = new byte[size];
		
		for (int i = 0; i < size; i++)
		{
			result[i] = value[i + index];
		}
		return result;
	}
	private byte[] get16Bytes(byte[] value)
	{
		int length = (value.length >> 4)  << 4;
		
		if ( value.length % 16 != 0 )
		{
			length += 16;
		}
		
		byte[] result = new byte[length];
		
		for (int i = 0; i < length; i++)
		{
			if ( i < value.length )
			{
				result[i] = value[i];
			}
			else
			{
				result[i] = (byte)'.';
			}
		}
		return result;
	}
}
