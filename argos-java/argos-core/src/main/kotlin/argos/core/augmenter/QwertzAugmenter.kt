/*
 * Copyright (c) 2016-2019, Leftshift One
 * __________________
 * [2019] Leftshift One
 * All Rights Reserved.
 * NOTICE:  All information contained herein is, and remains
 * the property of Leftshift One and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Leftshift One
 * and its suppliers and may be covered by Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Leftshift One.
 */

package argos.core.augmenter

import java.util.*

/**
 * Class to add typing errors to an input text.
 *
 * @param seed the initial seed of a Random-object
 */
class QwertzAugmenter(seed: Long? = null) {

    private val random = if (seed != null) Random(seed) else Random()

    companion object {
        val map = HashMap<Char, List<Char>>()

        init {
            map['1'] = listOf('2', 'q')
            map['2'] = listOf('1', '3', 'q', 'w')
            map['3'] = listOf('2', '4', 'w', 'e')
            map['4'] = listOf('3', '5', 'e', 'r')
            map['5'] = listOf('4', '6', 'r', 't')
            map['6'] = listOf('5', '7', 't', 'z')
            map['7'] = listOf('6', '8', 'z', 'u')
            map['8'] = listOf('7', '9', 'u', 'i')
            map['9'] = listOf('8', '0', 'i', 'o')
            map['0'] = listOf('9', 'ß', 'o', 'p')
            map['q'] = listOf('1', '2', 'w', 'a')
            map['w'] = listOf('2', '3', 'q', 'e', 'a', 's', 'd')
            map['e'] = listOf('3', '4', 'w', 'r', 's', 'd', 'f')
            map['r'] = listOf('4', '5', 'e', 't', 'd', 'f')
            map['t'] = listOf('5', '6', 'r', 'z', 'f', 'g', 'h')
            map['z'] = listOf('6', '7', 't', 'u', 'g', 'h', 'j')
            map['u'] = listOf('7', '8', 'z', 'i', 'h', 'j')
            map['i'] = listOf('8', '9', 'u', 'o', 'j', 'k')
            map['o'] = listOf('9', '0', 'ß', 'p', 'k', 'l', 'ö')
            map['p'] = listOf('0', 'ß', 'o', 'ü', 'l', 'ö')
            map['ü'] = listOf('p', 'ö', 'ä')
            map['a'] = listOf('q', 'w', 's', 'y')
            map['s'] = listOf('a', 'w', 'e', 'd', 'y', 'x')
            map['d'] = listOf('w', 'e', 'r', 's', 'f', 'x', 'c')
            map['f'] = listOf('e', 'r', 't', 'd', 'g', 'c', 'v')
            map['g'] = listOf('t', 'z', 'f', 'h', 'v', 'b')
            map['h'] = listOf('t', 'z', 'u', 'g', 'j', 'b', 'n')
            map['j'] = listOf('z', 'u', 'i', 'h', 'k', 'n', 'm')
            map['k'] = listOf('i', 'o', 'j', 'l', 'm')
            map['l'] = listOf('o', 'p', 'k', 'ö')
            map['ö'] = listOf('o', 'p', 'ü', 'l', 'ä')
            map['ä'] = listOf('ü', 'ö')
            map['y'] = listOf('a', 's', 'x')
            map['x'] = listOf('s', 'd', 'y', 'c')
            map['c'] = listOf('d', 'f', 'x', 'v')
            map['v'] = listOf('f', 'g', 'c', 'b')
            map['b'] = listOf('g', 'h', 'v', 'n')
            map['n'] = listOf('h', 'j', 'b', 'm')
            map['m'] = listOf('j', 'k', 'n')
        }
    }

    /**
     * add typos to a text
     *
     * @param value the input text
     *
     * @return the augmented text
     */
    fun augment(value: String): String {
        val tokens = value.split("\\s+|(?=\\p{Punct})|(?<=\\p{Punct})".toRegex())
                .filter { it.isNotBlank() }
                .mapIndexed { _, s ->
                    if (s.length > 2 && random.nextFloat() <= 0.2) {
                        val array = s.toCharArray()
                        val index = random.nextInt(s.length)

                        val list = map.get(array[index])
                        if (list != null) {
                            array[index] = list.get(random.nextInt(list.size))
                            return@mapIndexed String(array)
                        }
                    }
                    s
                }
        return tokens.joinToString(" ")
    }
}
