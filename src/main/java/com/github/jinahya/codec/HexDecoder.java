/*
 * Copyright 2012 Jin Kwon <jinahya at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.github.jinahya.codec;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Jin Kwon <jinahya at gmail.com>
 */
public class HexDecoder {


    private static final Logger LOGGER
        = LoggerFactory.getLogger(HexDecoder.class);


    /**
     * Decodes a single nibble.
     *
     * @param input the nibble to decode.
     *
     * @return the decoded half octet.
     */
    private static int decodeHalf(final int input) {

        switch (input) {
            case 0x30: // '0'
            case 0x31: // '1'
            case 0x32: // '2'
            case 0x33: // '3'
            case 0x34: // '4'
            case 0x35: // '5'
            case 0x36: // '6'
            case 0x37: // '7'
            case 0x38: // '8'
            case 0x39: // '9'
                return input - 0x30;
            case 0x41: // 'A'
            case 0x42: // 'B'
            case 0x43: // 'C'
            case 0x44: // 'D'
            case 0x45: // 'E'
            case 0x46: // 'F'
                return input - 0x37;
            case 0x61: // 'a'
            case 0x62: // 'b'
            case 0x63: // 'c'
            case 0x64: // 'd'
            case 0x65: // 'e'
            case 0x66: // 'f'
                return input - 0x57;
            default:
                throw new IllegalArgumentException("illegal input: " + input);
        }
    }


    /**
     * Decodes two nibbles in given input array and returns the decoded octet.
     *
     * @param input the input array of nibbles.
     * @param inoff the offset in the array.
     *
     * @return the decoded octet.
     */
    public static int decodeSingle(final byte[] input, final int inoff) {

        if (input == null) {
            throw new NullPointerException("input == null");
        }

        if (input.length < 2) {
            // not required by (inoff >= input.length -1) checked below
            throw new IllegalArgumentException(
                "input.length(" + input.length + ") < 2");
        }

        if (inoff < 0) {
            throw new IllegalArgumentException("inoff(" + inoff + ") < 0");
        }

        if (inoff >= input.length - 1) {
            throw new IllegalArgumentException(
                "inoff(" + inoff + ") >= input.length(" + input.length
                + ") - 1");
        }

        return (decodeHalf(input[inoff] & 0xFF) << 4)
               | decodeHalf(input[inoff + 1] & 0xFF);
    }


    /**
     * Decodes two nibbles in given input array and writes the decoded single
     * octet on specified output array.
     *
     * @param input the input array
     * @param inoff the offset in input array
     * @param output the output array
     * @param outoff the offset in output array
     */
    public static void decodeSingle(final byte[] input, final int inoff,
                                    final byte[] output, final int outoff) {

        if (output == null) {
            throw new NullPointerException("output == null");
        }

        if (outoff < 0) {
            throw new IllegalArgumentException("outoff(" + outoff + ") < 0");
        }

        if (outoff >= output.length) {
            throw new IllegalArgumentException(
                "outoff(" + outoff + ") >= output.length(" + output.length
                + ")");
        }

        output[outoff] = (byte) decodeSingle(input, inoff);
    }


    /**
     * Decodes multiple nibbles in given input array and writes the decoded
     * octets on specified output array.
     *
     * @param input the input array
     * @param inoff the offset in input array
     * @param output the output array
     * @param outoff the offset in output array
     * @param count the number of units to process
     */
    public static void decodeMultiple(final byte[] input, int inoff,
                                      final byte[] output, int outoff,
                                      final int count) {

        if (count < 0) {
            throw new IllegalArgumentException("count(" + count + ") < 0");
        }

        if (inoff + count * 2 > input.length) {
            throw new IllegalArgumentException(
                "inoff(" + inoff + ") + count(" + count + ") * 2 = "
                + (inoff + count * 2) + ">= input.length(" + input.length
                + ")");
        }

        if (outoff + count > output.length) {
            throw new IllegalArgumentException(
                "outoff(" + outoff + ") + count(" + count + ") = "
                + (inoff + count) + ">= output.length(" + output.length + ")");
        }

        for (int i = 0; i < count; i++) {
            decodeSingle(input, inoff, output, outoff);
            inoff += 2;
            outoff += 1;
        }
    }


    /**
     * Encodes given sequence of nibbles into a sequence of octets.
     *
     * @param input the nibbles to decode.
     *
     * @return the decoded octets.
     */
    public static byte[] decodeMultiple(final byte[] input) {

        if (input == null) {
            throw new NullPointerException("input == null");
        }

        final byte[] output = new byte[input.length >> 1]; // / 2

        decodeMultiple(input, 0, output, 0, output.length);

        return output;
    }


