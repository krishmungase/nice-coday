package com.nice.avishkar;

import java.util.Objects;

public final class TravelRequest {

    private final String requestId;
    private final String customerName;
    private final String source;
    private final String destination;
    private final String criteria;

    public TravelRequest(String requestId, String customerName, String source,
            String destination, String criteria) {
        this.requestId = Objects.requireNonNull(requestId, "Request ID cannot be null");
        this.customerName = Objects.requireNonNull(customerName, "Customer name cannot be null");
        this.source = Objects.requireNonNull(source, "Source cannot be null");
        this.destination = Objects.requireNonNull(destination, "Destination cannot be null");
        this.criteria = Objects.requireNonNull(criteria, "Criteria cannot be null");
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getCriteria() {
        return criteria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TravelRequest that = (TravelRequest) o;
        return Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }

    @Override
    public String toString() {
        return String.format("TravelRequest[id=%s, %s->%s, criteria=%s]",
                requestId, source, destination, criteria);
    }
}
