package one.nullhaven.safebt;

import net.minecraft.server.v1_5_R3.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.DataInput;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SafeNbtDecoderTagKeyDecodeTest {
    SafeNbtDecoder decoder;

    @BeforeEach
    void setUp() {
        decoder = new SafeNbtDecoder();
        decoder.setAllowAnyRootTag(true);
    }

    private static @NotNull Stream<Arguments> testTagKeysParams() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("regular_key"),
                Arguments.of("special_!@#$%^&*()"),
                Arguments.of(" \t\r\n"),
                Arguments.of("123"),
                Arguments.of("𝔘𝔫𝔦𝔠𝔬𝔡𝔢"),
                Arguments.of("test")
        );
    }

    @ParameterizedTest
    @MethodSource("testTagKeysParams")
    void testByteTag(@NotNull String key) throws IOException {
        NBTTagByte original = new NBTTagByte(key, (byte) 0);
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original.getName(), decoded.getName());
    }

    @ParameterizedTest
    @MethodSource("testTagKeysParams")
    void testShortTag(@NotNull String key) throws IOException {
        NBTTagShort original = new NBTTagShort(key, (short) 0);
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original.getName(), decoded.getName());
    }

    @ParameterizedTest
    @MethodSource("testTagKeysParams")
    void testIntTag(@NotNull String key) throws IOException {
        NBTTagInt original = new NBTTagInt(key, 0);
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original.getName(), decoded.getName());
    }

    @ParameterizedTest
    @MethodSource("testTagKeysParams")
    void testLongTag(@NotNull String key) throws IOException {
        NBTTagLong original = new NBTTagLong(key, 0L);
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original.getName(), decoded.getName());
    }

    @ParameterizedTest
    @MethodSource("testTagKeysParams")
    void testFloatTag(@NotNull String key) throws IOException {
        NBTTagFloat original = new NBTTagFloat(key, 0F);
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original.getName(), decoded.getName());
    }

    @ParameterizedTest
    @MethodSource("testTagKeysParams")
    void testDoubleTag(@NotNull String key) throws IOException {
        NBTTagDouble original = new NBTTagDouble(key, 0D);
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original.getName(), decoded.getName());
    }

    @ParameterizedTest
    @MethodSource("testTagKeysParams")
    void testStringTag(@NotNull String key) throws IOException {
        NBTTagString original = new NBTTagString(key, "");
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original.getName(), decoded.getName());
    }

    @ParameterizedTest
    @MethodSource("testTagKeysParams")
    void testIntArrayTag(@NotNull String key) throws IOException {
        NBTTagIntArray original = new NBTTagIntArray(key, new int[]{});
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original.getName(), decoded.getName());
    }

    @ParameterizedTest
    @MethodSource("testTagKeysParams")
    void testByteArrayTag(@NotNull String key) throws IOException {
        NBTTagByteArray original = new NBTTagByteArray(key, new byte[]{});
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original.getName(), decoded.getName());
    }

    @ParameterizedTest
    @MethodSource("testTagKeysParams")
    void testListTag(@NotNull String key) throws IOException {
        NBTTagList original = new NBTTagList(key);
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original.getName(), decoded.getName());
    }

    @ParameterizedTest
    @MethodSource("testTagKeysParams")
    void testCompoundTag(@NotNull String key) throws IOException {
        NBTTagCompound original = new NBTTagCompound(key);
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original.getName(), decoded.getName());
    }
}
