/*
 * mxisd - Matrix Identity Server Daemon
 * Copyright (C) 2018 Kamax Sarl
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

package io.kamax.mxisd.threepid.generator.phone;

import io.kamax.matrix.json.GsonUtil;
import io.kamax.mxisd.Mxisd;
import io.kamax.mxisd.config.threepid.medium.MediumConfig;
import io.kamax.mxisd.config.threepid.medium.PhoneConfig;
import io.kamax.mxisd.config.threepid.medium.PhoneSmsTemplateConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class BuiltInPhoneGeneratorSupplier implements PhoneGeneratorSupplier {

    @Override
    public Optional<PhoneGenerator> apply(MediumConfig config, Mxisd mxisd) {
        PhoneConfig cfg = (PhoneConfig) config;
        if (StringUtils.equals(SmsNotificationGenerator.ID, cfg.getGenerator())) {
            PhoneSmsTemplateConfig genCfg = Optional.ofNullable(cfg.getGenerators().get(SmsNotificationGenerator.ID))
                    .map(json -> GsonUtil.get().fromJson(json, PhoneSmsTemplateConfig.class))
                    .orElseGet(PhoneSmsTemplateConfig::new);
            return Optional.of(new SmsNotificationGenerator(mxisd.getConfig().getMatrix(), mxisd.getConfig().getServer(), genCfg));
        }

        return Optional.empty();
    }

}
