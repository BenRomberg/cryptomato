package com.benromberg.cryptomato;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Path("/sample")
public class SampleResource {
    @GET
    @Path("/health")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getHealth() {
        return Response.ok(new HealthStatusDto("up and running")).build();
    }

    @XmlRootElement
    public static class HealthStatusDto {
        private String statusMessage;

        public HealthStatusDto() {
            // for JAXB
        }

        HealthStatusDto(String statusMessage) {
            this.statusMessage = statusMessage;
        }

        @XmlElement
        public String getStatusMessage() {
            return statusMessage;
        }
    }
}