/******************************************************************************
 *  Compilation:  javac BitmapCompressor.java
 *  Execution:    java BitmapCompressor - < input.bin   (compress)
 *  Execution:    java BitmapCompressor + < input.bin   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   q32x48.bin
 *                q64x96.bin
 *                mystery.bin
 *
 *  Compress or expand binary input from standard input.
 *
 *  % java DumpBinary 0 < mystery.bin
 *  8000 bits
 *
 *  % java BitmapCompressor - < mystery.bin | java DumpBinary 0
 *  1240 bits
 ******************************************************************************/

import java.util.LinkedList;
import java.util.Queue;

/**
 *  The {@code BitmapCompressor} class provides static methods for compressing
 *  and expanding a binary bitmap input.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Zach Blick
 *  @author Stevie K. Halprin
 */
public class BitmapCompressor {

    // Number of bits to represent the length of a run of bits of same type
    public static final int BITS_PER_RUN_LENGTH = 11;
    // Number of bits to represent the length of the binary file
    public static final int BIT_HEADER_LENGTH = 13;

    /**
     * Reads a sequence of bits from standard input, compresses them,
     * and writes the results to standard output.
     */
    public static void compress() {
        // Queue to hold binary values
        Queue<Boolean> sequence = new LinkedList<>();
        // Integer to hold length of sequence
        int seqLength = 0;
        // Read in the binary values one at a time into a Queue
        while (!BinaryStdIn.isEmpty()) {
            sequence.add(BinaryStdIn.readBoolean());
            // Increment seqLength by 1
            seqLength++;
        }

        // Write the length of the sequence with the first BITS_PER_LENGTH bits in the binary file
        BinaryStdOut.write(seqLength, BIT_HEADER_LENGTH);

        // Boolean to hold the type of bit of the current streak
        boolean currentBitType = sequence.remove();
        // Write out this bit type as the first type of bit in the sequence
        BinaryStdOut.write(currentBitType);

        // Boolean to hold the current bit being compared to currentBitType
        boolean currentBit;
        // Length of the current run of bits of the same type
        int currentRun = 1;

        // Go through the sequence of bits
        while (!sequence.isEmpty()) {
            // Get the next bit in the sequence
            currentBit = sequence.remove();
            // If the current bit is of the same type as the current run, increment the run length by one
            if (currentBit == currentBitType) {
                currentRun++;
            }
            // Otherwise, write out a sequence of bits representing the run
            else {
                // Next write the length of the run
                BinaryStdOut.write(currentRun, BITS_PER_RUN_LENGTH);
                // Update the type of the current bit
                currentBitType = currentBit;
                // Reset the length of the current run back to 1
                currentRun = 1;
            }
        }
        // Write out the last run
        BinaryStdOut.write(currentRun, BITS_PER_RUN_LENGTH);
        // Close the file
        BinaryStdOut.close();
    }

    /**
     * Reads a sequence of bits from standard input, decodes it,
     * and writes the results to standard output.
     */
    public static void expand() {
        // Find how many characters are in the sequence
        int sequenceLength = BinaryStdIn.readInt(BIT_HEADER_LENGTH);
        // Find out the first type of bit in the sequence
        boolean bit = BinaryStdIn.readBoolean();
        // Integer to keep track of how many bits have been written out
        int bitsWritten = 0;

        // Write out the bits until the same number of bits as were in the original file have been written
        while (bitsWritten < sequenceLength) {
            // Get the length of the current run
            int runLength = BinaryStdIn.readInt(BITS_PER_RUN_LENGTH);

            // Write out the given bits [the length of the run] times
            for (int j = 0; j < runLength; j++) {
                BinaryStdOut.write(bit);
            }
            // Increment the number of bits written by the length of the run
            bitsWritten += runLength;
            // Switch the type of bit
            bit = !bit;
        }
        // Close the file
        BinaryStdOut.close();
    }

    /**
     * When executed at the command-line, run {@code compress()} if the command-line
     * argument is "-" and {@code expand()} if it is "+".
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}