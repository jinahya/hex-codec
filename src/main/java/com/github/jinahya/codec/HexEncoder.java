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


/**
 *
 * @author Jin Kwon <jinahya at gmail.com>
 */
public class HexEncoder {


    /**
     * Encodes a nibble to a single hex char.
     *
     * @param input the nibble to encode
     *
     * @return the encoded hex char.
     */
    private static int encodeHalf(final int input) {

        switch (input) {
            case 0x00:
            case 0x01:
            case 0x02:
            case 0x03:
            case 0x04:
            case 0x05:
            case 0x06:
            case 0x07:
            case 0x08:
            case 0x09:
                return input + 0x30; // 0x30('0') ~ 0x39('9')
            case 0x0A:
            case 0x0B:
            case 0x0C:
            case 0x0D:
            case 0x0E:
            case 0x0F:
                return input + 0x37; // 0x41('A') ~ 0x46('F')
            default:
                throw new IllegalArgumentException("illegal half: " + input);
        }
    }


    /**
     * Encodes a single octet into two nibbles.
     *
     * @param input the octet to encode.
     * @param output the array to which each encoded nibbles are written.
     * @param outoff the offset in the output array.
     */
    public static void encodeSingle(final int input, final byte[] output,
                                    final int outoff) {

        if (output == null) {
            throw new NullPointerException("output == null");
        }

        if (outoff < 0) {
            throw new IllegalArgumentException("outoff(" + outoff + ") < 0");
        }

        if (outoff >= output.length - 1) {
            throw new IllegalArgumentException(
                "outoff(" + outoff + ") >= output.length(" + output.length
                + ") - 1");
        }

        output[outoff] = (byte) encodeHalf((input >> 4) & 0x0F);
        output[outoff + 1] = (byte) encodeHalf(input & 0x0F);
    }


    /**
     * Encodes a single octet into two nibbles.
     *
     * @param input the input byte array
     * @param inoff the offset in the input array
     * @param output the array to which each encoded nibbles are written.
     * @param outoff the offset in the output array.
     */
    public static void encodeSingle(final byte[] input, final int inoff,
                                    final byte[] output, final int outoff) {

        if (input == null) {
            throw new NullPointerException("input == null");
        }

        if (inoff < 0) {
            throw new IllegalArgumentException("inoff(" + inoff + ") < 0");
        }

        if (inoff >= input.length) {
            throw new IllegalArgumentException(
                "inoff(" + inoff + ") >= input.length(" + input.length + ")");
        }

        encodeSingle(input[inoff], output, outoff);
    }


    public static void encodeMultiple(final byte[] input, int inoff,
                                      final byte[] output, int outoff,
                                      final int count) {

        if (count < 0) {
            throw new IllegalArgumentException("count(" + count + ") < 0");
        }

        for (int i = 0; i < count; i++) {
            encodeSingle(input, inoff, output, outoff);
            inoff += 1;
            outoff += 2;
        }
    }


    /**
     * Encodes all or specified number of octets in given input stream and
     * writes encoded nibbles to given output stream.
     *
     * @param input the input stream
     * @param output the output stream
     * @param inbuf the buffer to use
     * @param length the maximum number of octets to encode
     *
     * @return the actual number of octets encoded
     *
     * @throws IOException if an I/O error occurs
     */
    public static long encode(final InputStream input,
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

        final byte[] outbuf = new byte[inbuf.length << 1];

        long count = 0L;

        long remained = length - count;
        for (int read; length == -1L || remained > 0L; count += read) {
            int l = inbuf.length;
            if (length != -1L && l > remained) {
                l = (int) remained;
            }
            read = input.read(inbuf, 0, l);
            if (read == -1) {
                break;
            }
            encodeMultiple(inbuf, 0, outbuf, 0, read);
            output.write(outbuf, 0, read << 1);
            remained -= read;
        }

        return count;
    }


    /**
     *
     * @param input
     * @param output
     * @param length
     *
     * @return number of actual octets encoded
     *
     * @throws IOException if an I/O error occurs.
     *
     * @see #encode(java.io.InputStream, java.io.OutputStream, byte[], long)
     */
    public static long encode(final InputStream input,
                              final OutputStream output, final long length)
        throws IOException {

        return encode(input, output, new byte[4096], length);
    }


    /**
     * Encodes given sequence of octets into a sequence of nibbles.
     *
     * @param input the octets to encode
     *
     * @return the encoded nibbles.
     */
    public static byte[] encodeMultiple(final byte[] input) {

        if (input == null) {
            throw new NullPointerException("input == null");
        }

        final byte[] output = new byte[input.length << 1]; // * 2

        encodeMultiple(input, 0, output, 0, input.length);

        return output;
    }


