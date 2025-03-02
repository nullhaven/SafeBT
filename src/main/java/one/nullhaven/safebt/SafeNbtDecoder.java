package one.nullhaven.safebt;

import net.minecraft.server.v1_5_R3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.IOException;
import java.util.Stack;

/**
 * Safe implementation of an NBT decoder with strictly enforced NBT structure
 * depth, size, allowed root tag type, etc.
 *
 * @author dreamscached
 * @version 1.0.0
 * @since 1.0.0
 */
public class SafeNbtDecoder implements NbtDecoder {
    /**
     * Maximum depth (within either {@link NBTTagCompound} or {@link NBTTagList}, counted from root
     * tag) this decoder will traverse and decode NBT payload.
     */
    private int maxTraversalDepth = 5;

    /**
     * Maximum length (count of key-value pairs) of {@link NBTTagCompound} this decoder
     * will traverse and decode.
     */
    private int maxCompoundSize = 20;

    /**
     * Maximum length of {@link NBTTagList} this decoder will traverse and decode.
     */
    private int maxListSize = 100;

    /**
     * Maximum length of contiguous structures (e.g. {@link NBTTagString}, {@link NBTTagIntArray} and
     * {@link NBTTagByteArray}, but not including tag key strings, see {@link SafeNbtDecoder#maxKeyLength})
     * this decoder will traverse and decode.
     */
    private int maxContiguousSize = 100;

    /**
     * Maximum length of NBT tag key strings this decoder will allow.
     */
    private int maxKeyLength = 32;

    /**
     * Maximum amount of new instances of {@link NBTBase} allocated this decoder
     * will allow.
     */
    private int maxAllocatedTags = 512;

    /**
     * Whether it is mandatory that the root tag is {@link NBTTagCompound} with key {@code ""} (empty string).<br>
     * <b>Note:</b> naturally every NBT file contains a single compound root tag.
     * Notchian servers/clients will never produce NBT structures without a compound
     * root tag or a root tag with a non-empty tag key.
     */
    private boolean allowAnyRootTag = false;

    /**
     * Whether {@link NBTTagEnd} is allowed to be used as a value.<br>
     * <b>Note:</b> naturally {@link NBTTagEnd} can only occur as part of {@link NBTTagCompound}
     * structure. Notchian servers/clients will never produce 'stray' {@link NBTTagEnd} values.
     */
    private boolean allowStrayEndTags = false;

    private @Nullable Stack<NBTBase> stack;
    private int allocatedTags = 0;

    public void setMaxTraversalDepth(int maxTraversalDepth) {
        this.maxTraversalDepth = maxTraversalDepth;
    }

    public void setMaxCompoundSize(int maxCompoundSize) {
        this.maxCompoundSize = maxCompoundSize;
    }

    public void setMaxListSize(int maxListSize) {
        this.maxListSize = maxListSize;
    }

    public void setMaxContiguousSize(int maxContiguousSize) {
        this.maxContiguousSize = maxContiguousSize;
    }

    public void setMaxKeyLength(int maxKeyLength) {
        this.maxKeyLength = maxKeyLength;
    }

    public void setMaxAllocatedTags(int maxAllocatedTags) {
        this.maxAllocatedTags = maxAllocatedTags;
    }

    public void setAllowAnyRootTag(boolean allowAnyRootTag) {
        this.allowAnyRootTag = allowAnyRootTag;
    }

    public void setAllowStrayEndTags(boolean allowStrayEndTags) {
        this.allowStrayEndTags = allowStrayEndTags;
    }

    private boolean hasNoRoot() {
        return this.stack == null;
    }

    private int getStructureDepth() {
        return this.stack != null ? this.stack.size() : 0;
    }

    private boolean isInCompound() {
        return this.stack != null && this.stack.peek().getTypeId() == 10;
    }

    /**
     * Decodes NBT payload stream, performing sanity checks in-place.
     *
     * @param in {@link DataInput} stream with incoming payload
     * @return decoded {@link NBTBase} implementation
     * @throws IOException            in case of an unexpected input/output error
     * @throws UnsafePayloadException in case of malicious NBT payload
     */
    @Override
    public @NotNull NBTBase decode(@NotNull DataInput in) throws IOException {
        return this.decodeNextTag(in);
    }

