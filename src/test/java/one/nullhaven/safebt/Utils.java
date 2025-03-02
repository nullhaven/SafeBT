package one.nullhaven.safebt;

import net.minecraft.server.v1_5_R3.NBTBase;
import net.minecraft.server.v1_5_R3.NBTTagList;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

public final class Utils {
    public static byte @NotNull [] getTagBytes(@NotNull NBTBase tag) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(bytes);
        NBTBase.a(tag, out);
        return bytes.toByteArray();
    }

    public static @NotNull DataInput getTagInput(@NotNull NBTBase tag) {
        byte[] bytes = getTagBytes(tag);
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        return new DataInputStream(stream);
    }

    public static @NotNull DataInputStream newGzippedDataInput(@NotNull Path path) throws IOException {
        InputStream fileInput = Files.newInputStream(path);
        InputStream gzipInput = new GZIPInputStream(fileInput);
        return new DataInputStream(gzipInput);
    }

    public static void setListTagType(@NotNull NBTTagList list, byte type) {
        try {
            Field fType = NBTTagList.class.getDeclaredField("type");
            fType.setAccessible(true);
            fType.set(list, type);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Utils() {

    }
}
