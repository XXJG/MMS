package Winmms.MainClass;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


public class FRSAKey 
{
	//通过path路径创建rsakey
	public int createRSAKey(String phonenumber, String path)
	{
		File file = new File(path);
		File ffile = new File(UserString.headpath + "friends/");
		File refile = new File(UserString.headpath + "remine/");
		File tefile = new File(UserString.headpath + "temp/");
		
		if (!file.exists())
		{
			file.mkdir();
		}
		else
		{
			UserString.deleteFilesInDir(path);
		}
		if (!ffile.exists())
		{
			ffile.mkdir();
		}
		else
		{
			UserString.deleteFilesInDir(UserString.headpath + "friends/");
		}
		if (!refile.exists())
		{
			refile.mkdir();
		}	
		else
		{
			UserString.deleteFilesInDir(UserString.headpath + "remine/");
		}
		
		if (!tefile.exists())
		{
			tefile.mkdir();
		}	
		else
		{
			UserString.deleteFilesInDir(UserString.headpath + "temp/");
		}
		
		
		try
		{
			File privatefile = new File(path + "privateKey" + phonenumber + ".dat");
			File publicfile = new File(path + "publicKey" + phonenumber + ".dat");
			File reprivatefile = new File(UserString.headpath + "remine/" + "privateKey" + phonenumber + ".dat");
			File republicfile = new File(UserString.headpath + "remine/" + "publicKey" + phonenumber + ".dat");
			
			if (privatefile.exists() && publicfile.exists() &&
					reprivatefile.exists() && republicfile.exists())
			{
				System.out.println("You have created RSAKey for '" + phonenumber + "' !");
				return -1;
			}
			else
			{
				System.out.println("You are creating RSAKey for '" + phonenumber + "' !");
				KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
				keyGen.initialize(1024);
				KeyPair key = keyGen.generateKeyPair();
				
				FileOutputStream outputprivatefile = new FileOutputStream(privatefile); 
				FileOutputStream outputpublicfile = new FileOutputStream(publicfile);
				FileOutputStream reoutputprivatefile = new FileOutputStream(reprivatefile); 
				FileOutputStream reoutputpublicfile = new FileOutputStream(republicfile);
				
				outputprivatefile.write(key.getPrivate().getEncoded());
				outputpublicfile.write(key.getPublic().getEncoded());
				reoutputprivatefile.write(key.getPrivate().getEncoded());
				reoutputpublicfile.write(key.getPublic().getEncoded());
				
				outputprivatefile.close();
				outputpublicfile.close();
				reoutputprivatefile.close();
				reoutputpublicfile.close();
			}
		}
		catch (Exception ex)
		{
			System.out.println("new file failed in FRSAKey ::" + ex.toString());
			return 0;
		}
		return 1;
	}
	//通过path获得publickey
	public PublicKey getPublicKey(String phonenumber, String path)
	{
		//read public key 
		try
		{
			FileInputStream fsPublicKey = new FileInputStream(path + "publicKey" + phonenumber + ".dat"); 
			BufferedInputStream bfsPublicKey = new BufferedInputStream(fsPublicKey); 
			byte[] byteEncodedPublicKey = new byte[bfsPublicKey.available()]; 
			bfsPublicKey.read(byteEncodedPublicKey); 
			bfsPublicKey.close(); 

		//使用KeyFactory产生公钥 

		//build public key 
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(byteEncodedPublicKey); 
			KeyFactory keyFactory = KeyFactory.getInstance("RSA"); 
			PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
			return pubKey;
		}
		catch (Exception ex)
		{
			System.out.println("new FileinputStream failed in getPublicKey:" + ex.toString());
			return null;
		}
	}
	