    private @NotNull NBTBase decodeNextTag(@NotNull DataInput in) throws IOException {
        NBTBase tag = this.allocateNextTag(in);
        byte tagType = tag.getTypeId();
        if (tagType == 0) return tag;

        if (tagType == 9 || tagType == 10) {
            this.checkStructureDepth();
            if (this.hasNoRoot()) this.stack = new Stack<>();
            this.stack.push(tag);

            this.decodeStructure(tag, in);
            this.stack.pop();
            return tag;
        }

        this.decodeScalarOrArray(tag, in);
        return tag;
    }

    private @NotNull NBTBase decodeNextItem(byte listType, @NotNull DataInput in) throws IOException {
        NBTBase tag = this.allocateNextItem(listType);
        if (tag.getTypeId() == 9 || tag.getTypeId() == 10) {
            this.checkStructureDepth();
            if (this.hasNoRoot()) this.stack = new Stack<>();
            this.stack.push(tag);

            this.decodeStructure(tag, in);
            this.stack.pop();
            return tag;
        }

        this.decodeScalarOrArray(tag, in);
        return tag;
    }

    private void decodeStructure(@NotNull NBTBase tag, @NotNull DataInput in) throws IOException {
        switch (tag.getTypeId()) {
            case 9:
                this.decodeList((NBTTagList) tag, in);
                break;
            case 10:
                this.decodeCompound((NBTTagCompound) tag, in);
                break;
            default:
                throw new AssertionError(String.format("Unexpected tag in decodeStructure(...): %s", tag));
        }
    }

    private void decodeList(@NotNull NBTTagList tag, @NotNull DataInput in) throws IOException {
        byte listType = in.readByte();
        this.checkTagType(listType);
        this.checkListType(listType);

        int size = in.readInt();
        this.checkListSize(size);
        for (int i = 0; i < size; i++)
            tag.add(this.decodeNextItem(listType, in));
    }

    private void decodeCompound(@NotNull NBTTagCompound tag, @NotNull DataInput in) throws IOException {
        int size = 0;
        NBTBase nextTag;
        while ((nextTag = this.decodeNextTag(in)).getTypeId() != 0) {
            this.checkCompoundSize(size);
            tag.set(nextTag.getName(), nextTag);
        }
    }

    private void decodeScalarOrArray(@NotNull NBTBase tag, @NotNull DataInput in) throws IOException {
        switch (tag.getTypeId()) {
            case 0:
                break;
            case 1:
                ((NBTTagByte) tag).data = in.readByte();
                break;
            case 2:
                ((NBTTagShort) tag).data = in.readShort();
                break;
            case 3:
                ((NBTTagInt) tag).data = in.readInt();
                break;
            case 4:
                ((NBTTagLong) tag).data = in.readLong();
                break;
            case 5:
                ((NBTTagFloat) tag).data = in.readFloat();
                break;
            case 6:
                ((NBTTagDouble) tag).data = in.readDouble();
                break;
            case 7:
                this.decodeByteArray((NBTTagByteArray) tag, in);
                break;
            case 8:
                ((NBTTagString) tag).data = in.readUTF();
                this.checkStringSize(((NBTTagString) tag).data);
                break;
            case 11:
                this.decodeIntArray((NBTTagIntArray) tag, in);
                break;
            default:
                throw new AssertionError(String.format("Unexpected tag in decodeScalarOrArray(...): %s", tag));
        }
    }

    private void decodeByteArray(@NotNull NBTTagByteArray tag, @NotNull DataInput in) throws IOException {
        int size = in.readInt();
        this.checkArraySize(size);
        tag.data = new byte[size];
        in.readFully(tag.data);
    }

    private void decodeIntArray(@NotNull NBTTagIntArray tag, @NotNull DataInput in) throws IOException {
        int size = in.readInt();
        this.checkArraySize(size);
        tag.data = new int[size];
        for (int i = 0; i < size; i++)
            tag.data[i] = in.readInt();
    }

    private @NotNull NBTBase allocateNextTag(@NotNull DataInput in) throws IOException {
        byte tagType = in.readByte();
        this.checkTagType(tagType); // Verify the tag type is in acceptable range
        this.checkEndTag(tagType); // Prevent allocation of NBTTagEnd if not in compound
        this.checkRootTag(tagType); // Prevent allocation of a root tag if not compound
        if (tagType == 0) return new NBTTagEnd();

        String tagKey = this.nextTagKey(in);
        this.checkRootTagKey(tagKey); // Prevent allocation with non-empty root keys
        return this.allocateTag(tagType, tagKey);
    }

