package mn.compassmate.helper;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferIndexFinder;

import java.nio.charset.StandardCharsets;

public class StringFinder implements ChannelBufferIndexFinder {

    private String string;

    public StringFinder(String string) {
        this.string = string;
    }

    @Override
    public boolean find(ChannelBuffer buffer, int guessedIndex) {

        if (buffer.writerIndex() - guessedIndex < string.length()) {
            return false;
        }

        return string.equals(buffer.toString(guessedIndex, string.length(), StandardCharsets.US_ASCII));
    }

}
