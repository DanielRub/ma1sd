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

    private String matrixAccountId = "";

    public String getMatrixAccountId() {
        return matrixAccountId;
    }

    public void setMatrixAccountId(String matrixAccountId) {
        this.matrixAccountId = matrixAccountId;
    }

    public WhatsappConfig build() {
        log.info("--- Whatsapp connector config ---");
        log.info("Account : {}", getMatrixAccountId());
        return this;
    }

}
