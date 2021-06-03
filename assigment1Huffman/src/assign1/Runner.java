package assign1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class Runner {
	static String[] IN_FILE_PATH = new String[] {"path toofriginal file", "path to decompressed file.bmp"};
	static String[] OUT_FILE_PATH =new String[] {"path to compressed file"};
	public static void main(String[] args) {
		HuffmanEncoderDecoder hed = new HuffmanEncoderDecoder();
		hed.Compress(IN_FILE_PATH, OUT_FILE_PATH);
		hed.Decompress(OUT_FILE_PATH, IN_FILE_PATH);
	}
}
