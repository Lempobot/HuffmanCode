package assign1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class Runner {
	static String[] IN_FILE_PATH = new String[] {"C:\\Users\\Alex\\Downloads\\ExampleInputs\\Red_Flowers.bmp", "C:\\Users\\Alex\\Downloads\\ExampleInputs\\Red_FlowersDecompressed.bmp"};
	static String[] OUT_FILE_PATH =new String[] {"C:\\Users\\Alex\\Downloads\\ExampleInputs\\Red_FlowersCompressed.bmp"};
	public static void main(String[] args) {
		HuffmanEncoderDecoder hed = new HuffmanEncoderDecoder();
		hed.Compress(IN_FILE_PATH, OUT_FILE_PATH);
		hed.Decompress(OUT_FILE_PATH, IN_FILE_PATH);
	}
}
//This function maps the dictionary with Short:Code. it works, maybe weird.
	/*private void treeIntoDict(HashMap<Short, String> dictionaryMap, NodeTree root, String path) {
		if(root == null) {
			System.out.println("there is no tree");
			return;
		}
		if (root.left == null && root.right == null) {
			dictionaryMap.put(root.character, path);
			return;
		}
		String nodeBit = path;
		treeIntoDict(dictionaryMap, root.left, nodeBit + "0");
		treeIntoDict(dictionaryMap, root.right, nodeBit + "1");
	}*/
/*treeIntoDict(dictionaryMap, root,0,0,outputBit);

private void treeIntoDict(HashMap<Short, Integer[]> dictionaryMap, NodeTree root, int numberOfBits, int path, BitOutputStream outputBit) {
	When we reach the leaf, we want to save the path of bits
	 and the number of bits for later reading.
	if (root.left == null && root.right == null) {
		Integer[] pairCountPath = {numberOfBits, path};
		dictionaryMap.put(root.character, pairCountPath);
		outputBit.writeBits(1, 1);
		outputBit.writeBits(numberOfBits, path >> 8);
		return;
	}
	outputBit.writeBits(1, 0);
	treeIntoDict(dictionaryMap, root.left, numberOfBits + 1, path << 1, outputBit);
	treeIntoDict(dictionaryMap, root.right, numberOfBits + 1,(path << 1) | 1, outputBit);
	
	
	
	try {
			input = new FileInputStream(input_names[0]);
			while (true) {
				byte firstByte = (byte) input.read();
				if (firstByte == -1)
					break;
				byteCounter++;
				byte secondByte = (byte) input.read();
				if (secondByte == -1) {
					secondByte = 0;
					short combined = (short) ((firstByte << 8) | secondByte);
					occurrencesMap.put(combined, occurrencesMap.getOrDefault(combined, 0) + 1);
				} else {
					byteCounter++;
					short combined = (short) ((firstByte << 8) | secondByte);
					occurrencesMap.put(combined, occurrencesMap.getOrDefault(combined, 0) + 1);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		------------------
		try {
			input = new FileInputStream(input_names[0]);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		while (true) {
			// try reading first two bytes
			int firstByte = 0;
			try {
				firstByte = input.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (firstByte == -1)
				break;
			byteCounter++;
			int secondByte = 0;
			try {
				secondByte = input.read();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// take two bytes of data and merge them into a short(2-byte-data type)
			short twoBytes = (short) firstByte;
			twoBytes <<= 8;
			if (secondByte != -1) {
				byteCounter++;
				twoBytes = (short) (twoBytes | secondByte);
			}
			// twoBytes should now hold 2 bytes of data

			// if the number of signs in the file is odd, last byte should be 0xff
			// insert the two-byte data into a HashMap in order to determine their frequency
			occurrencesMap.put(twoBytes, occurrencesMap.getOrDefault(twoBytes, 0) + 1);
			if (secondByte == -1)
				break;
		}
}*/