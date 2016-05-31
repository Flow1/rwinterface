/**
 * @author Theo den Exter, ARS
 * Date: May 21th 2016
 * Version 1.0
 *
 * Binary class for RW-interface
 *
 * History
 *
 */
package eu.srk.org;

import java.nio.ByteBuffer;

public class Binary {

	public Binary() {

	}

	public void testConversion() {

		for (int i = 0; i < 300; i++) {
			byte b = intToByte(i);
			int k = byteToInt(b);
			System.out.println(i + " " + (b & (0xff)) + " " + k);
		}
	}

	public byte intToByte(int i) {
		byte b = (byte) (i & (0xff));
		return b;
	}

	public int byteToInt(byte b) {
		int i = b & (0xff);
		return i;
	}

	public byte[] toByteArray1(int value) {
		return ByteBuffer.allocate(4).putInt(value).array();
	}

	public byte[] toByteArray2(int value) {
		return new byte[] { (byte) (value >> 24), (byte) (value >> 16),
				(byte) (value >> 8), (byte) value };
	}

	public int fromByteArray1(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getInt();
	}

	public byte[] toByteArray1Long(long value) {
		return ByteBuffer.allocate(4).putInt((int) value).array();
	}

	public long fromByteArray1Long(byte[] bytes) {
		int i = fromByteArray1(bytes);
		long unsignedValue = i & 0xffffffffl;
		return unsignedValue;
	}

	// packing an array of 4 bytes to an int, big endian
	public int fromByteArray2(byte[] bytes) {
		return bytes[0] << 24 | (bytes[1] & 0xFF) << 16
				| (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}

	// Process Incoming Position Report
	public void test() {

	}

}