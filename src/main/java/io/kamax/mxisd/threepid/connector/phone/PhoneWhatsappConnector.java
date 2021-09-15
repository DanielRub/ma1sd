/*
 * mxisd - Matrix Identity Server Daemon
 * Copyright (C) 2017 Kamax Sarl
 *
 * https://www.kamax.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.kamax.mxisd.threepid.connector.phone;

import com.twilio.exception.ApiException;
import io.kamax.matrix.client.MatrixPasswordCredentials;
import io.kamax.matrix.client._SyncData;
import io.kamax.matrix.client._SyncData.InvitedRoom;
import io.kamax.matrix.client._SyncData.JoinedRoom;
import io.kamax.matrix.client.regular.MatrixHttpClient;
import io.kamax.matrix.client.regular.SyncOptions;
import io.kamax.matrix.event._MatrixPersistentEvent;
import io.kamax.matrix.hs._MatrixRoom;
import io.kamax.matrix.json.event.MatrixJsonRoomMessageEvent;
import io.kamax.mxisd.Mxisd;
import static io.kamax.mxisd.MxisdStandaloneExec.mxisd;
import io.kamax.mxisd.config.threepid.connector.WhatsappConfig;
import io.kamax.mxisd.exception.InternalServerError;
import io.kamax.mxisd.exception.NotImplementedException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhoneWhatsappConnector implements PhoneConnector {

    public static final String ID = "whatsapp";

    private transient final Logger log = LoggerFactory.getLogger(PhoneWhatsappConnector.class);

    private static WhatsappConfig cfg;
    private static Mxisd currentMxisd;
    private static String domain;
    private static MatrixHttpClient matrixHttpClient;
    private static _MatrixRoom room;

    public PhoneWhatsappConnector(WhatsappConfig cfg) {
        this.cfg = cfg.build();
        log.info("Whatsapp has been initiated");
    }

    @Override
    public String getId() {
        return ID;
    }

    /**
     * public static void main(String[] args) throws MalformedURLException {
     * MatrixHttpClient matrixHttpClient = new MatrixHttpClient(new
     * URL("https://matrix.cloud4press.com")); matrixHttpClient.login(new
     * MatrixPasswordCredentials("danielrub", "apiAPI4.dan")); _MatrixRoom room
     * = matrixHttpClient.getRoom("!tVSdcpAgAyfPzXZoLE:matrix.cloud4press.com");
     *
     * System.out.println("message0 = " + readResponse(matrixHttpClient));
     * sendMessageToRoom(matrixHttpClient, room.getId(), "pm --force
     * +243814444817"); String message1 = readResponse(matrixHttpClient);
     * System.out.println("message1 = " + message1); if (message1 != null &&
     * message1.replace(")", "").endsWith(":matrix.cloud4press.com")) { String[]
     * split = message1.replace(")", "").split("/"); String roomId =
     * split[split.length - 1]; sendMessageToRoom(matrixHttpClient, roomId,
     * "Hello"); } else { String roomId = tryJoinRoom(matrixHttpClient,
     * "243814444817"); if (roomId != null) {
     * sendMessageToRoom(matrixHttpClient, roomId, "Hello"); } }
     *
     * }
     */
    private void sendMessageToRoom(MatrixHttpClient matrixHttpClient, String roomId, String message) {
        matrixHttpClient.getRoom(roomId).sendText(message);
    }

    private String readResponse(MatrixHttpClient matrixHttpClient) {
        String message = null;
        //String token = null;
        int counter = 0;
        do {
            _SyncData data = matrixHttpClient.sync(SyncOptions.build().setSince(null).get());
            JoinedRoom joinedRoom = (JoinedRoom) data.getRooms().getJoined().stream().filter(a -> a.getId().equals("!tVSdcpAgAyfPzXZoLE:matrix.cloud4press.com")).findFirst().get();
            List<_MatrixPersistentEvent> events = joinedRoom.getTimeline().getEvents();
            Optional<MatrixJsonRoomMessageEvent> messageEvent = events.stream()
                    .filter(event -> event.getType().equals("m.room.message"))
                    .map(pEvent -> new MatrixJsonRoomMessageEvent(pEvent.getJson()))
                    .filter(a -> a.getSender().getLocalPart().equals("whatsappbot"))
                    .reduce((a, b) -> b);

            if (messageEvent.isPresent()) {
                message = messageEvent.get().getBody();
            }
            counter++;

        } while (message == null && counter < 20);
        return message;
    }

    private String tryJoinRoom(MatrixHttpClient matrixHttpClient, String msisdn) {
        MatrixJsonRoomMessageEvent msg = null;
        String token = null;
        String roomId = null;
        int counter = 0;
        do {
            _SyncData data = matrixHttpClient.sync(SyncOptions.build().setSince(token).get());
            Optional<_MatrixRoom> roomFound = data.getRooms().getInvited()
                    .stream()
                    .map(a -> matrixHttpClient.getRoom(a.getId()))
                    .filter(room -> {
                        log.debug("joining.. room :" + room.getId());
                        room.join();
                        return room.getJoinedUsers().size() == 2 && room.getJoinedUsers().stream().anyMatch(user -> user.getId().getLocalPart().equals("whatsapp_" + msisdn));
                    }).findFirst();
            if (roomFound.isPresent()) {
                roomFound.get().join();
                roomId = roomFound.get().getId();
            } else {
                token = data.nextBatchToken();
                counter++;
            }
        } while (roomId == null && counter < 20);
        return roomId;
    }

    @Override
    public synchronized void send(String recipient, String content) {

        if (StringUtils.isBlank(cfg.getAdminAccountId())) {
            log.error("Whatsapp connector in not fully configured and is missing mandatory configuration values.");
            throw new NotImplementedException("Phone numbers cannot be validated at this time. Contact your administrator.");
        }
        log.info("Sending Whatsapp notification from {} to {} with {} characters", cfg.getAdminAccountId(), recipient, content.length());

        log.debug("step1");
        currentMxisd = mxisd.getMxisd();
        log.debug("step2");
        domain = currentMxisd.getConfig().getMatrix().getDomain();
        log.debug("step3");
        matrixHttpClient = new MatrixHttpClient(domain);
        log.debug("step4");
        matrixHttpClient.login(new MatrixPasswordCredentials(cfg.getAdminAccountId(), cfg.getPassword()));
        log.debug("step5");
        room = matrixHttpClient.getRoom(cfg.getBotRoomId() + ":" + domain);
        log.debug("step6");

        try {
            log.debug("Notification:" + content);
            log.debug("previous message = " + readResponse(matrixHttpClient));
            sendMessageToRoom(matrixHttpClient, room.getId(), "pm --force +" + recipient);
            String message1 = readResponse(matrixHttpClient);
            log.debug("current message = " + message1);
            if (message1 != null && message1.replace(")", "").endsWith(":" + domain)) {
                String[] split = message1.replace(")", "").split("/");
                String roomId = split[split.length - 1];
                sendMessageToRoom(matrixHttpClient, roomId, content);
            } else {
                String roomId = tryJoinRoom(matrixHttpClient, recipient);
                if (roomId != null) {
                    sendMessageToRoom(matrixHttpClient, roomId, content);
                }
            }

        } catch (ApiException e) {
            throw new InternalServerError(e);
        }
    }

}
