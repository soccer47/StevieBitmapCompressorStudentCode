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
    public static final int BITS_PER_RUN_LENGTH = 8;

    /**
     * Reads a sequence of bits from standard input, compresses them,
     * and writes the results to standard output.
     */
    public static void compress() {
        // Queue to hold binary values
        Queue<Boolean> sequence = new LinkedList<>();

        // Read in the binary values one at a time into a Queue
        while (!BinaryStdIn.isEmpty()) {
            sequence.add(BinaryStdIn.readBoolean());
        }

        // Boolean to hold the type of bit of the current streak
        // Start currentBitType at 0/false
        boolean currentBitType = false;

        // Boolean to hold the current bit being compared to currentBitType
        boolean currentBit;
        // Length of the current run of bits of the same type
        int currentRun = 0;

        // Go through the sequence of bits
        while (!sequence.isEmpty()) {
            // Get the next bit in the sequence
            currentBit = sequence.remove();
            // If the current bit is of the same type as the current run, increment the run length by one
            // Otherwise, write out a sequence of bits representing the run
            if (currentBit != currentBitType){
                // Next write the length of the run
                BinaryStdOut.write(currentRun, BITS_PER_RUN_LENGTH);
                // Update the type of the current bit
                currentBitType = currentBit;
                // Reset the length of the current run
                currentRun = 0;
            }
            // Increment currentRun by 1
            currentRun++;
            // If the current run is longer than 255, write out the code for the first 255 bits
            if (currentRun == 256) {
                BinaryStdOut.write(255, BITS_PER_RUN_LENGTH);
                // Write out the code for 8 bits of the other type
                BinaryStdOut.write(0, BITS_PER_RUN_LENGTH);
                // Reset the length of currentRun back to 1
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
        // Boolean to hold the type of bit of the current run
        // The type of bit of the first run is 0
        boolean bit = false;

        // Write out the bits until all the bits have been taken in from the compressed file
        while (!BinaryStdIn.isEmpty()) {
            // Get the length of the current run
            int runLength = BinaryStdIn.readInt(BITS_PER_RUN_LENGTH);

            // Write out the given bits [the length of the run] times
            for (int j = 0; j < runLength; j++) {
                BinaryStdOut.write(bit);
            }
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