package assign1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * Assignment 1
 * Submitted by: 
 * Alex Breger ID 205580087
 * Dani Kogel ID 318503257
 */

import base.Compressor;

public class HuffmanEncoderDecoder implements Compressor {

	public HuffmanEncoderDecoder() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void Compress(String[] input_names, String[] output_names) {
		// Input and Output variables.
		FileInputStream input = null;
		BitOutputStream outputBit = null;

		// We make a hash map of short and integer to pair
		// occurrences of shorts in the file.
		HashMap<Short, Integer> occurrencesMap = new HashMap<>();
		// We make a priority queue to make it work like a minimum heap
		// we will use it to make a huffman tree as it lets us to connect
		// 2 minimal nodes at a time
		PriorityQueue<NodeTree> minHeap = new PriorityQueue<>();
		// We make a hashmap to store the short and its code.
		// This will serve as a dictionary.
		HashMap<Short, String> dictionaryMap = new HashMap<>();
		// We make a var to count the number of bytes.
		int byteCounter = 0;
		// We make a var to count the leaves to know how much
		int leafCounter = 0;
		// Firstly we count the occurrences of every 2 bytes (short)
		// and make a table in the form of a hash map
		// where a 'key' is the 'short' and the 'value' is the amount

		// To generate the hash map we need to read the file for the first
		// time, byte-by-byte, and concatenate those bytes.
		// we will count the number of bytes we read.
		try {
			input = new FileInputStream(input_names[0]);
			while (true) {
				short firstByte = (short) input.read();
				if (firstByte == -1)
					break;
				byteCounter++;
				short secondByte = (short) input.read();
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
		// Gets the number of the leaves by getting the size of the frequencies map.
		leafCounter = occurrencesMap.size();

		// Next we will use the priority queue to build the tree.
		makeMinHeap(occurrencesMap, minHeap);
		// Now that we have the queue, we will make the tree.
		NodeTree root = makeTree(minHeap);
		// Initiation of the bit reader.
		try {
			outputBit = new BitOutputStream(new FileOutputStream(output_names[0]));
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		// After making the tree, we need to map the encoding.
		// So we need a new hash map which will hold the pairs
		// Short:Integer
		// We will traverse the tree from root to each leaf and map
		// With '0' for left and '1' for right

		// Here we start the compressed file with information needed for the
		// Decompression. number of total bytes in the original file and the tree
		// structure This will be the header of the compressed file.
		// Every compressed file needs its own tree to decompress.
		outputBit.writeBits(32, byteCounter);
		outputBit.writeBits(32, leafCounter);
		treeIntoDict(dictionaryMap, root, "", outputBit);

		// Now we will read the file again and compress it based on the dictionary
		try {
			input.close();
			input = new FileInputStream(input_names[0]);
			while (true) {
				short firstByte = (short) input.read();
				if (firstByte == -1)
					break;
				short secondByte = (short) input.read();
				if (secondByte == -1) {
					secondByte = 0;
					short combined = (short) ((firstByte << 8) | secondByte);
					if (dictionaryMap.containsKey(combined))
						writeIntoCompressed(dictionaryMap.get(combined), outputBit);
				} else {
					short combined = (short) ((firstByte << 8) | secondByte);
					if (dictionaryMap.containsKey(combined))
						writeIntoCompressed(dictionaryMap.get(combined), outputBit);
				}
			}
			outputBit.flush();
			input.close();
			outputBit.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Compression Is Done");

	}

	// This function writes the bits into the file by going on the
	// Relevant string to the 'short' key and sending its bit path
	// One by one.
	private void writeIntoCompressed(String codeBits, BitOutputStream outputBit) {
		for (int i = 0; i < codeBits.length(); i++) {
			outputBit.writeBits(1, Integer.parseInt("" + codeBits.charAt(i)));
		}
	}

	// This function maps the dictionary with Short:String path.
	// Tried to do as a byte[] but failed. Switched to non array.
	private void treeIntoDict(HashMap<Short, String> dictionaryMap, NodeTree root, String path,
			BitOutputStream outputBit) {
		if (root.left == null && root.right == null) {
			dictionaryMap.put(root.character, path);
			outputBit.writeBits(1, 1);
			outputBit.writeBits(16, root.character);
			return;
		}
		treeIntoDict(dictionaryMap, root.left, path + "0", outputBit);
		treeIntoDict(dictionaryMap, root.right, path + "1", outputBit);
		outputBit.writeBits(1, 0);
	}

	// This function makes the tree until it reaches the root node. It works.
	private NodeTree makeTree(PriorityQueue<NodeTree> minHeap) {
		while (minHeap.size() > 1) {
			NodeTree right = minHeap.poll();
			NodeTree left = minHeap.poll();
			NodeTree combined = new NodeTree(left, right, (short) 0, left.freq + right.freq);
			minHeap.add(combined);
		}
		return minHeap.poll();
	}

	// This function fills the queue. It works.
	private void makeMinHeap(HashMap<Short, Integer> occurrencesMap, PriorityQueue<NodeTree> minHeap) {
		for (Map.Entry<Short, Integer> value : occurrencesMap.entrySet()) {
			NodeTree node = new NodeTree(null, null, value.getKey(), value.getValue());
			minHeap.add(node);
		}
	}

	@Override
	public void Decompress(String[] input_names, String[] output_names) {
		// As we start to decompress, we will first read the header.

		// Input and Output variables.
		BitInputStream inputBit = null;
		BitOutputStream outputBit = null;

		try {
			inputBit = new BitInputStream(new FileInputStream(input_names[0]));
			outputBit = new BitOutputStream(new FileOutputStream(output_names[1]));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		// Those are read from the file header. Needed for knowing how much is left for
		// specific loops.
		int byteCounter = 0;
		int leafCounter = 0;
		try {
			byteCounter = inputBit.readBits(32);
			leafCounter = inputBit.readBits(32);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// We need a stack to make the Huffman tree.
		Stack<NodeTree> stackTree = new Stack<>();
		// With the stack we will build the tree from the header.
		NodeTree root = buildHuffTree(stackTree, leafCounter, inputBit);
		// Now we need a dictionary Encoded:Value so when we meet an encoding
		// That corresponds to a specific `short` we will wrtire the short
		// Into the encoded file.
		HashMap<String, Short> decodeDict = new HashMap<>();
		// We turn the tree into a dictionary just like we did
		// In the encoder.
		treeIntoDictHuff(decodeDict, root, "");
		// Now what is left is to read the file bit-by-bit.
		// Every bit we concatenate into a string and check if the string is in the
		// dictionary.
		// If it is, we write the `Short` value into the decompressed file.
		decodeTree(decodeDict, outputBit, inputBit, byteCounter);
		//Here we are done with the decompression. Task is Complete.
		System.out.println("Decompression Is Done");

	}

	// We read bit by bit and add the bits to a string.
	// If the string matches the key in the dictionary,
	private void decodeTree(HashMap<String, Short> decodeDict, BitOutputStream outputBit, BitInputStream inputBit,
			int byteCounter) {
		String bit = "";
		int bitCheck = 0;
		try {
			bitCheck = inputBit.readBits(1);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (byteCounter > 0) {// EOF
			bit += bitCheck;
			try {
				if (decodeDict.containsKey(bit)) {
					outputBit.writeBits(16, decodeDict.get(bit));
					byteCounter -= 2;
					bit = "";
					bitCheck = inputBit.readBits(1);
				} else {
					bitCheck = inputBit.readBits(1);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// We turn the tree into a dictionary. same as we did in encoding.
	private void treeIntoDictHuff(HashMap<String, Short> decodeDict, NodeTree root, String path) {
		if (root.left == null && root.right == null) {
			decodeDict.put(path, root.character);
			return;
		}
		treeIntoDictHuff(decodeDict, root.left, path + "0");
		treeIntoDictHuff(decodeDict, root.right, path + "1");
	}

	// We build a tree from the header.
	private NodeTree buildHuffTree(Stack<NodeTree> stackTree, int leafCounter, BitInputStream inputBit) {
		int inputBitHolder = 0;
		// Because huffman tree is a complete binary tree
		int nonLeafCounter = leafCounter - 1;
		try {
			while (leafCounter > 0 || nonLeafCounter > 0) {
				inputBitHolder = inputBit.readBits(1);

				if (inputBitHolder == 1) {
					NodeTree leaf = new NodeTree(null, null, (short) inputBit.readBits(16), 0);
					stackTree.push(leaf);
					leafCounter--;
				} else if (inputBitHolder == 0) {
					NodeTree right = stackTree.pop();
					NodeTree left = stackTree.pop();
					NodeTree combined = new NodeTree(left, right, (short) 0, 0);
					stackTree.push(combined);
					nonLeafCounter--;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stackTree.pop();
	}

	@Override
	public byte[] CompressWithArray(String[] input_names, String[] output_names) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] DecompressWithArray(String[] input_names, String[] output_names) {
		// TODO Auto-generated method stub
		return null;
	}

}
