package io.github.ppdzm.utils.universal.core

import CoreConstants._

/**
 * Created by Stuart Alex on 2021/4/8.
 */
object SystemProperties {
    def language: String = System.getProperty(programLanguageKey, "en")

    def configFileName: String = System.getProperty(profilePrefixKey, System.getProperty(profilePrefixKey.replace(".", "_"), defaultPrefixValue))

    def configFileExtension: String = System.getProperty(profileExtensionKey, System.getProperty(profileExtensionKey.replace(".", "_"), defaultExtensionValue))
}