	public static byte[] getPublicKeyEncode(String phonenumber, String path)
	{
		//read public key 
		try
		{
			FileInputStream fsPublicKey = new FileInputStream(path + "publicKey" + phonenumber + ".dat"); 
			BufferedInputStream bfsPublicKey = new BufferedInputStream(fsPublicKey); 
			byte[] byteEncodedPublicKey = new byte[bfsPublicKey.available()]; 
			bfsPublicKey.read(byteEncodedPublicKey); 
			bfsPublicKey.close(); 

		//使用KeyFactory产生公钥 

		//build public key
			return byteEncodedPublicKey;
		}
		catch (Exception ex)
		{
			System.out.println("new FileinputStream failed in getPublicKey: " + path + "," + phonenumber +ex.toString());
			return null;
		}
	}
	//通过默认路径获得publickey
	public PublicKey getPublicKey(String phonenumber)
	{
		//read public key 
		String path = UserString.headpath + "friends/";
		byte[] byteEncodedPublicKey;
		try
		{
			FileInputStream fsPublicKey = new FileInputStream(path + "publicKey" + phonenumber + ".dat"); 
			BufferedInputStream bfsPublicKey = new BufferedInputStream(fsPublicKey); 
			byteEncodedPublicKey = new byte[bfsPublicKey.available()]; 
			bfsPublicKey.read(byteEncodedPublicKey); 
			bfsPublicKey.close(); 
		}
		catch (Exception ex)
		{
			System.out.println("new FileinputStream failed in getPublicKey:"  + path + "," + phonenumber + ex.toString());
			return null;
		}
		try
		{
			//使用KeyFactory产生公钥 

			//build public key 
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(byteEncodedPublicKey); 
			KeyFactory keyFactory = KeyFactory.getInstance("RSA"); 
			PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
			return pubKey;
		}
		catch (Exception ex)
		{
			System.out.println("init key in getPublicKey:"  + path + "," + phonenumber + ex.toString());
			return null;
		}
	}
	//通过path获得privatekey
	public PrivateKey getPrivateKey(String phonenumber, String path)
	{
		//read public key 
		try
		{
			FileInputStream fsPrivateKey = new FileInputStream(path + "privateKey" + phonenumber + ".dat");  
			BufferedInputStream bfsPrivateKey = new BufferedInputStream(fsPrivateKey); 
			byte[] byteEncodedPrivateKey = new byte[bfsPrivateKey.available()];
			  
			//System.out.println("生成的公钥："+byteEncodedPublicKey);
			bfsPrivateKey.read(byteEncodedPrivateKey); 
			bfsPrivateKey.close();	
			//使用KeyFactory产生私钥 
			PKCS8EncodedKeySpec  privateKeySpec = new PKCS8EncodedKeySpec (byteEncodedPrivateKey); 
			KeyFactory privatekeyFactory = KeyFactory.getInstance("RSA"); 
			PrivateKey privKey = privatekeyFactory.generatePrivate(privateKeySpec);
			//System.out.println("生成的私钥："+privKey );
			  		
			return privKey;
		}
		catch (Exception ex)
		{
			System.out.println("new FileinputStream failed in getPublicKey:" + ex.toString());
			return null;
		}
	}
	//通过默认路径获得privatekey
	public PrivateKey getPrivateKey(String phonenumber)
	{
		//read public key 
		String path = UserString.headpath + "mine/";
		try
		{
			FileInputStream fsPrivateKey = new FileInputStream(path + "privateKey" + phonenumber + ".dat");  
			BufferedInputStream bfsPrivateKey = new BufferedInputStream(fsPrivateKey); 
			byte[] byteEncodedPrivateKey = new byte[bfsPrivateKey.available()];
			  
			//System.out.println("生成的公钥："+byteEncodedPublicKey);
			bfsPrivateKey.read(byteEncodedPrivateKey); 
			bfsPrivateKey.close();	
			//使用KeyFactory产生私钥 
			PKCS8EncodedKeySpec  privateKeySpec = new PKCS8EncodedKeySpec (byteEncodedPrivateKey); 
			KeyFactory privatekeyFactory = KeyFactory.getInstance("RSA"); 
			PrivateKey privKey = privatekeyFactory.generatePrivate(privateKeySpec);
			//System.out.println("生成的私钥："+privKey );
			  		
			return privKey;
		}
		catch (Exception ex)
		{
			System.out.println("new FileinputStream failed in getPublicKey:" + ex.toString());
			return null;
		}
	}
	
	public static byte[] getPrivateKeyEncode(String phonenumber,String path)
	{
		//read public key 
		try
		{
			FileInputStream fsPrivateKey = new FileInputStream(path + "privateKey" + phonenumber + ".dat");  
			BufferedInputStream bfsPrivateKey = new BufferedInputStream(fsPrivateKey); 
			byte[] byteEncodedPrivateKey = new byte[bfsPrivateKey.available()];
			  
			//System.out.println("生成的公钥："+byteEncodedPublicKey);
			bfsPrivateKey.read(byteEncodedPrivateKey); 
			bfsPrivateKey.close();	
			//使用KeyFactory产生私钥 
			  		
			return byteEncodedPrivateKey;
		}
		catch (Exception ex)
		{
			System.out.println("new FileinputStream failed in getPublicKey:" + ex.toString());
			return null;
		}
	}
}