    /**
     * Encodes all or some remaining octets in given input buffer and put those
     * encoded nibbles into given output buffer.
     *
     * @param input the input octet buffer
     * @param output the output nibble buffer
     *
     * @return the number of octets encoded
     */
    public static int encodeMultiple(final ByteBuffer input,
                                     final ByteBuffer output) {

        if (input == null) {
            throw new NullPointerException("input");
        }

        if (output == null) {
            throw new NullPointerException("output");
        }

        int count = 0;

        for (; input.hasRemaining() && output.remaining() >= 2; count++) {
            final byte octet = input.get();
            output.put((byte) encodeHalf((octet >> 4) & 0x0F));
            output.put((byte) encodeHalf(octet & 0x0F));
        }

        return count;
    }


    /**
     * Encodes all remaining octets in given input buffer and returns a byte
     * buffer of encoded nibbles.
     *
     * @param input the input octet buffer
     *
     * @return a byte buffer of encoded nibbles
     */
    public static ByteBuffer encodeMultiple(final ByteBuffer input) {

        if (input == null) {
            throw new NullPointerException("input");
        }

        final ByteBuffer output = ByteBuffer.allocate(input.remaining() << 1);

        final int count = encodeMultiple(input, output);
        assert count == output.capacity() >> 1;

        return output;
    }


    /**
     * Creates a new instance.
     */
    public HexEncoder() {

        super();
    }


    /**
     * Encodes given sequence of octets into a sequence of nibbles.
     *
     * @param input the octets to encode.
     *
     * @return the encoded nibbles.
     */
    public byte[] encode(final byte[] input) {

        return encodeMultiple(input);
    }


    /**
     *
     * @param input
     * @param outputCharsetName
     *
     * @return
     *
     * @throws UnsupportedEncodingException if {@code outputCharsetName} is not
     * supported
     */
    public String encodedToString(final byte[] input,
                                  final String outputCharsetName)
        throws UnsupportedEncodingException {

        if (outputCharsetName == null) {
            throw new NullPointerException("outputCharsetName == null");
        }

        return new String(encode(input), outputCharsetName);
    }


    public String encodedToString(final byte[] input,
                                  final Charset outputCharset) {

        if (outputCharset == null) {
            throw new NullPointerException("outputCharset == null");
        }

        return new String(encode(input), outputCharset);
    }


    public byte[] encode(final String input, final String inputCharsetName)
        throws UnsupportedEncodingException {

        if (input == null) {
            throw new NullPointerException("input == null");
        }

        if (inputCharsetName == null) {
            throw new NullPointerException("inputCharsetName == null");
        }

        return encode(input.getBytes(inputCharsetName));
    }


    public byte[] encode(final String input, final Charset inputCharset) {

        if (input == null) {
            throw new NullPointerException("input == null");
        }

        if (inputCharset == null) {
            throw new NullPointerException("inputCharset == null");
        }

        return encode(input.getBytes(inputCharset));
    }


    /**
     *
     * @param input
     * @param inputCharsetName
     * @param outputCharsetName
     *
     * @return
     *
     * @throws UnsupportedEncodingException
     *
     * @see #encode(java.lang.String, java.lang.String)
     * @see String#String(byte[], java.lang.String)
     */
    public String encodeToString(final String input,
                                 final String inputCharsetName,
                                 final String outputCharsetName)
        throws UnsupportedEncodingException {

        if (outputCharsetName == null) {
            throw new NullPointerException("outputCharsetName == null");
        }

        return new String(encode(input, inputCharsetName), outputCharsetName);
    }


    /**
     * Encodes given string.
     *
     * @param input the input string.
     * @param inputCharset the charset to decode input string
     * @param outputCharset the charset to encode output string.
     *
     * @return the encoded string.
     *
     * @see #encode(java.lang.String, java.nio.charset.Charset)
     * @see String#String(byte[], java.nio.charset.Charset)
     */
    public String encodeToString(final String input, final Charset inputCharset,
                                 final Charset outputCharset) {

        if (outputCharset == null) {
            throw new NullPointerException("outputCharset == null");
        }

        return new String(encode(input, inputCharset), outputCharset);
    }


    /**
     * [TESTING].
     *
     * @param input octets.
     *
     * @return nibbles.
     */
    byte[] encodeLikeAnEngineer(final byte[] input) {

        if (input == null) {
            throw new NullPointerException("input");
        }

        final byte[] output = new byte[input.length << 1];

        int index = 0; // index in output
        for (int i = 0; i < input.length; i++) {
            output[index++] = (byte) encodeHalf((input[i] >> 4) & 0x0F);
            output[index++] = (byte) encodeHalf((input[i] & 0x0F));
        }

        return output;
    }


    /**
     * [TESTING].
     *
     * @param input octets.
     *
     * @return nibbles.
     */
    byte[] encodeLikeABoss(final byte[] input) {

        if (input == null) {
            throw new NullPointerException("input");
        }

        final byte[] output = new byte[input.length << 1];

        int index = 0; // index in output
        for (int i = 0; i < input.length; i++) {
            String s = Integer.toString(input[i] & 0xFF, 16);
            if (s.length() == 1) {
                s = "0" + s;
            }
            output[index++] = (byte) s.charAt(0);
            output[index++] = (byte) s.charAt(1);
        }

        return output;
    }


}

