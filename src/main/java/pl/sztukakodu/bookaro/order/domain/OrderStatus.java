package pl.sztukakodu.bookaro.order.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum OrderStatus {
    NEW {
        @Override
        public UpdateStatusResult updateStatus(OrderStatus status) {
            return switch (status) {
                case PAID -> UpdateStatusResult.ok(PAID);
                case CANCELED -> UpdateStatusResult.revoked(CANCELED);
                case ABANDONED -> UpdateStatusResult.revoked(ABANDONED);
                default -> super.updateStatus(status);
            };
        }
    },
    PAID {
        @Override
        public UpdateStatusResult updateStatus(OrderStatus status) {
            if (status == SHIPPED) {
                return UpdateStatusResult.ok(SHIPPED);
            }
            return super.updateStatus(status);
        }
    },
    CANCELED {
        @Override
        public UpdateStatusResult updateStatus(OrderStatus status) {
            return super.updateStatus(status);
        }
    },
    ABANDONED {
        @Override
        public UpdateStatusResult updateStatus(OrderStatus status) {
            return super.updateStatus(status);
        }
    },
    SHIPPED {
        @Override
        public UpdateStatusResult updateStatus(OrderStatus status) {
            return super.updateStatus(status);
        }
    };

    public static Optional<OrderStatus> parseString(String value) {
        return Arrays.stream(values())
                .filter(it -> StringUtils.equalsIgnoreCase(it.name(), value))
                .findFirst();
    }

    public UpdateStatusResult  updateStatus(OrderStatus status) {
        throw new IllegalArgumentException("Unable to mark " + this.name() + " order as " + status.name());
    }
}