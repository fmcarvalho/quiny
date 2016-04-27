/*
 * Copyright (c) 2016, Miguel Gamboa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package quiny.test;

import quiny.Queryable;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Miguel Gamboa
 *         created on 22-04-2016
 */
public class QueryableTests {

    private final List<String> data = Arrays
            .asList("isel", "ola", "isel", "ola", "-super", "babel", "super");

    @Test
    public void testQueryableForEach() {
        final Iterator<String> expected = Arrays
                .asList("isel", "ola", "isel", "ola", "-super", "babel", "super")
                .iterator();
        Queryable
                .of(data)
                .forEach(w -> Assert.assertEquals(expected.next(), w));

    }

    @Test
    public void testQueryableMap() {

        final Integer[] expected = {4, 3, 4, 3, 6, 5, 5};
        final Integer[] actual = Queryable
                .of(data)
                .map(String::length)
                .toArray(size -> new Integer[size]);
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testQueryableFilter() {
        final String[] expected = {"isel", "ola", "isel", "ola", "babel", "super"};
        final String[] actual = Queryable
                .of(data)
                .filter(w -> !w.startsWith("-"))
                .toArray(size -> new String[size]);
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testQueryableDistinct() {
        final String[] expected = {"isel", "ola", "-super", "babel", "super"};
        final String[] actual = Queryable
                .of(data)
                .distinct()
                .toArray(size -> new String[size]);
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testQueryableLimit() {
        final String[] expected = {"isel", "ola", "isel"};
        final String[] actual = Queryable
                .of(data)
                .limit(3)
                .toArray(size -> new String[size]);
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testQueryableChain() {
        final Iterator<Integer> expected = Arrays.asList(4, 3, 5).iterator();
        Queryable
                .of(data)
                .distinct()
                .filter((String w) -> !w.startsWith("-"))
                .map(String::length)
                .limit(3)
                .forEach(l -> Assert.assertEquals(expected.next(), l));
    }
}
