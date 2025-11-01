package com.nice.avishkar;

import java.nio.file.Path;
import java.util.Objects;

public final class ResourceInfo {

    private final Path transportSchedulePath;
    private final Path customerRequestPath;

    public ResourceInfo(Path transportSchedulePath, Path customerRequestPath) {
        this.transportSchedulePath = Objects.requireNonNull(
                transportSchedulePath, "Transport schedule path cannot be null"
        );
        this.customerRequestPath = Objects.requireNonNull(
                customerRequestPath, "Customer request path cannot be null"
        );
    }

    public Path getTransportSchedulePath() {
        return transportSchedulePath;
    }

    public Path getCustomerRequestPath() {
        return customerRequestPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResourceInfo that = (ResourceInfo) o;
        return Objects.equals(transportSchedulePath, that.transportSchedulePath)
                && Objects.equals(customerRequestPath, that.customerRequestPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transportSchedulePath, customerRequestPath);
    }

    @Override
    public String toString() {
        return String.format("ResourceInfo[schedules=%s, requests=%s]",
                transportSchedulePath, customerRequestPath);
    }
}
