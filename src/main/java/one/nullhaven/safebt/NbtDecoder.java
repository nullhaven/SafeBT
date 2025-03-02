package one.nullhaven.safebt;

import net.minecraft.server.v1_5_R3.NBTBase;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.IOException;

/**
 * Abstract NBT payload decoder interface.
 *
 * @author dreamscached
 * @version 1.0.0
 * @since 1.0.0
 */
public interface NbtDecoder {
    /**
     * Decodes NBT payload stream.
     *
     * @param in {@link DataInput} stream with incoming payload
     * @return decoded {@link NBTBase} implementation
     * @throws IOException in case of unexpected input/output error
     */
    @NotNull NBTBase decode(@NotNull DataInput in) throws IOException;
}
