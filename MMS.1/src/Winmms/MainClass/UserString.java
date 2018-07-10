package Winmms.MainClass;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class UserString 
{
	public static final String headpath = "/data/data/Winmms.ACT/";
	public static final String secure_notice_insign = "Notice:\n[身份认证失败，\n短信内容不安全] :\nBody:\n";
	public static final int msg_length = 120;
	public static final short port = 5000;
	public static String thistelephone = "this";
	public final static String splite = "@@@@@@";
	private static String numberSize = "0123456789ABCDEFGHIJKLMNOPQRSTUVWSYZabcdefghigklmnopqrstuvwxyz!@";
	private static String number = "0123456789ABCDEF";
	
	public static String[] projection = {"_id","address","date","body"};
	public static final int __id = 0;
	public static final int _address = 1;
	public static final int _date = 2;
	public static final int _body = 3;
	public static String sortOrderasc = "_id asc";
	public static String sortOrderdesc = "_id desc";
	
	public static String aes = "";
	public static String aess = "";
	public static String aeskey = "";
	public static String aesskey = "";
	public static final String headkey = "11111K";
	public static final String headencrypt = "11111O";
	
	public static final int[] phoneremark = {3,8};
	
	/**
	 * to judge whether the string is key.
	 * @param value
	 * @return
	 */
	public static boolean isKey(String value)
	{
		boolean iskey = true;
		if (value.length() < 6)
		{
			return false;
		}
		if (value.charAt(5) != 'K')
		{
			iskey = false;
		}
		return iskey;
	}
	public static boolean isdecryptf(String value)
	{
		boolean isdecryptf = true;
		if (value.length() < 6)
		{
			return false;
		}
		if (value.charAt(5) != 'O')
		{
			isdecryptf = false;
		}
		return isdecryptf;
	}
	/* Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。  
	 * @param src byte[] data  
	 * @return hex string  
	 */     
	public static String bytesToHexString(byte[] src)
	{  
		if (src == null || src.length <= 0) 
	    {  
	        return null;  
	    }  
		
		StringBuilder stringBuilder = new StringBuilder(""); 
		
	    for (int i = 0; i < src.length; i++) 
	    {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) 
	        {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    
	    return stringBuilder.toString().toUpperCase();  
	}  
	
	/** 
	 * Convert hex string to byte[] 
	 * @param hexString the hex string 
	 * @return byte[] 
	 */  
	public static byte[] hexStringToBytes(String hexString) 
	{  
	    if (hexString == null || hexString.equals("")) 
	    {  
	        return null;  
	    } 
	    
	    hexString = hexString.toUpperCase();  
	    
	    int length = hexString.length() / 2; 
	    char[] hexChars = hexString.toCharArray();  
	    byte[] d = new byte[length];
	    
	    for (int i = 0; i < length; i++) 
	    {  
	        int pos = i * 2;  
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
	    }  
	    
	    return d;  
	}  
	
	/** 
	 * Convert char to byte 
	 * @param c char 
	 * @return byte 
	 */  
	
	 private static byte charToByte(char c) 
	 {  
	    return (byte) number.indexOf(c);  
	 }
	 
	/** 
	 * Convert int to char
	 * @param int i 
	 * @return char 
	 */  
		
	 private static char intToChar(int i) 
	 {  
		 return  numberSize.charAt(i);  
	 }
	 
	 /** 
	  * Convert  char to int
	  * @param c char 
	  * @return int 
	  */  
			
	 private static int charToInt(char c) 
	 {  
		 return  numberSize.indexOf(c);  
	 }
	 /**
	  * Splite String into serveral parting by parting length
	  * @param resource String, length int
	  * @return String[] 
	  * 0-4 位 	文件唯一标识位
	  * 5位	文件类型标识  'o' 普通文件,'k' 密匙文件, 'O' 独立加密文件，'K' 独立密钥文件
	  * 6位 第三位 序号位
	  **/
	 
	 public static String[] createSendMessageByLength(String resource, int size, char type)
	 {
		 int row = 0;
		 final int remark = 7;
		 int[] date = currentDate();
		 int length = (size - remark);
		 
		 row = resource.length() / length;
 		 
		 char[][] value = new char[row][length + remark];
		 char[] tail = new char[(resource.length() % length) + remark];
		 String[] res; 
	
		 for (int i = 0; i < row; i++)
		 {
			 value[i][0] = intToChar(date[0]); 
			 value[i][1] = intToChar(date[1]);
			 value[i][2] = intToChar(date[2]);
			 value[i][3] = intToChar(date[3]);
			 value[i][4] = intToChar(date[4]);
			 
			 value[i][5] = type;
			 value[i][6] = intToChar(i);
			 
			 for (int j = 0; j < length; j++)
			 {
				 value[i][j + remark] = resource.charAt(i * length + j);
			 }
		 }
		 
		 for (int i = 0; i < tail.length - remark; i++)
		 {
			 tail[0] = intToChar(date[0]); 
			 tail[1] = intToChar(date[1]);
			 tail[2] = intToChar(date[2]);
			 tail[3] = intToChar(date[3]);
			 tail[4] = intToChar(date[4]);
			 tail[5] = type;
			 tail[6] = '@';
			 
			 tail[i + remark] = resource.charAt(row * length + i);
		 }
		 int strRow = row;
		 
		 if (tail.length == 0)
		 {
			 value[row - 1][6] = '@';
		 }
		 else
		 {
			 strRow++;
		 }
		 
		 res = new String[strRow];
		 
		 for (int i = 0; i< row; i++)
		 {
			 res[i] = new String(value[i]);
		 }
		 
		 if (row < strRow)
		 {
			 res[row] = new String(tail);
		 }
		 
		 return res;
	 }
	 
	 
	 /**
	  * Link all parts to a whole String 
	  *@parm resource String[]
	  *@return String
	  **/
	 
	 public static String linkStrings(String[] resource)
	 {
		 String str = "";
		 final int remark = 7;
		 String[] value = new String[resource.length];
		 for (int i = 0; i < value.length; i++)
		 {
			 value[i] = "";
		 }
		 boolean is_complete = false;
		 // 重新排序字符串组 by 6 位序号位 并 判断这是一个正常结束的字符串组
		 for (int i = 0; i < resource.length; i++)
		 {
			 if (resource[i].charAt(6) == '@')
			 {
				 value[resource.length - 1] = resource[i];
				 is_complete = true;
			 }
			 else
			 {
				 if (charToInt( resource[i].charAt(6)) > resource.length - 1) 
				 {
					 System.out.println("index:   " + charToInt( resource[i].charAt(6) ) + "," + resource.length);
					 break;
				 }
				 value[charToInt( resource[i].charAt(6) )] = resource[i];
			 }
		 }
		 // 判断给定字符串 组 正确的含有 0 - end
		 for (int i = 0; i < value.length; i++)
		 {
			 if (value[i].equals(""))
			 {
				 is_complete = false; 
			 }
		 }
		 if (!is_complete)
		 {
			return null; 
		 }
		 for (int i = 0; i < value.length; i++)
		 {
			 System.out.println("str+:   " + remark + "," + value[i].length());
			 str += value[i].substring(remark, value[i].length()); 
		 }
		 
		 return str;
	 }
	 
	 public static boolean isFromEqualMsg(String str1, String str2)
	 {
		boolean is = true;
		int end = 5;
		for (int i = 0; i < end; i++)
		{
			if (str1.charAt(i) != str2.charAt(i))
			{
				is = false;
			}
		}
		return is;
	 }
	 /**
	  * 
	  * @param path
	  * @return
	  */
	 public static boolean fileIsExists(String path)
	 {
		 File file = new File(path);
		 if (!file.exists())
		 {
			 System.out.println("file: " + path + "isn't exist!");
			 return false;
		 }
		 return true;
	 }
	 
	 public static int createFile(String path)
	 {
		 File file = new File(path);
		 if (!file.exists())
		 {
			 System.out.println("file: " + path + "isn't exist!");
			 try 
			 {
				file.createNewFile();
			 } 
			 catch (IOException e) 
			 {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -1;
			 }
		 }
		 else
		 {
			 return 0;
		 }
		 return 1;
	 }
	 public static boolean deleteFile(String path)
	 {
		 File file = new File(path);
		 file.delete();
		 return true;
	 }
	 
	 public static boolean deleteFilesInDir(String path)
	 {
		 File file = new File(path);
		 if (!file.exists())
		 {
			 System.out.println("file: " + path + "isn't exist!");
			 return true;
		 }
		 else
		 {
			 File[] files = file.listFiles();
			 if (files != null)
			 {
				 for (int i = 0; i < files.length; i++)
				 {
					 files[i].delete();
				 }
			 } 
			 return true;
		 }
	 }
	 public static String deleteEnter(String value)
	 {
		 while ( value.charAt(value.length() - 1) == '\n' && value.length() > 0)
		 {
			 value = value.substring(0, value.length() - 1);
			 if (value.length() < 1)
			 {
				 break;
			 }
		 }	 
		 return value;
	 }
	 public static String getChinaNumber(String number)
	 {
		 if (number.length() > 11)
		 {
			 return number.substring(number.length() - 11, number.length());
		 }
		 return number;
	 }
	 public static String getNumber(String number)
	 {
		 String str = "0";
		 for (int i = 0; i < number.length(); i++)
		 {
			 if (number.charAt(i) != ' ')
			 {
				str += number.charAt(i);
			 }
		 }
		 return str;
	 }
	 public static int[] currentDate()
	 {
		 int[] date = new int[5];
		 final Calendar c = Calendar.getInstance(); 
	      
		 date[0] = c.get(Calendar.MONTH);//获取当前月份 
		 date[1] = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码 
		 date[2] = c.get(Calendar.HOUR_OF_DAY);//获取当前的小时数 
		 date[3] = c.get(Calendar.MINUTE);//获取当前的分钟数   
		 date[4] = c.get(Calendar.SECOND);//获取当前的分钟数   
	     
		 return date;
	 }
}
