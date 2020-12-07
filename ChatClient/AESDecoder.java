package ChatClient;
import java.util.Arrays;

public class AESDecoder {
    public static String decode(String msg, String aesKey) {
        char[] key = aesKey.toCharArray();
        char[] c = Utils.makeCharArray(msg);
        int[] w = new int[44];
        if(!Utils.checkKeyLength(key.length)) {
            System.out.printf("Incorrect Key Length. Length should be 16. Current length is %d\n", key.length);
            return null;
        }

        Utils.extendKey(key, w);
        int[][] cArray;
        StringBuilder sb = new StringBuilder();
        for(int k = 0; k < c.length/16; k += 1) {
            char[] chunk = Arrays.copyOfRange(c, k*16, (k+1)*16);
            cArray = Utils.convertToIntArray(chunk);
            Utils.addRoundKey(cArray, 10, w);

            int[][] wArray;
            for(int i = 9; i >= 1; i--) {
                Utils.deSubBytes(cArray);
                Utils.deShiftRows(cArray);
                Utils.deMixColumns(cArray);
                wArray = Utils.getArrayFrom4W(i, w);
                Utils.deMixColumns(wArray);
                Utils.addRoundTowArray(cArray, wArray);
            }

            Utils.deSubBytes(cArray);

            Utils.deShiftRows(cArray);

            Utils.addRoundKey(cArray, 0, w);

            char[] temp = Utils.convertArrayToStr(cArray);
            sb.append(temp);

        }
        return sb.toString();

    }
}
