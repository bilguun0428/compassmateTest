package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.BcdUtil;
import mn.compassmate.helper.BitUtil;
import mn.compassmate.helper.DateBuilder;
import mn.compassmate.helper.UnitsConverter;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Date;

public class Vt200ProtocolDecoder extends BaseProtocolDecoder {

    public Vt200ProtocolDecoder(Vt200Protocol protocol) {
        super(protocol);
    }

    private static double decodeCoordinate(int value) {
        int degrees = value / 1000000;
        int minutes = value % 1000000;
        return degrees + minutes * 0.0001 / 60;
    }

    protected Date decodeDate(ChannelBuffer buf) {
        DateBuilder dateBuilder = new DateBuilder()
                .setDateReverse(BcdUtil.readInteger(buf, 2), BcdUtil.readInteger(buf, 2), BcdUtil.readInteger(buf, 2))
                .setTime(BcdUtil.readInteger(buf, 2), BcdUtil.readInteger(buf, 2), BcdUtil.readInteger(buf, 2));
        return dateBuilder.getDate();
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;

        buf.skipBytes(1); // header

        String id = ChannelBuffers.hexDump(buf.readBytes(6));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, id);
        if (deviceSession == null) {
            return null;
        }

        int type = buf.readUnsignedShort();
        buf.readUnsignedShort(); // length

        if (type == 0x2086 || type == 0x2084 || type == 0x2082) {

            Position position = new Position();
            position.setProtocol(getProtocolName());
            position.setDeviceId(deviceSession.getDeviceId());

            buf.readUnsignedByte(); // data type
            buf.readUnsignedShort(); // trip id

            position.setTime(decodeDate(buf));

            position.setLatitude(decodeCoordinate(BcdUtil.readInteger(buf, 8)));
            position.setLongitude(decodeCoordinate(BcdUtil.readInteger(buf, 9)));

            int flags = buf.readUnsignedByte();
            position.setValid(BitUtil.check(flags, 0));
            if (!BitUtil.check(flags, 1)) {
                position.setLatitude(-position.getLatitude());
            }
            if (!BitUtil.check(flags, 1)) {
                position.setLongitude(-position.getLongitude());
            }

            position.setSpeed(UnitsConverter.knotsFromKph(buf.readUnsignedByte()));
            position.setCourse(buf.readUnsignedByte() * 2);

            position.set(Position.KEY_SATELLITES, buf.readUnsignedByte());
            position.set(Position.KEY_RSSI, buf.readUnsignedByte());
            position.set(Position.KEY_ODOMETER, buf.readUnsignedInt() * 1000);
            position.set(Position.KEY_STATUS, buf.readUnsignedInt());

            // additional data

            return position;

        } else if (type == 0x3088) {

            Position position = new Position();
            position.setProtocol(getProtocolName());
            position.setDeviceId(deviceSession.getDeviceId());

            getLastLocation(position, null);

            buf.readUnsignedShort(); // trip id
            buf.skipBytes(8); // imei
            buf.skipBytes(8); // imsi

            position.set("tripStart", decodeDate(buf).getTime());
            position.set("tripEnd", decodeDate(buf).getTime());
            position.set("drivingTime", buf.readUnsignedShort());

            position.set(Position.KEY_FUEL_CONSUMPTION, buf.readUnsignedInt());
            position.set(Position.KEY_ODOMETER_TRIP, buf.readUnsignedInt());

            position.set("maxSpeed", UnitsConverter.knotsFromKph(buf.readUnsignedByte()));
            position.set("maxRpm", buf.readUnsignedShort());
            position.set("maxTemp", buf.readUnsignedByte() - 40);
            position.set("hardAccelerationCount", buf.readUnsignedByte());
            position.set("hardBreakingCount", buf.readUnsignedByte());

            for (String speedType : Arrays.asList("over", "high", "normal", "low")) {
                position.set(speedType + "SpeedTime", buf.readUnsignedShort());
                position.set(speedType + "SpeedDistance", buf.readUnsignedInt());
                position.set(speedType + "SpeedFuel", buf.readUnsignedInt());
            }

            position.set("idleTime", buf.readUnsignedShort());
            position.set("idleFuel", buf.readUnsignedInt());

            position.set("hardCorneringCount", buf.readUnsignedByte());
            position.set("overspeedCount", buf.readUnsignedByte());
            position.set("overheatCount", buf.readUnsignedShort());
            position.set("laneChangeCount", buf.readUnsignedByte());
            position.set("emergencyRefueling", buf.readUnsignedByte());

            return position;

        }

        return null;
    }

}
