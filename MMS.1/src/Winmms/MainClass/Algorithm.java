package Winmms.MainClass;

public class Algorithm 
{
	public static void main(String[] args) throws Exception
	{
		String A = "123";
		String B = "456";
		String AMessage = "你好，很高兴能够认识你，可以交个朋友么？";
		System.out.println("Send: " + AMessage);
		
		MsgEncrypt msge = new MsgEncrypt();
		
		byte[] sentMessage = msge.encrypt(AMessage, A, B);
		System.out.println("Receiver: " + msge.decrypt(sentMessage, A, B));
		
	}
}
