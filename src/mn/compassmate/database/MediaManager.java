package mn.compassmate.database;

import org.jboss.netty.buffer.ChannelBuffer;
import mn.compassmate.Config;
import mn.compassmate.helper.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MediaManager {

    private String path;

    public MediaManager(Config config) {
        path = config.getString("media.path");
    }

    private File createFile(String uniqueId, String name) throws IOException {
        Path filePath = Paths.get(path, uniqueId, name);
        Path directoryPath = filePath.getParent();
        if (directoryPath != null) {
            Files.createDirectories(directoryPath);
        }
        return filePath.toFile();
    }

    public String writeFile(String uniqueId, ChannelBuffer buf, String extension) {
        int size = buf.readableBytes();
        String name = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + "." + extension;
        try (FileOutputStream output = new FileOutputStream(createFile(uniqueId, name));
                FileChannel fileChannel = output.getChannel()) {
            ByteBuffer byteBuffer = buf.toByteBuffer();
            int written = 0;
            while (written < size) {
                written += fileChannel.write(byteBuffer);
            }
            fileChannel.force(false);
            return name;
        } catch (IOException e) {
            Log.warning(e);
        }
        return null;
    }

}
