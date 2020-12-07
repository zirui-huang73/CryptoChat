package ChatClient;
import java.util.Arrays;

public class AESEncoder {

    public static String encode(String msg, String aesKey) {

        char[] p = Utils.makeCharArray(msg);
        char[] key = aesKey.toCharArray();
        int[] w = new int[44];

        if (!Utils.checkKeyLength(key.length)) {
            System.out.printf("Incorrect Key Length. Length should be 16. Current length is %d\n", key.length);
            return null;
        }

        Utils.extendKey(key, w);
        int[][] pArray;
        StringBuilder sb = new StringBuilder();


        for (int k = 0; k < p.length/16; k += 1) {
            char[] chunk = Arrays.copyOfRange(p, k*16, (k+1)*16);
            pArray = Utils.convertToIntArray(chunk);

            Utils.addRoundKey(pArray, 0, w);

            for (int i = 1; i < 10; i++) {

                Utils.subBytes(pArray);

                Utils.shiftRows(pArray);

                Utils.mixColumns(pArray);

                Utils.addRoundKey(pArray, i, w);

            }

            // 10th round
            Utils.subBytes(pArray);

            Utils.shiftRows(pArray);

            Utils.addRoundKey(pArray, 10, w);

            char[] temp = Utils.convertArrayToStr(pArray);
            sb.append(temp);
        }
        return sb.toString();
    }
}
