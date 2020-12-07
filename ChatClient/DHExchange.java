package ChatClient;
import java.math.BigInteger;
import java.util.Random;

public class DHExchange {

    private int privateKey;
    private BigInteger publicKey;
    private BigInteger privateShareKey; // An 2048-bit shared secret key
    private BigInteger p;
    private BigInteger g;
    private String aesKey;

    public DHExchange() {
        // generate public and private keys
        p = DHParameter.getDHPrime();
        g = DHParameter.getGenerator();
        this.privateKey = new Random().nextInt(3000);
        generatePublicKey();

    }

    public void generatePublicKey() {
        publicKey =  g.pow(privateKey).mod(p);
    }


    public void setPrivateShareKey(BigInteger peerPublicKey) {
        privateShareKey= peerPublicKey.pow(privateKey).mod(p);
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }


    public void setAESKey() {
        byte[] Hash = SHA256.hash(privateShareKey.toString());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int n = (Math.abs(Hash[i]) % 128);
            if (n < 33) n+=33;
            sb.append((char) n);
        }
        aesKey = sb.toString();
    }

    public String getAesKey() {
        return aesKey;
    }
}
