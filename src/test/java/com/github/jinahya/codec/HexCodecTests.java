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


import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.lang3.RandomStringUtils;


/**
 *
 * @author Jin Kwon <jinahya at gmail.com>
 */
public final class HexCodecTests {


    static Random random() {

        return ThreadLocalRandom.current();
    }


    private static final byte[] EMPTY_ENCODED_BYTES = new byte[0];


    private static final String EMPTY_ENCODED_STRING = "";


    private static final byte[] EMPTY_DECODED_BYTES = new byte[0];


    private static final String EMPTY_DECODED_STRING = "";


    public static byte[] decodedBytes(final int maxlen) {

        if (maxlen < 0) {
            throw new IllegalArgumentException("maxlen(" + maxlen + ") < 0");
        }

        if (maxlen == 0) {
            return EMPTY_DECODED_BYTES;
        }

        final byte[] decodedBytes = new byte[random().nextInt(maxlen)];

        random().nextBytes(decodedBytes);

        return decodedBytes;
    }


    public static byte[] decodedBytes() {

        return decodedBytes(4096);
    }


    static String decodedString(final int maxlen) {

        if (maxlen < 0) {
            throw new IllegalArgumentException("maxlen(" + maxlen + ") < 0");
        }

        if (maxlen == 0) {
            return EMPTY_DECODED_STRING;
        }

        return RandomStringUtils.random(random().nextInt(maxlen));
    }


    static String decodedString() {

        return decodedString(4096);
    }


    public static byte[] encodedBytes(final int maxlen) {

        if (maxlen < 0) {
            throw new IllegalArgumentException("maxlen(" + maxlen + ") < 0");
        }

        if ((maxlen & 1) == 1) {
            throw new IllegalArgumentException(
                "(maxlen(" + maxlen + ") & 1) == 1");
        }

        if (maxlen == 0) {
            return EMPTY_ENCODED_BYTES;
        }

        final byte[] encodedBytes = new byte[random().nextInt(maxlen / 2) << 1];

        for (int i = 0; i < encodedBytes.length; i++) {
            switch (random().nextInt() % 3) {
                case 0: // alpha
                    encodedBytes[i] = (byte) (random().nextInt(0x0A) + 0x30);
                    break;
                case 1: // upper
                    encodedBytes[i] = (byte) (random().nextInt(0x06) + 0x41);
                    break;
                default: // lower
                    encodedBytes[i] = (byte) (random().nextInt(0x06) + 0x61);
                    break;
            }
        }

        return encodedBytes;
    }


    public static byte[] encodedBytes() {

        return encodedBytes(8192);
    }


    public static String encodedString(final int bound) {

        if (bound < 0) {
            throw new IllegalArgumentException("bound(" + bound + ") < 0");
        }

        if ((bound & 1) == 1) {
            throw new IllegalArgumentException(
                "(bound(" + bound + ") & 1) == 1");
        }

        if (bound == 0) {
            return EMPTY_ENCODED_STRING;
        }

        return new String(encodedBytes(bound), StandardCharsets.US_ASCII);
    }


    public static String encodedString() {

        return encodedString(8192);
    }


    static byte[] uppercase(final byte[] lowercased) {

        final byte[] uppercased = new byte[lowercased.length];

        for (int i = 0; i < uppercased.length; i++) {
            if (lowercased[i] >= 0x61 && lowercased[i] <= 0x7A) {
                uppercased[i] = (byte) (lowercased[i] - 0x20);
            } else {
                uppercased[i] = lowercased[i];
            }
        }

        return uppercased;
    }


    private HexCodecTests() {

        super();
    }


}

