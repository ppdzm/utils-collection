package io.github.ppdzm.utils.hadoop.constants

import io.github.ppdzm.utils.universal.config.{ConfigItem, ConfigTrait}

trait KerberosConfigConstants extends ConfigTrait {
    lazy val KERBEROS_REALM = new ConfigItem(config, "kerberos.realm")
    lazy val KERBEROS_KDC = new ConfigItem(config, "kerberos.kdc")
    lazy val KERBEROS_KRB5CONF = new ConfigItem(config, "kerberos.krb5conf")
}
