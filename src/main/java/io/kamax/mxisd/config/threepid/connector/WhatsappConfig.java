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
package io.kamax.mxisd.config.threepid.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhatsappConfig {

    private transient final Logger log = LoggerFactory.getLogger(WhatsappConfig.class);

    private String adminAccountId = "";
    private String password = "";
    private String botRoomId = "";

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBotRoomId() {
        return botRoomId;
    }

    public void setBotRoomId(String botRoomId) {
        this.botRoomId = botRoomId;
    }

    public String getAdminAccountId() {
        return adminAccountId;
    }

    public void setAdminAccountId(String adminAccountId) {
        this.adminAccountId = adminAccountId;
    }

    public WhatsappConfig build() {
        log.info("--- Whatsapp connector config ---");
        log.info("Account Id : {}", getAdminAccountId());
        log.info("Bot room id : {}", getBotRoomId());

        return this;
    }

}
