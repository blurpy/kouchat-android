
/***************************************************************************
 *   Copyright 2006-2019 by Christian Ihle                                 *
 *   contact@kouchat.net                                                   *
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
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.junit.ExpectedException;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockingDetails;

/**
 * Tests of {@link TestUtils}.
 *
 * @author Christian Ihle
 */
public class TestUtilsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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

    @Test
    public void getFieldValueShouldSupportInheritedPrivateAndPublicFields() {
        final ExtendingTestClass extendingTestClass = new ExtendingTestClass("private", 555);

        final String privateField = TestUtils.getFieldValue(extendingTestClass, String.class, "privateField");
        assertEquals("private", privateField);

        final Integer publicField = TestUtils.getFieldValue(extendingTestClass, Integer.class, "publicField");
        assertEquals(Integer.valueOf(555), publicField);
    }

    @Test
    public void getFieldValueShouldThrowExceptionIfInvalidFieldName() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("java.lang.NoSuchFieldException: wrongField");

        final TestClass testClass = new TestClass(null, null);

        TestUtils.getFieldValue(testClass, String.class, "wrongField");
    }

    @Test
    public void getFieldValueShouldThrowExceptionIfInvalidFieldClass() {
        expectedException.expect(ClassCastException.class);
        // The message seems to be missing in some versions of the JDK, so can't verify reliably
        // expectedException.expectMessage("Cannot cast java.lang.String to java.lang.Integer");

        final TestClass testClass = new TestClass("test", 1);

        TestUtils.getFieldValue(testClass, Integer.class, "privateField");
    }

    @Test
    public void setFieldValueShouldSupportPrivateAndPublicFields() {
        final TestClass testClass = new TestClass("test", 1);
        TestUtils.setFieldValue(testClass, "publicField", 50);
        TestUtils.setFieldValue(testClass, "privateField", "something");

        assertEquals(Integer.valueOf(50), testClass.publicField);
        assertEquals("something", testClass.getPrivateField());
    }

    @Test
    public void setFieldValueShouldSupportInheritedPrivateAndPublicFields() {
        final ExtendingTestClass extendingTestClass = new ExtendingTestClass("test", 1);
        TestUtils.setFieldValue(extendingTestClass, "publicField", 50);
        TestUtils.setFieldValue(extendingTestClass, "privateField", "something");

        assertEquals(Integer.valueOf(50), extendingTestClass.publicField);
        assertEquals("something", extendingTestClass.getPrivateField());
    }

    @Test
    public void setFieldValueShouldSupportSettingNull() {
        final TestClass testClass = new TestClass("test", 1);
        TestUtils.setFieldValue(testClass, "publicField", null);
        TestUtils.setFieldValue(testClass, "privateField", null);

        assertNull(testClass.publicField);
        assertNull(testClass.getPrivateField());
    }

    @Test
    public void setFieldValueShouldThrowExceptionIfInvalidFieldName() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("java.lang.NoSuchFieldException: wrongField");

        final TestClass testClass = new TestClass(null, null);

        TestUtils.setFieldValue(testClass, "wrongField", null);
    }

    @Test
    public void setFieldValueWithMockShouldCreateAMockAndSetTheMockInTheCorrectFieldAndReturnTheMock() {
        final CustomTestClass customTestClass = new CustomTestClass();
        assertNull(customTestClass.getTestClass());

        final TestClass testClassMock = TestUtils.setFieldValueWithMock(customTestClass, "testClass", TestClass.class);

        assertSame(testClassMock, customTestClass.getTestClass());

        final MockingDetails mockingDetails = mockingDetails(testClassMock);
        assertTrue(mockingDetails.isMock());
    }

    @Test
    public void fieldValueIsNullShouldBeTrueIfNull() {
        assertTrue(TestUtils.fieldValueIsNull(new TestClass(null, 1), "privateField"));
    }

    @Test
    public void fieldValueIsNullShouldBeFalseIfNotNull() {
        assertFalse(TestUtils.fieldValueIsNull(new TestClass("not null", 1), "privateField"));
    }

    @Test
    public void allFieldsAreNullShouldBeFalseIfAFieldHaveAValue() {
        assertFalse(TestUtils.allFieldsAreNull(new TestClass("not null", 1)));
        assertFalse(TestUtils.allFieldsAreNull(new TestClass("not null", null)));
        assertFalse(TestUtils.allFieldsAreNull(new TestClass(null, 3)));
    }

    @Test
    public void allFieldsAreNullShouldBeTrueIfAllFieldsHaveNullValues() {
        assertTrue(TestUtils.allFieldsAreNull(new TestClass(null, null)));
    }

    @Test
    public void allFieldsAreNullShouldBeTrueEvenIfPrimitiveFieldExists() {
        assertTrue(TestUtils.allFieldsAreNull(new PrimitiveTestClass(true)));
    }

    @Test
    public void allFieldsHaveValueShouldBeFalseIfAFieldIsNull() {
        assertFalse(TestUtils.allFieldsHaveValue(new TestClass(null, null)));
        assertFalse(TestUtils.allFieldsHaveValue(new TestClass("not null", null)));
        assertFalse(TestUtils.allFieldsHaveValue(new TestClass(null, 3)));
    }

    @Test
    public void allFieldsHaveValueShouldBeTrueIfAllFieldsHaveValues() {
        assertTrue(TestUtils.allFieldsHaveValue(new TestClass("not null", 1)));
    }

    @Test
    public void allFieldsHaveValueShouldBeTrueEvenIfPrimitiveFieldExists() {
        assertTrue(TestUtils.allFieldsHaveValue(new PrimitiveTestClass(true)));
    }
}
