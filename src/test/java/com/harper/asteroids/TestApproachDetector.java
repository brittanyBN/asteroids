package com.harper.asteroids;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harper.asteroids.model.NearEarthObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestApproachDetector {

    private ObjectMapper mapper = new ObjectMapper();
    private NearEarthObject neo1, neo2;

    @Before
    public void setUp() throws IOException {
        neo1 = mapper.readValue(getClass().getResource("/neo_example.json"), NearEarthObject.class);
        neo2 = mapper.readValue(getClass().getResource("/neo_example2.json"), NearEarthObject.class);

    }

    @Test
    public void testFiltering() {
        List<NearEarthObject> neos = List.of(neo1, neo2);
        List<NearEarthObject> filtered = ApproachDetector.getClosest(neos, 1);
        //Neo2 has the closest passing at 5261628 kms away.
        // TODO: Neo2's closest passing is in 2028.
        // In Jan 202, neo1 is closer (5390966 km, vs neo2's at 7644137 km)

        Date today = new Date();
        Date nextWeek = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000);

        List<NearEarthObject> dateFiltered = filtered.stream()
                .filter(neo -> neo.getCloseApproachData().stream()
                        .anyMatch(data -> data.getCloseApproachDate() != null &&
                                !data.getCloseApproachDate().before(today) && !data.getCloseApproachDate().after(nextWeek)))
                .toList();

        assertEquals(1, dateFiltered.size());
        assertEquals(neo2, dateFiltered.get(0));
    }
}
