
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package net.usikkert.kouchat.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests of {@link TestUtils}.
 *
 * @author Christian Ihle
 */
public class TestUtilsTest {

    @Test
    public void getFieldValueShouldSupportPrivateAndPublicFields() {
        final TestClass testClass = new TestClass("private", 555);

        final String privateField = TestUtils.getFieldValue(testClass, String.class, "privateField");
        assertEquals("private", privateField);

        final Integer publicField = TestUtils.getFieldValue(testClass, Integer.class, "publicField");
        assertEquals(Integer.valueOf(555), publicField);
    }

    @Test
    public void getFieldValueShouldSupportFieldsWithNull() {
        final TestClass testClass = new TestClass(null, null);

        final String privateField = TestUtils.getFieldValue(testClass, String.class, "privateField");
        assertNull(privateField);
    }

    @Test(expected = RuntimeException.class)
    public void getFieldValueShouldThrowExceptionIfInvalidFieldName() {
        final TestClass testClass = new TestClass(null, null);

        TestUtils.getFieldValue(testClass, String.class, "wrongField");
    }

    @Test(expected = ClassCastException.class)
    public void getFieldValueShouldThrowExceptionIfInvalidFieldClass() {
        final TestClass testClass = new TestClass("test", 1);

        TestUtils.getFieldValue(testClass, Integer.class, "privateField");
    }

    @Test
    public void setFieldValueShouldSupportPrivateAndPublicFields() {
        final TestClass testClass = new TestClass("test", 1);
        TestUtils.setFieldValue(testClass, "publicField", 50);
        TestUtils.setFieldValue(testClass, "privateField", "something");

        assertEquals(Integer.valueOf(50), testClass.publicField);
        assertEquals("something", testClass.privateField);
    }

    @Test
    public void setFieldValueShouldSupportSettingNull() {
        final TestClass testClass = new TestClass("test", 1);
        TestUtils.setFieldValue(testClass, "publicField", null);
        TestUtils.setFieldValue(testClass, "privateField", null);

        assertNull(testClass.publicField);
        assertNull(testClass.privateField);
    }

    @Test(expected = RuntimeException.class)
    public void setFieldValueShouldThrowExceptionIfInvalidFieldName() {
        final TestClass testClass = new TestClass(null, null);

        TestUtils.setFieldValue(testClass, "wrongField", null);
    }

    class TestClass {

        private final String privateField;
        public Integer publicField;

        TestClass(final String privateField, final Integer publicField) {
            this.privateField = privateField;
            this.publicField = publicField;
        }
    }
}
