package one.nullhaven.safebt;

import net.minecraft.server.v1_5_R3.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.DataInput;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SafeNbtDecoderTagDecodeTest {
    SafeNbtDecoder decoder;

    @BeforeEach
    void setUp() {
        decoder = new SafeNbtDecoder();
        decoder.setAllowAnyRootTag(true);
    }

    @Test
    void testDecodeTagEnd() throws IOException {
        decoder.setAllowStrayEndTags(true);
        NBTBase original = new NBTTagEnd();
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original, decoded);
    }

    @ParameterizedTest
    @ValueSource(bytes = {Byte.MIN_VALUE, Byte.MAX_VALUE, -1, 0, 1})
    void testDecodeTagByte(byte value) throws IOException {
        NBTBase original = new NBTTagByte("tag", value);
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original, decoded);
    }

    @ParameterizedTest
    @ValueSource(shorts = {Short.MIN_VALUE, Short.MAX_VALUE, -1, 0, 1})
    void testDecodeTagShort(short value) throws IOException {
        NBTBase original = new NBTTagShort("tag", value);
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original, decoded);
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, Integer.MAX_VALUE, -1, 0, 1})
    void testDecodeTagInt(int value) throws IOException {
        NBTBase original = new NBTTagInt("tag", value);
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original, decoded);
    }

    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, Long.MAX_VALUE, -1, 0, 1})
    void testDecodeTagLong(long value) throws IOException {
        NBTBase original = new NBTTagLong("tag", value);
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original, decoded);
    }

    @ParameterizedTest
    @ValueSource(floats = {Float.MIN_VALUE, Float.MAX_VALUE, -1, 0, 1})
    void testDecodeTagFloat(float value) throws IOException {
        NBTBase original = new NBTTagFloat("tag", value);
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original, decoded);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.MIN_VALUE, Double.MAX_VALUE, -1, 0, 1})
    void testDecodeTagDouble(double value) throws IOException {
        NBTBase original = new NBTTagDouble("tag", value);
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original, decoded);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "test", "123", "special_!@#$%^&*()", " \t\r\n", "𝔘𝔫𝔦𝔠𝔬𝔡𝔢"})
    void testDecodeTagString(@NotNull String value) throws IOException {
        NBTBase original = new NBTTagString("tag", value);
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original, decoded);
    }

    private static @NotNull Stream<Arguments> testDecodeTagByteArray() {
        //noinspection RedundantCast
        return Stream.of(
                Arguments.of((Object) new byte[0]),
                Arguments.of((Object) new byte[]{Byte.MIN_VALUE, Byte.MAX_VALUE, -1, 0, 1}),
                Arguments.of((Object) new byte[1]),
                Arguments.of((Object) new byte[10]),
                Arguments.of((Object) new byte[100])
        );
    }

    @ParameterizedTest
    @MethodSource
    void testDecodeTagByteArray(byte @NotNull [] value) throws IOException {
        NBTBase original = new NBTTagByteArray("tag", value);
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original, decoded);
    }

    private static @NotNull Stream<Arguments> testDecodeTagIntArray() {
        //noinspection RedundantCast
        return Stream.of(
                Arguments.of((Object) new int[0]),
                Arguments.of((Object) new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, -1, 0, 1}),
                Arguments.of((Object) new int[1]),
                Arguments.of((Object) new int[10]),
                Arguments.of((Object) new int[100])
        );
    }

    @ParameterizedTest
    @MethodSource
    void testDecodeTagIntArray(int @NotNull [] value) throws IOException {
        NBTBase original = new NBTTagIntArray("tag", value);
        DataInput input = Utils.getTagInput(original);
        NBTBase decoded = decoder.decode(input);
        assertEquals(original, decoded);
    }

    @Nested
    class testDecodeTagList {
        @Test
        void empty() throws IOException {
            NBTBase original = new NBTTagList("tag");
            DataInput input = Utils.getTagInput(original);
            NBTTagList decoded = (NBTTagList) decoder.decode(input);
            // NOTE: empty lists serialize as list of bytes (type id = 1),
            // this is irrelevant for this case
            Utils.setListTagType(decoded, (byte) 1);
            assertEquals(original, decoded);
        }

        @Test
        void ends() throws IOException {
            decoder.setAllowStrayEndTags(true);
            NBTTagList original = new NBTTagList("tag");
            for (int i = 0; i < 10; i++) original.add(new NBTTagEnd());
            DataInput input = Utils.getTagInput(original);
            NBTTagList decoded = (NBTTagList) decoder.decode(input);
            assertEquals(original, decoded);
        }

        @Test
        void bytes() throws IOException {
            NBTTagList original = new NBTTagList("tag");
            for (int i = 0; i < 10; i++) original.add(new NBTTagByte("", (byte) 0));
            DataInput input = Utils.getTagInput(original);
            NBTTagList decoded = (NBTTagList) decoder.decode(input);
            assertEquals(original, decoded);
        }

        @Test
        void shorts() throws IOException {
            NBTTagList original = new NBTTagList("tag");
            for (int i = 0; i < 10; i++) original.add(new NBTTagShort("", (short) 0));
            DataInput input = Utils.getTagInput(original);
            NBTTagList decoded = (NBTTagList) decoder.decode(input);
            assertEquals(original, decoded);
        }

        @Test
        void ints() throws IOException {
            NBTTagList original = new NBTTagList("tag");
            for (int i = 0; i < 10; i++) original.add(new NBTTagInt("", 0));
            DataInput input = Utils.getTagInput(original);
            NBTTagList decoded = (NBTTagList) decoder.decode(input);
            assertEquals(original, decoded);
        }

        @Test
        void longs() throws IOException {
            NBTTagList original = new NBTTagList("tag");
            for (int i = 0; i < 10; i++) original.add(new NBTTagLong("", 0L));
            DataInput input = Utils.getTagInput(original);
            NBTTagList decoded = (NBTTagList) decoder.decode(input);
            assertEquals(original, decoded);
        }

        @Test
        void floats() throws IOException {
            NBTTagList original = new NBTTagList("tag");
            for (int i = 0; i < 10; i++) original.add(new NBTTagFloat("", 0F));
            DataInput input = Utils.getTagInput(original);
            NBTTagList decoded = (NBTTagList) decoder.decode(input);
            assertEquals(original, decoded);
        }

        @Test
        void doubles() throws IOException {
            NBTTagList original = new NBTTagList("tag");
            for (int i = 0; i < 10; i++) original.add(new NBTTagDouble("", 0D));
            DataInput input = Utils.getTagInput(original);
            NBTTagList decoded = (NBTTagList) decoder.decode(input);
            assertEquals(original, decoded);
        }

        @Test
        void strings() throws IOException {
            NBTTagList original = new NBTTagList("tag");
            for (int i = 0; i < 10; i++) original.add(new NBTTagString("", "string"));
            DataInput input = Utils.getTagInput(original);
            NBTTagList decoded = (NBTTagList) decoder.decode(input);
            assertEquals(original, decoded);
        }

        @Test
        void byteArrays() throws IOException {
            NBTTagList original = new NBTTagList("tag");
            for (int i = 0; i < 10; i++) original.add(new NBTTagByteArray("", new byte[0]));
            DataInput input = Utils.getTagInput(original);
            NBTTagList decoded = (NBTTagList) decoder.decode(input);
            assertEquals(original, decoded);
        }

        @Test
        void intArrays() throws IOException {
            NBTTagList original = new NBTTagList("tag");
            for (int i = 0; i < 10; i++) original.add(new NBTTagIntArray("", new int[0]));
            DataInput input = Utils.getTagInput(original);
            NBTTagList decoded = (NBTTagList) decoder.decode(input);
            assertEquals(original, decoded);
        }

        @Test
        void lists() throws IOException {
            NBTTagList original = new NBTTagList("tag");
            for (int i = 0; i < 10; i++) {
                NBTTagList list = new NBTTagList();
                list.add(new NBTTagInt("", 0));
                original.add(list);
            }

            DataInput input = Utils.getTagInput(original);
            NBTTagList decoded = (NBTTagList) decoder.decode(input);
            assertEquals(original, decoded);
        }

        @Test
        void compounds() throws IOException {
            NBTTagList original = new NBTTagList("tag");
            for (int i = 0; i < 10; i++) {
                NBTTagCompound compound = new NBTTagCompound();
                original.add(compound);
            }

            DataInput input = Utils.getTagInput(original);
            NBTTagList decoded = (NBTTagList) decoder.decode(input);
            assertEquals(original, decoded);
        }
    }

    @Nested
    class testDecodeTagCompound {
        @Test
        void empty() throws IOException {
            NBTBase original = new NBTTagCompound("tag");
            DataInput input = Utils.getTagInput(original);
            NBTBase decoded = decoder.decode(input);
            assertEquals(original, decoded);
        }

        @Test
        void shallow() throws IOException {
            NBTTagCompound original = new NBTTagCompound("tag");
            original.set("byte", new NBTTagByte("byte", (byte) 0));
            original.set("short", new NBTTagShort("short", (short) 0));
            original.set("int", new NBTTagInt("int", 0));
            original.set("long", new NBTTagLong("long", 0L));
            original.set("float", new NBTTagFloat("float", 0F));
            original.set("double", new NBTTagDouble("double", 0D));
            original.set("ints", new NBTTagIntArray("ints", new int[0]));
            original.set("bytes", new NBTTagByteArray("bytes", new byte[0]));

            DataInput input = Utils.getTagInput(original);
            NBTBase decoded = decoder.decode(input);
            assertEquals(original, decoded);
        }

        @Test
        void nestedCompound() throws IOException {
            NBTTagCompound subtag = new NBTTagCompound("compound");
            subtag.set("byte", new NBTTagByte("byte", (byte) 0));
            subtag.set("short", new NBTTagShort("short", (short) 0));
            subtag.set("int", new NBTTagInt("int", 0));
            subtag.set("long", new NBTTagLong("long", 0L));
            subtag.set("float", new NBTTagFloat("float", 0F));
            subtag.set("double", new NBTTagDouble("double", 0D));
            subtag.set("ints", new NBTTagIntArray("ints", new int[0]));
            subtag.set("bytes", new NBTTagByteArray("bytes", new byte[0]));
            NBTTagCompound original = new NBTTagCompound("tag");
            original.set("compound", subtag);

            DataInput input = Utils.getTagInput(original);
            NBTBase decoded = decoder.decode(input);
            assertEquals(original, decoded);
        }

        @Test
        void nestedCompoundMultiple() throws IOException {
            NBTTagCompound subtag_lv3 = new NBTTagCompound("compound_lv3");
            subtag_lv3.setCompound("compound_lv4", new NBTTagCompound("compound_lv4"));
            NBTTagCompound subtag_lv2 = new NBTTagCompound("compound_lv2");
            subtag_lv2.setCompound("compound_lv3", subtag_lv3);
            NBTTagCompound subtag_lv1 = new NBTTagCompound("compound_lv1");
            subtag_lv1.setCompound("compound_lv2", subtag_lv2);
            NBTTagCompound original = new NBTTagCompound("tag");
            original.set("compound_lv1", subtag_lv1);

            DataInput input = Utils.getTagInput(original);
            NBTBase decoded = decoder.decode(input);
            assertEquals(original, decoded);
        }

        @Test
        void nestedList() throws IOException {
            NBTTagList list = new NBTTagList();
            list.add(new NBTTagByte("", (byte) 0));
            NBTTagCompound original = new NBTTagCompound("tag");
            original.set("list", list);

            DataInput input = Utils.getTagInput(original);
            NBTBase decoded = decoder.decode(input);
            assertEquals(original, decoded);
        }
    }
}
