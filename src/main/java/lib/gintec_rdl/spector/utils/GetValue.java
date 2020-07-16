package lib.gintec_rdl.spector.utils;

public class GetValue<T> {
    private final T value;

    private GetValue(T value) {
        this.value = value;
    }

    public T ifNull(T alt) {
        return this.value != null ? value : alt;
    }

    public T notNull() {
        return notNull("Value cannot be null");
    }

    public T notNull(String message) {
        if (value == null) throw new NullPointerException(message);
        return value;
    }

    public static <T> GetValue<T> of(T instance) {
        return new GetValue<T>(instance);
    }
}
