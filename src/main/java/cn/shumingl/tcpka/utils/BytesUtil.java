package cn.shumingl.tcpka.utils;

public class BytesUtil {

    public static void fill(byte[] srcBytes, byte[] dstBytes, int offset, int length) {
        System.arraycopy(srcBytes, 0, dstBytes, offset, length);
    }

    public static void fill(byte[] srcBytes, byte[] dstBytes, int offset) {
        System.arraycopy(srcBytes, 0, dstBytes, offset, srcBytes.length);
    }

    public static byte[] merge(byte[]... byteArrays) {
        int length = 0;
        int offset = 0;
        for (byte[] byteArray : byteArrays)
            length += byteArray.length;
        byte[] buffer = new byte[length];
        for (byte[] byteArray : byteArrays) {
            System.arraycopy(byteArray, 0, buffer, offset, byteArray.length);
            offset += byteArray.length;
        }
        return buffer;
    }
}
