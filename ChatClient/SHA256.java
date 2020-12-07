/*
Reference Table
    * General Instruction of SHA256: https://qvault.io/2020/07/08/how-sha-2-works-step-by-step-sha-256/
    * Convert String into binary array: https://mkyong.com/java/java-convert-string-to-binary/
 */
package ChatClient;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SHA256 {
    private static final int BLOCK_BYTES = 64;
    private static final int BYTE_TO_BIT = 8;


    private static final int[] K = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    private static final int[] HASH_VALUES = {
            0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
    };


    // Calculate expected length of padding message
    private static int paddingLength (int currentLength) {
         return 64 * (currentLength / BLOCK_BYTES + 1);
    }


    private static byte[] padding(byte[] msg) {
        // 1. Calculate expected length of padding result
        int finalBufferLength = paddingLength(msg.length);
        byte[] result = Arrays.copyOf(msg, finalBufferLength);

        // 2. Append a single 1
        result[msg.length] = (byte) 0x80;

        // 3. Pad remain with 0
        for (int i = msg.length+1; i < finalBufferLength; i++) {
            result[i] = (byte) 0x00;
        }

        // 4. The last 8 bytes need to be the length of msg
        long msgLength = msg.length * 8L;
        byte[] bytes = new byte[64 / BYTE_TO_BIT];
        ByteBuffer.wrap(bytes).putLong(msgLength);
        byte[] lengthByteArray = Arrays.copyOfRange(bytes, 0, 8);
        System.arraycopy(lengthByteArray, 0, result, finalBufferLength-8, 8);
        return result;
    }



    public static byte[] hash(String input) {
        int[] H = HASH_VALUES.clone();

        byte[] msg = input.getBytes();
        // 1. Pre-Processing (Padding)
        byte[] paddingMsgByte = padding(msg);
        int [] paddingMsg = toIntArray(paddingMsgByte);

        // 2. Process the message in successive 512-bit (16-word) chunks
        for (int i = 0; i < paddingMsg.length/16; i++) {
            // Create a 64-entry message schedule array w[0..63] of 32-bit words
            int[] w = new int[64];
            // Copy chunk into first 16 words w[0..15] of the message schedule array
            System.arraycopy(paddingMsg, i * 16, w, 0, 16);
            // Extend the first 16 words into the remaining 48 words w[16..63] of the message schedule array:
            // for i from 16 to 63
            for (int j = 16; j < 64; j++) {
                // w[j] := w[j-16] + s0 + w[j-7] + s1
                int s0 = smallSig0(w[j - 15]);
                int s1 = smallSig1(w[j - 2]);
                w[j] = w[j - 16] + s0 + w[j - 7] + s1;
            }

            //  Compression function main loop
            int[] TEMP = new int[8];
            System.arraycopy(H, 0, TEMP, 0, 8);


            for (int j = 0; j < 64; j++) {
                int S0 = bigSig0(TEMP[0]);
                int S1 = bigSig1(TEMP[4]);
                int ch = calcCh(TEMP[4], TEMP[5], TEMP[6]);
                int maj = calcMaj(TEMP[0], TEMP[1], TEMP[2]);
                int temp1 = TEMP[7] + S1 + ch + K[j] + w[j];
                int temp2 = S0 + maj;

                // Update temp hash values
                TEMP[7] = TEMP[6];
                TEMP[6] = TEMP[5];
                TEMP[5] = TEMP[4];
                TEMP[4] = TEMP[3] + temp1;
                TEMP[3] = TEMP[2];
                TEMP[2] = TEMP[1];
                TEMP[1] = TEMP[0];
                TEMP[0] = temp1 + temp2;
            }



            // Add the compressed chunk to the current hash value:
            for (int j = 0; j < 8; j++) {
                H[j] += TEMP[j];
            }
        }
        return toByteArray(H);
    }

    private static int[] toIntArray(byte[] byteArr) {
        int[] intArr = new int[byteArr.length/4];
        for (int i = 0; i < byteArr.length/4; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 4*i; j < 4*i+4; j++) {
                String s1 = String.format("%8s", Integer.toBinaryString(byteArr[j] & 0xFF))
                        .replace(' ', '0');
                stringBuilder.append(s1);
            }
            try{
                int x = Integer.valueOf(stringBuilder.toString(), 2);
                intArr[i] = x;
            } catch (NumberFormatException e) {
                intArr[i] = 1;
            }

        }
        return intArr;
    }

    private static byte[] toByteArray(int[] intArr) {
        // https://stackoverflow.com/a/1086092/12358813
        ByteBuffer byteBuffer = ByteBuffer.allocate(intArr.length * 4);
        for (int x: intArr) {
            byteBuffer.putInt(x);
        }
        return byteBuffer.array();
    }





    private static int smallSig0(int x) {
        return Integer.rotateRight(x, 7) ^ Integer.rotateRight(x, 18) ^ (x >>> 3);
    }

    private static int smallSig1(int x) {
        return Integer.rotateRight(x, 17) ^ Integer.rotateRight(x, 19) ^ (x >>> 10);
    }

    private static int bigSig0(int x) {
        return Integer.rotateRight(x, 2) ^ Integer.rotateRight(x, 13) ^ Integer.rotateRight(x, 22);
    }

    private static int bigSig1(int x) {
        return Integer.rotateRight(x, 6) ^ Integer.rotateRight(x, 11) ^ Integer.rotateRight(x, 25);
    }

    private static int calcCh(int e, int f, int g) {
        return (e & f) ^ (~e & g);
    }

    private static int calcMaj(int a, int b, int c) {
        return (a & b) ^ (a & c) ^ (b & c);
    }
}