    private @NotNull NBTBase allocateNextItem(byte listType) {
        this.checkEndTag(listType);
        return this.allocateTag(listType, "");
    }

    private @NotNull String nextTagKey(@NotNull DataInput in) throws IOException {
        String value = in.readUTF();
        if (value.length() > this.maxKeyLength) {
            throw new UnsafePayloadException(String.format("Unexpected tag key length (%d, max %d.)", value.length(), this.maxKeyLength));
        }
        return value;
    }

    private void checkTagType(byte tagType) {
        if (!(tagType >= 0 && tagType <= 11)) {
            throw new UnsafePayloadException(String.format("Unexpected tag type (%02X.)", tagType));
        }
    }

    private void checkRootTag(byte tagType) {
        if (tagType != 10) {
            if (this.hasNoRoot() && !this.allowAnyRootTag) {
                throw new UnsafePayloadException(String.format("Unexpected root tag ID %02X (expected 0A.)", tagType));
            }
        }
    }

    private void checkRootTagKey(String key) {
        if (this.hasNoRoot()) {
            if (!key.isEmpty() && !this.allowAnyRootTag) {
                throw new UnsafePayloadException(String.format("Unexpected root tag key (%s.)", key));
            }
        }
    }

    private void checkEndTag(byte tagType) {
        if (tagType == 0 && !this.isInCompound() && !this.allowStrayEndTags) {
            throw new UnsafePayloadException("Unexpected end tag outside compound.");
        }
    }

    private void checkListType(byte listType) {
        if (listType == 0 && !this.allowStrayEndTags) {
            throw new UnsafePayloadException("Unexpected list of end tags.");
        }
    }

    private void checkStructureDepth() {
        int current = this.getStructureDepth();
        if (current + 1 > this.maxTraversalDepth) {
            throw new UnsafePayloadException(String.format("Maximum allowed struct depth exceeded (%d, max %d.)", current + 1, this.maxTraversalDepth));
        }
    }

    private void checkAllocatedTags() {
        if (this.allocatedTags + 1 > this.maxAllocatedTags) {
            throw new UnsafePayloadException(String.format("Maximum allocated tags count exceeded (%d, max %d.)", this.allocatedTags + 1, this.maxAllocatedTags));
        }
    }

    private void checkStringSize(@NotNull String str) {
        if (str.length() > this.maxContiguousSize) {
            throw new UnsafePayloadException(String.format("Maximum allowed string size exceeded (%d, max %d.)", str.length(), this.maxContiguousSize));
        }
    }

    private void checkArraySize(int size) {
        if (size > this.maxContiguousSize) {
            throw new UnsafePayloadException(String.format("Maximum allowed array size exceeded (%d, max %d.)", size, this.maxContiguousSize));
        }
    }

    private void checkCompoundSize(int size) {
        if (size + 1 > this.maxCompoundSize) {
            throw new UnsafePayloadException(String.format("Maximum allowed compound size exceeded (%d, max %d.)", size, this.maxCompoundSize));
        }
    }

    private void checkListSize(int size) {
        if (size > this.maxListSize) {
            throw new UnsafePayloadException(String.format("Maximum allowed list size exceeded (%d, max %d.)", size, this.maxListSize));
        }
    }

    private NBTBase allocateTag(byte tagType, @NotNull String key) {
        this.checkAllocatedTags();
        NBTBase newTag;

        switch (tagType) {
            case 0:
                newTag = new NBTTagEnd();
                break;
            case 1:
                newTag = new NBTTagByte(key);
                break;
            case 2:
                newTag = new NBTTagShort(key);
                break;
            case 3:
                newTag = new NBTTagInt(key);
                break;
            case 4:
                newTag = new NBTTagLong(key);
                break;
            case 5:
                newTag = new NBTTagFloat(key);
                break;
            case 6:
                newTag = new NBTTagDouble(key);
                break;
            case 7:
                newTag = new NBTTagByteArray(key);
                break;
            case 8:
                newTag = new NBTTagString(key);
                break;
            case 9:
                newTag = new NBTTagList(key);
                break;
            case 10:
                newTag = new NBTTagCompound(key);
                break;
            case 11:
                newTag = new NBTTagIntArray(key);
                break;
            default:
                throw new UnsafePayloadException(String.format("Unexpected tag type %02X", tagType));
        }

        this.allocatedTags++;
        return newTag;
    }
}
