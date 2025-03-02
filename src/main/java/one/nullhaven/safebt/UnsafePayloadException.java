package one.nullhaven.safebt;

/**
 * An exception thrown when an NBT payload is violating restrictions of
 * safe NBT decoding algorithm.
 */
public class UnsafePayloadException extends RuntimeException {
    public UnsafePayloadException(String message) {
        super(message);
    }
}
