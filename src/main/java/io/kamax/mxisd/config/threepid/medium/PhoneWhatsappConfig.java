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

package io.kamax.mxisd.config.threepid.medium;

import io.kamax.mxisd.threepid.connector.phone.PhoneWhatsappConnector;
import io.kamax.mxisd.threepid.generator.phone.SmsNotificationGenerator;

public class PhoneWhatsappConfig extends MediumConfig {

    public PhoneWhatsappConfig() {
        setConnector(PhoneWhatsappConnector.ID);
        setGenerator(SmsNotificationGenerator.ID);
    }

}
