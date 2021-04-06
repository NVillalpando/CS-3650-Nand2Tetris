
public class Code {
    // dest is based on the assumption that if there is no equals sign within the program, then the value will be set as 000 and this method won't be called.
    public static String dest(String mnemonic) {
        String[] bits = new String[]{"001", "010", "011", "100", "101", "110", "111"};
        String[] mnemonics = new String[]{"M", "D", "MD", "A", "AM", "AD", "AMD"};

        // Searches for stored bit values to return
        for (int i = 0; i < 7; i++) {
            if (mnemonic.equals(mnemonics[i])) {
                return bits[i];
            }
        }
        // did this for intellij but this should never happen.
        return "";
    }

    // Check for comp bits to return. takes in a mnemonic to find.
    public static String comp(String mnemonic) {
        String[] bits = new String[]{"0101010", "0111111", "0111010", "0001100", "0110000", "0001101", "0110001", "0001111", "0110011", "0011111", "0110111", "0001110", "0110010", "0000010", "0010011", "0000111", "0000000", "0010101"};
        String[] mnemonics = new String[]{"0", "1", "-1", "D", "A", "!D", "!A", "-D", "-A", "D+1", "A+1", "D-1", "A-1", "D+A", "D-A", "A-d", "D&A", "D|A"};

        String[] aBits = new String[]{"1110000", "1110001", "1110011", "1110111", "1110010", "1000010", "1010011", "1000111", "1000000", "1010101"};
        String[] aMnemonics = new String[]{"M", "!M", "-M", "M+1", "M-1", "D+M", "D-M", "M-D", "D&M", "D|M"};

        // First check for comp bits with a=0
        for (int i = 0; i < 18; i++) {
            if (mnemonic.equals(mnemonics[i])) {
                return bits[i];
            }
        }

        // Last check for comp bits with a=1
        for (int i = 0; i < 18; i++) {
            if (mnemonic.equals(aMnemonics[i])) {
                return aBits[i];
            }
        }

        // Once again, putting a return here for java but this is under the assumption that code given is error free.
        return "";

    }

    // jump also plays on the assumption that if there is no semicolon in the code, then there is no jump and will be set as 000 not calling this method.
    public static String jump(String mnemonic) {
        String[] bits = new String[]{"001", "010", "011", "100", "101", "110", "111"};
        String[] mnemonics = new String[]{"JGT", "JEQ", "JGE", "JLT", "JNE", "JLE", "JMP"};

        // check for mnemonic bits
        for (int i = 0; i < 8; i++) {
            if (mnemonic.equals(mnemonics[i])) {
                return bits[i];
            }
        }

        // java fix
        return "";
    }
}
