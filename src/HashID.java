// IN2011 Computer Networks
// Coursework 2023/2024
//
// Construct the hashID for a string

import java.lang.StringBuilder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashID {

    public static byte [] computeHashID(String line) throws Exception {
	if (line.endsWith("\n")) {
	    // What this does and how it works is covered in a later lecture
	    MessageDigest md = MessageDigest.getInstance("SHA-256");
	    md.update(line.getBytes(StandardCharsets.UTF_8));
	    return md.digest();

	} else {
	    // 2D#4 computes hashIDs of lines, i.e. strings ending with '\n'
	    throw new Exception("No new line at the end of input to HashID");
	}
    }

	public int calculateDistance(byte[] hashID1, byte[] hashID2) {
		int distance = 0;
		for (int i = 0; i < hashID1.length; i++) {
			int xorResult = hashID1[i] ^ hashID2[i];
			for (int j = 7; j >= 0; j--) {
				if (((xorResult >> j) & 1) == 1) {
					return distance;
				}
				distance++;
			}
		}
		return distance;
	}

	public String convertToHex(byte[] hashID) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : hashID) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

}
