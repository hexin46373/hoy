package ws.hoyland.qqonline;

import java.io.ByteArrayOutputStream;
import java.net.*;
import java.util.Random;

import sun.io.Converters;

import ws.hoyland.util.Converts;
import ws.hoyland.util.Crypter;

public class T2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String account = "287705149";
		String password = "ghsjiygrtr";
		byte[] ips = new byte[]{
				(byte)183, (byte)60, (byte)19, (byte)100
		};
		// 数据
//		final short[] sbuf = new short[] { 
//				0x02, 0x34, 0x4B, 0x08, 0x25, 0x37,
//				0x94, 0x04, 0x10, 0x07, 0x2C, 0x03, 0x00, 0x00, 0x00, 0x01,
//				0x01, 0x01, 0x00, 0x00, 0x66, 0xA2, 0x00, 0x00, 0x00, 0x00,
//				0x59, 0x75, 0x25, 0x3B, 0xF3, 0x91, 0x58, 0x4C, 0x07, 0xC0,
//				0xC5, 0x5A, 0x1D, 0x01, 0xBB, 0x8A, 0x76, 0x4E, 0xC8, 0xE5,
//				0x75, 0xA8, 0xCD, 0x6F, 0x22, 0xDA, 0xEE, 0x66, 0x08, 0xFB,
//				0x0F, 0xCD, 0x82, 0x07, 0x70, 0xF4, 0x09, 0xC8, 0xC7, 0xDF,
//				0xE2, 0xF1, 0xB4, 0x7F, 0x36, 0xB8, 0x57, 0x58, 0x3F, 0xDF,
//				0x18, 0x82, 0x90, 0xD7, 0xE8, 0x25, 0x1F, 0xDF, 0xD0, 0x14,
//				0xA6, 0x0D, 0xB0, 0x90, 0x4A, 0x08, 0xFA, 0xF3, 0xF3, 0x4B,
//				0x0E, 0x04, 0xCF, 0x52, 0x88, 0x74, 0x1F, 0x52, 0x16, 0x8A,
//				0x19, 0x95, 0x2E, 0x4E, 0x26, 0x0A, 0x8D, 0xA7, 0x38, 0x56,
//				0x61, 0xE5, 0xDE, 0xDD, 0x56, 0x02, 0xCF, 0x47, 0xAB, 0xD8,
//				0x35, 0xF6, 0xAC, 0x05, 0xB9, 0x2A, 0x10, 0xBE, 0xA1, 0x5A,
//				0xF8, 0x5C, 0x6D, 0x69, 0x01, 0x0A, 0x54, 0x9B, 0x29, 0x1D,
//				0x03 };
//		
//		byte[] buf = new byte[sbuf.length];
//		for (int i = 0; i < sbuf.length; i++) {
//			//buf[i] = sbuf[i] > 0xEF ? (byte) (sbuf[i]& 0xFF) : (byte) sbuf[i];
//			buf[i] = (byte) (sbuf[i]& 0xFF);
//		}
		short seq = 0x1123; //包序号
		
		//联接服务器,发送数据
		DatagramSocket ds = null;
		DatagramPacket dpIn = null;
		DatagramPacket dpOut = null;
		
		byte[] buf = null;
		byte[] buffer = new byte[8092];
		
		ByteArrayOutputStream baos = null; 
		
		ByteArrayOutputStream bsofplain = null; 
		
		byte[] rndkey = genKey(0x10);
		byte[] ecdhkey = genKey(0x19);
		
		Crypter crypter = new Crypter();
		byte[] encrypt = null;
		byte[] decrypt = null;
		
		try{
			ds = new DatagramSocket(5023);			
			
			//OUT 1: 0825
			seq++;
			
			baos = new ByteArrayOutputStream();
			baos.write(new byte[]{
					0x02, 0x34, 0x4B, 0x08, 0x25
			});
			baos.write(Converts.hexStringToByte(Integer.toHexString(seq).toUpperCase()));
			baos.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
			baos.write(new byte[]{
					0x03, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x00, 0x00, 0x00
			});
			baos.write(rndkey);
			System.out.println(Converts.bytesToHexString(baos.toByteArray()));
			//以下需要加密
			bsofplain = new ByteArrayOutputStream();
			bsofplain.write(new byte[]{
					0x00, 0x18, 0x00, 0x16, 0x00, 0x01,
					0x00, 0x00, 0x04, 0x36, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x14, (byte)0x9B
			});
			bsofplain.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
			bsofplain.write(new byte[]{
					0x00, 0x00, 0x00, 0x00, 0x03, 0x09, 0x00, 0x08, 0x00, 0x01
			});
			bsofplain.write(ips);
			bsofplain.write(new byte[]{
					0x00, 0x02, 0x00, 0x36, 0x00, 0x12, 0x00, 0x02, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
					0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x14, 0x00, 0x1D, 0x01, 0x02 
			});
			bsofplain.write(new byte[]{
					0x00, 0x19 
			});
			bsofplain.write(ecdhkey);
			System.out.println(Converts.bytesToHexString(bsofplain.toByteArray()));
			encrypt = crypter.encrypt(bsofplain.toByteArray(), rndkey);
			//加密完成
			baos.write(encrypt);
			baos.write(new byte[]{
					0x03
			});
			buf = baos.toByteArray();
			System.out.println(Converts.bytesToHexString(baos.toByteArray()));
						
			dpOut = new DatagramPacket(buf, buf.length, InetAddress.getByName("183.60.19.100"), 8000);
			ds.send(dpOut);
			
			//IN 1:
			dpIn = new DatagramPacket(buffer, buffer.length);
			ds.receive(dpIn);
			
			String data = Converts.bytesToHexString(buffer);
			System.out.println(data);
			
			//------------------------------------------------------------------------------
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			ds.close();
		}
	}
	
	public static byte[] genKey(int length){
		byte[] rs = new byte[length];
		Random rnd = new Random();
		rnd.nextBytes(rs);
		return rs;
	}
}
