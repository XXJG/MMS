package Winmms.MainClass;

public class Algorithm 
{
	public static void main(String[] args) throws Exception
	{
		String A = "123";
		String B = "456";
		String AMessage = "��ã��ܸ����ܹ���ʶ�㣬���Խ�������ô��";
		System.out.println("Send: " + AMessage);
		
		MsgEncrypt msge = new MsgEncrypt();
		
		byte[] sentMessage = msge.encrypt(AMessage, A, B);
		System.out.println("Receiver: " + msge.decrypt(sentMessage, A, B));
		
	}
}
