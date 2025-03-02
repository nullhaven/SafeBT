package one.nullhaven.safebt;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SafeNbtDecoderMaliciousPayloadTest {
    SafeNbtDecoder decoder;

    @BeforeEach
    void setUp() {
        decoder = new SafeNbtDecoder();
    }

    private static @NotNull Stream<Arguments> testThrowsOnMaliciousPayload() throws IOException {
        Path directory = Paths.get("src/test/resources/nbt");

        List<Arguments> nbtFiles;
        try (Stream<Path> stream = Files.list(directory)) {
            nbtFiles = stream
                    .filter(path -> path.getFileName().toString().endsWith(".nbt.gzip"))
                    .map(Arguments::of)
                    .collect(Collectors.toList());
        }

        return nbtFiles.stream();
    }

    @ParameterizedTest
    @MethodSource
    void testThrowsOnMaliciousPayload(Path path) {
        assertThrows(UnsafePayloadException.class, () -> {
            try (DataInputStream input = Utils.newGzippedDataInput(path)) {
                decoder.decode(input);
            }
        });
    }
}
