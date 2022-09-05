package pl.kosma.worldnamepacket;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class WorldNamePacket {
    private static final byte FORGE_PACKET_DISCRIMINATOR = 0;
    private static final byte VOXELMAP_MAGIC_NUMBER = 42;
    private static final byte VOXELMAP_FABRIC_REQUEST_PACKET[] = new byte[]{ 0, 0, 0, 42 };
    public static final String CHANNEL_NAME_VOXELMAP = "worldinfo:world_id";
    public static final String CHANNEL_NAME_XAEROMAP = "xaeroworldmap:main";
    private static final String HEX_DIGITS = "0123456789abcdef";

    public static byte[] formatResponsePacket(byte[] requestBytes, String worldName) {
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        if (requestBytes == null) {
            int hash = worldName.hashCode();
            responseStream.write(0); // LevelMapProperties marker
            responseStream.write((hash & 0xFF000000) >> 24); // id (integer)
            responseStream.write((hash & 0x00FF0000) >> 16); // ^
            responseStream.write((hash & 0x0000FF00) >> 8);  // ^
            responseStream.write((hash & 0x000000FF));          // ^
        } else {
            /**
             * Workaround for a bug in VoxelMap for Fabric.
             *
             * The correct request-response format for well-behaved mods is:
             * Request: 00 42 00
             * Response: 00 42 <length> <world name>
             *
             * VoxelMap for Fabric didn't get the memo, it managed to botch both the request
             * and the response. The VoxelMap format is:
             * Request: 00 00 00 42
             * Response: 42 <length> <world name>
             *
             * Hell is other people's code.
             */
            boolean useForgeDiscriminator = !Arrays.equals(requestBytes, VOXELMAP_FABRIC_REQUEST_PACKET);
            byte[] worldNameBytes = worldName.getBytes(StandardCharsets.UTF_8);
            if (useForgeDiscriminator)
                responseStream.write(FORGE_PACKET_DISCRIMINATOR);
            responseStream.write(VOXELMAP_MAGIC_NUMBER);
            responseStream.write(worldNameBytes.length);
            responseStream.write(worldNameBytes, 0, worldNameBytes.length);
        }
        return responseStream.toByteArray();
    }

    /**
     * Helper function for debugging packet-related issues.
     */
    public static String byteArrayToHexString(byte[] data) {
        StringBuilder buf = new StringBuilder();

        for (int i = 0; i != data.length; i++) {
            int v = data[i] & 0xff;

            buf.append(HEX_DIGITS.charAt(v >> 4));
            buf.append(HEX_DIGITS.charAt(v & 0xf));
            buf.append(" ");
        }

        return buf.toString();
    }
}
