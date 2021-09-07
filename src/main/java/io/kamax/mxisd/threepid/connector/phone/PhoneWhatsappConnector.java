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
import io.kamax.matrix.MatrixID;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix.client.MatrixClientContext;
import io.kamax.matrix.client.MatrixHttpRequest;
import io.kamax.matrix.client.MatrixHttpRoom;
import io.kamax.matrix.client.MatrixHttpUser;
import io.kamax.matrix.client.MatrixPasswordCredentials;
import io.kamax.matrix.client._MatrixClient;
import io.kamax.matrix.client._SyncData;
import io.kamax.matrix.client.as.MatrixApplicationServiceClient;
import io.kamax.matrix.client.regular.MatrixHttpClient;
import io.kamax.matrix.client.regular.SyncDataJson;
import io.kamax.matrix.hs.MatrixHomeserver;
import io.kamax.matrix.hs._MatrixRoom;
import io.kamax.matrix.room.RoomCreationOptions;
import io.kamax.matrix.room._RoomCreationOptions;
import io.kamax.mxisd.Mxisd;
import static io.kamax.mxisd.MxisdStandaloneExec.mxisd;
import io.kamax.mxisd.auth.UserAuthResult;
import io.kamax.mxisd.config.threepid.connector.WhatsappConfig;
import io.kamax.mxisd.exception.InternalServerError;
import io.kamax.mxisd.exception.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhoneWhatsappConnector implements PhoneConnector {

    public static final String ID = "whatsapp";

    private transient final Logger log = LoggerFactory.getLogger(PhoneWhatsappConnector.class);

    private WhatsappConfig cfg;

    public PhoneWhatsappConnector(WhatsappConfig cfg) {
        this.cfg = cfg.build();
        log.info("Whatsapp has been initiated");
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void send(String recipient, String content) {
        if (StringUtils.isBlank(cfg.getMatrixAccountId())) {
            log.error("Whatsapp connector in not fully configured and is missing mandatory configuration values.");
            throw new NotImplementedException("Phone numbers cannot be validated at this time. Contact your administrator.");
        }

        recipient = "+" + recipient;
        log.info("Sending Whatsapp notification from {} to {} with {} characters", cfg.getMatrixAccountId(), recipient, content.length());
        try {
            log.info("Notification:" + content);
            //now we can send the whatsapp message
            log.info("step1:");
            Mxisd currentMxisd = mxisd.getMxisd();
            /*boolean success = currentMxisd.getAuth().authenticate("@danielrub:matrix.cloud4press.com", "apiAPI4.dan").isSuccess();
            log.info("auth success==" + success);
            MatrixClientContext mxContext = new MatrixClientContext();
            log.info("step2:");
            log.info("--step--" + currentMxisd.getConfig().getMatrix().getDomain());
            mxContext.setDomain(currentMxisd.getConfig().getMatrix().getDomain());
            log.info("step2.1:");
            mxContext.setHsBaseUrl(currentMxisd.getConfig().getAppsvc().getEndpoint().getToHS().getUrl());
            log.info("step2.2:");
            mxContext.setInitialDeviceName("test");
            log.info("step2.3:");
            mxContext.setUserWithLocalpart("danielrub");
            log.info("step2.3:");
            mxContext.setToken(currentMxisd.getConfig().getExec().getToken().toString());
            log.info("step3:");
            mxContext.setHsBaseUrl(currentMxisd.getConfig().getAppsvc().getEndpoint().getToHS().getUrl());
            MatrixApplicationServiceClient client = new MatrixApplicationServiceClient(mxContext);
            client.login(new MatrixPasswordCredentials("danielrub", "apiAPI4.dan"));
            log.info("step4:");
            log.info("getAccessToken is present==" + client.getAccessToken().isPresent());
            log.info("token2==" + client.getContext().getToken());
            log.info("user==" + client.getContext().getUser().get());
            _MatrixClient user = client.getUser("whatsappbot");
            log.info("user==" + user.toString());
            log.info("user device==" + user.getDeviceId());
            log.info("user rooms==" + user.getWhoAmI());
            log.info("step5:");
            _MatrixRoom matrixRoom = user.createRoom(RoomCreationOptions.build()
                    .setAliasName("pwd")
                    .setDirect(true)
                    .setName("pwd")
                    .get());
            log.info("step6:");
            matrixRoom.join();
            log.info("step7:");
            String sendText = matrixRoom.sendText("pm --force " + recipient);
            log.info("sendText==" + sendText);
            //Message.creator(new PhoneNumber("+" + recipient), new PhoneNumber(cfg.getNumber()), content).create();
           */ MatrixHttpClient matrixHttpClient = new MatrixHttpClient("matrix.cloud4press.com");
            matrixHttpClient.login(new MatrixPasswordCredentials("danielrub", "apiAPI4.dan"));
            log.info("joined rooms"+matrixHttpClient.getJoinedRooms());
            
                
        } catch (ApiException e) {
            throw new InternalServerError(e);
        }
    }

}