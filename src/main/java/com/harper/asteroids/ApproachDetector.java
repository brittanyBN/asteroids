package com.harper.asteroids;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harper.asteroids.model.CloseApproachData;
import com.harper.asteroids.model.NearEarthObject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Receives a set of neo ids and rates them after earth proximity.
 * Retrieves the approach data for them and sorts to the n closest.
 * https://api.nasa.gov/neo/rest/v1/neo/
 * Alerts if someone is possibly hazardous.
 */
public class ApproachDetector {
    private static final String NEO_URL = "https://api.nasa.gov/neo/rest/v1/neo/";
    private final List<String> nearEarthObjectIds;
    private final Client client;
    private final ObjectMapper mapper;



    public ApproachDetector(List<String> ids) {
        this.nearEarthObjectIds = ids;
        this.client = ClientBuilder.newClient();
        mapper = new ObjectMapper();
    }

    /**
     * Get the n closest approaches in this period
     * @param limit - n
     */
    public List<NearEarthObject> getClosestApproaches(int limit) {
        List<CompletableFuture<NearEarthObject>> futures = nearEarthObjectIds.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> getNeoData(id)))
                .toList();

        List<NearEarthObject> neos = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        System.out.println("Received " + neos.size() + " neos, now sorting");
        return getClosest(neos, limit);
    }

    private NearEarthObject getNeoData(String id) {
        try {
            System.out.println("Check passing of object " + id);
            Response response = client
                    .target(NEO_URL + id)
                    .queryParam("api_key", App.API_KEY)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            return mapper.readValue(response.readEntity(String.class), NearEarthObject.class);
        } catch (IOException e) {
            System.err.println("Failed scanning for asteroids: " + e);
            return null;
        }
    }

    /**
     * Get the closest passing.
     * @param neos the NearEarthObjects
     * @param limit
     * @return
     */
    public static List<NearEarthObject> getClosest(List<NearEarthObject> neos, int limit) {
        Date today = new Date();
        Date nextWeek = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000);

        return neos.stream()
                .filter(neo -> {
                    List<CloseApproachData> closeApproachDataList = neo.getCloseApproachData();
                    if (closeApproachDataList.isEmpty()) {
                        return false;
                    }

                    return closeApproachDataList.stream()
                            .anyMatch(data -> data.getCloseApproachDate() != null &&
                                    !data.getCloseApproachDate().before(today) && !data.getCloseApproachDate().after(nextWeek));
                })
                .sorted(new VicinityComparator())
                .limit(limit)
                .collect(Collectors.toList());
    }

}
