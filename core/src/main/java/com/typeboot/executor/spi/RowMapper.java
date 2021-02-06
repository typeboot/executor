package com.typeboot.executor.spi;

public interface RowMapper {
    Object map(WrappedRow row);
}
