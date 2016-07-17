package io.jryan.thingo;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BoardUnitTest {

    @Test
    public void numberOfColumnsAndRowsIsGreaterThanZero() {
        assertTrue(Board.NUMBER_OF_COLUMNS_AND_ROWS > 0);
    }
}
