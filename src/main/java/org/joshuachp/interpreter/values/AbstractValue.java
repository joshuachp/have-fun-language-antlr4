package org.joshuachp.interpreter.values;

import java.util.Objects;

public abstract class AbstractValue<T> extends AbstractReturnValue {

    protected final T value;

    public AbstractValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractValue<?> that = (AbstractValue<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