    public static long decode(final InputStream input,
                              final OutputStream output, final byte[] inbuf,
                              final long length)
        throws IOException {

        if (input == null) {
            throw new NullPointerException("input");
        }

        if (output == null) {
            throw new NullPointerException("output");
        }

        if (inbuf == null) {
            throw new NullPointerException("buffer");
        }

        if (inbuf.length == 0) {
            throw new IllegalArgumentException(
                "inbuf.length(" + inbuf.length + ") == 0");
        }

        if (length < -1L) {
            throw new IllegalArgumentException("length(" + length + ") < -1L");
        }

        final int o = (inbuf.length & 1) == 1 ? inbuf.length - 1 : inbuf.length;
        final byte[] outbuf = new byte[o >> 1];

        long count = 0L;

        long remained = length - count;
        for (int read; length == -1L || remained > 0L; count += read) {

            read = input.read(inbuf, 0, o);
            if (read == -1) {
                break;
            }
            if (read < o && (read & 1) == 1) { // odd number of byte read
                if ((inbuf[read] = (byte) input.read()) == -1) {
                    throw new IOException("unacceptable end of stream");
                }
                read++;
            }
            assert (read & 1) == 0;
            decodeMultiple(inbuf, 0, outbuf, 0, read);
            output.write(outbuf, 0, read >> 1);
            remained -= read;
        }

        return count;
    }


    public static long decode(final InputStream input,
                              final OutputStream output, final long length)
        throws IOException {

        return decode(input, output, new byte[8192], length);
    }


    public static int decodeMultiple(final ByteBuffer input,
                                     final ByteBuffer output) {

        if (input == null) {
            throw new NullPointerException("input");
        }

        if (output == null) {
            throw new NullPointerException("output");
        }

        int count = 0;

        for (; input.remaining() >= 2 && output.hasRemaining(); count++) {

            output.put((byte) ((decodeHalf(input.get() & 0xFF) << 4)
                               | decodeHalf(input.get() & 0xFF)));
        }

        return count;
    }


    /**
     * Decodes all remaining nibbles in given input buffer and returns a byte
     * buffer of decoded octets.
     *
     * @param input the input nibble buffer
     *
     * @return a byte buffer of encoded octets
     */
    public static ByteBuffer decodeMultiple(final ByteBuffer input) {

        if (input == null) {
            throw new NullPointerException("input");
        }

        final ByteBuffer output = ByteBuffer.allocate(input.remaining() >> 1);

        final int count = decodeMultiple(input, output);
        assert count == output.capacity() << 1;

        return output;
    }


    /**
     * Creates a new instance.
     */
    public HexDecoder() {

        super();
    }


    /**
     * Decodes given sequence of nibbles into a sequence of octets.
     *
     * @param input the nibbles to decode.
     *
     * @return the decoded octets.
     */
    public byte[] decode(final byte[] input) {

        return decodeMultiple(input);
    }


    /**
     * Decodes given sequence of nibbles into a string.
     *
     * @param input the nibbles to decode
     * @param outputCharsetName the charset name to encode output string
     *
     * @return the decoded string.
     *
     * @throws UnsupportedEncodingException if outputCharset is not supported
     *
     * @see String#String(byte[], java.lang.String)
     */
    public String decodeToString(final byte[] input,
                                 final String outputCharsetName)
        throws UnsupportedEncodingException {

        if (outputCharsetName == null) {
            throw new NullPointerException("outputCharsetName == null");
        }

        return new String(decode(input), outputCharsetName);
    }


    /**
     * Decodes given sequence of nibbles into a string.
     *
     * @param input the nibbles to decode
     * @param outputCharset the charset to encode output string
     *
     * @return the decoded string
     *
     * @see String#String(byte[], java.nio.charset.Charset)
     */
    public String decodeToString(final byte[] input,
                                 final Charset outputCharset) {

        if (outputCharset == null) {
            throw new NullPointerException("outputCharset == null");
        }

        return new String(decode(input), outputCharset);
    }


    /**
     * Decodes given string.
     *
     * @param input the input string to decode
     * @param inputCharsetName the charset name to decode the input string.
     *
     * @return an array of decoded octets
     *
     * @throws UnsupportedEncodingException {@code inputCharsetName} is not
     * supported
     *
     * @see String#getBytes(java.lang.String)
     */
    public byte[] decode(final String input, final String inputCharsetName)
        throws UnsupportedEncodingException {

        if (input == null) {
            throw new NullPointerException("input == null");
        }

        if (inputCharsetName == null) {
            throw new NullPointerException("inputCharsetName == null");
        }

        return decode(input.getBytes(inputCharsetName));
    }


