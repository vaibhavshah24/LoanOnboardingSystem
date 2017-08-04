package com.finance.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static com.finance.data.DataMap.*;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DataMapTest {

    @Test
    public void shouldLoadDataInHashMaps() throws Exception {
        prepareDataMap();
        assertEquals(5, MENU_ITEMS.size());
        assertEquals(6, APPROVAL_HIERARCHY.size());
        assertEquals(6, APPROVAL_QUEUE.size());
        assertEquals(4, USER_ROLE_MENU_MAPPING.size());
    }

}