    /**
     * Decodes given string.
     *
     * @param input the input string to decode
     * @param inputCharset the charset to decode the input string.
     *
     * @return an array of decoded octets
     *
     * @see String#getBytes(java.nio.charset.Charset)
     */
    public byte[] decode(final String input, final Charset inputCharset) {

        if (input == null) {
            throw new NullPointerException("input == null");
        }

        if (inputCharset == null) {
            throw new NullPointerException("inputCharset == null");
        }

        return decode(input.getBytes(inputCharset));
    }


    /**
     * Decodes given input string and return decoded result as as string.
     *
     * @param input the input sting to decode
     * @param inputCharsetName the charset name to decode input string.
     * @param outputCharsetName the charset name to encode output string.
     *
     * @return a string of decoded result
     *
     * @throws UnsupportedEncodingException if either {@code inputCharsetName}
     * or {@code outputCharsetName} is not supported
     * @see String#getBytes(java.lang.String)
     * @see String#String(byte[], java.lang.String)
     */
    public String decodeToString(final String input,
                                 final String inputCharsetName,
                                 final String outputCharsetName)
        throws UnsupportedEncodingException {

        if (outputCharsetName == null) {
            throw new NullPointerException("outputCharsetName == null");
        }

        return new String(decode(input, inputCharsetName), outputCharsetName);
    }


    /**
     * Decodes given input string and return decoded result as as string.
     *
     * @param input the input sting to decode
     * @param inputCharset the charset to decode input string.
     * @param outputCharsetName the charset name to encode output string.
     *
     * @return a string of decoded result
     *
     * @throws UnsupportedEncodingException if {@code outputCharsetName} is not
     * supported
     *
     * @see #decode(java.lang.String, java.nio.charset.Charset)
     * @see String#String(byte[], java.lang.String)
     */
    public String decodeToString(final String input, final Charset inputCharset,
                                 final String outputCharsetName)
        throws UnsupportedEncodingException {

        if (outputCharsetName == null) {
            throw new NullPointerException("outputCharsetName == null");
        }

        return new String(decode(input, inputCharset), outputCharsetName);
    }


    /**
     * Decodes given input string and return decoded result as as string.
     *
     * @param input the input sting to decode
     * @param inputCharsetName the charset to decode input string.
     * @param outputCharset the charset to encode output string.
     *
     * @return a string of decoded result
     *
     * @throws UnsupportedEncodingException if {@code inputCharsetName} is not
     * supported
     *
     * @see #decode(java.lang.String, java.lang.String)
     * @see String#String(byte[], java.nio.charset.Charset)
     */
    public String decodeToString(final String input,
                                 final String inputCharsetName,
                                 final Charset outputCharset)
        throws UnsupportedEncodingException {

        if (outputCharset == null) {
            throw new NullPointerException("outputCharset == null");
        }

        return new String(decode(input, inputCharsetName), outputCharset);
    }


    /**
     * Decodes given string and returns result as as string.
     *
     * @param input the input string to decode
     * @param inputCharset the charset to decode input string to byte array.
     * @param outputCharset the charset to encode output bytes to string
     *
     * @return decoded result as a string
     *
     * @see #decode(java.lang.String, java.nio.charset.Charset)
     * @see String#String(byte[], java.nio.charset.Charset)
     */
    public String decodeToString(final String input, final Charset inputCharset,
                                 final Charset outputCharset) {

        if (outputCharset == null) {
            throw new NullPointerException("outputCharset == null");
        }

        return new String(decode(input, inputCharset), outputCharset);
    }


    /**
     * [TESTING].
     *
     * @param input nibbles.
     *
     * @return octets.
     */
    byte[] decodeLikeAnEngineer(final byte[] input) {

        if (input == null) {
            throw new NullPointerException("input");
        }

        if ((input.length & 0x01) == 0x01) {
            throw new IllegalArgumentException(
                "input.length(" + input.length + ") is not even");
        }

        final byte[] output = new byte[input.length >> 1];

        int index = 0; // index in input
        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) ((decodeHalf(input[index++]) << 4)
                                | decodeHalf(input[index++]));
        }

        return output;
    }


    /**
     * [TESTING].
     *
     * @param input nibbles.
     *
     * @return octets.
     */
    byte[] decodeLikeABoss(final byte[] input) {

        if (input == null) {
            throw new NullPointerException("input");
        }

        if ((input.length & 0x01) == 0x01) {
            throw new IllegalArgumentException(
                "input.length(" + input.length + ") is not even");
        }

        final byte[] output = new byte[input.length / 2];

        int index = 0; // index in input
        for (int i = 0; i < output.length; i++) {
            final String s = new String(
                input, index, 2, StandardCharsets.US_ASCII);
            output[i] = (byte) Integer.parseInt(s, 16);
            index += 2;
        }

        return output;
    }


}

