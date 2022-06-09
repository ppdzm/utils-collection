package io.github.ppdzm.utils.universal.cli

import io.github.ppdzm.utils.universal.config.{ConfigItem, ConfigTrait}

/**
 * Created by Stuart Alex on 2017/12/1.
 */
trait PrintConfig extends ConfigTrait {
    lazy val PRINT_ALIGNMENT: ConfigItem = new ConfigItem(config, "print.alignment", "center")
    lazy val PRINT_BORDER_FLANK: ConfigItem = new ConfigItem(config, "print.border.flank", true)
    lazy val PRINT_BORDER_TRANSVERSE: ConfigItem = new ConfigItem(config, "print.border.transverse", true)
    lazy val PRINT_BORDER_VERTICAL: ConfigItem = new ConfigItem(config, "print.border.vertical", true)
    lazy val PRINT_COVER: ConfigItem = new ConfigItem(config, "print.cover", true)
    lazy val PRINT_EXPLODE: ConfigItem = new ConfigItem(config, "print.explode", true)
    lazy val PRINT_FORMAT: ConfigItem = new ConfigItem(config, "print.format", "default")
    lazy val PRINT_JSON_PRETTY: ConfigItem = new ConfigItem(config, "print.json.pretty", false)
    lazy val PRINT_LENGTH: ConfigItem = new ConfigItem(config, "print.length", 0)
    lazy val PRINT_LINEFEED: ConfigItem = new ConfigItem(config, "print.linefeed", 0)
    lazy val PRINT_PADDING: ConfigItem = new ConfigItem(config, "print.padding", true)
    lazy val PRINT_PAGE_SIZE: ConfigItem = new ConfigItem(config, "print.pageSize", 20)
    lazy val PRINT_NULL2EMPTY: ConfigItem = new ConfigItem(config, "print.null2empty", false)
    lazy val PRINT_RENDER: ConfigItem = new ConfigItem(config, "print.render", "0;32")
    lazy val PRINT_TRUNCATE: ConfigItem = new ConfigItem(config, "print.truncate", false)
    lazy val PRINT_TRUNCATE_LENGTH: ConfigItem = new ConfigItem(config, "print.truncate.length", 17)

    def alignment: String = PRINT_ALIGNMENT.stringValue

    def cover: Boolean = PRINT_COVER.booleanValue

    def explode: Boolean = PRINT_EXPLODE.booleanValue

    def flank: Boolean = PRINT_BORDER_FLANK.booleanValue

    def format: String = PRINT_FORMAT.stringValue

    def length: Int = PRINT_LENGTH.intValue

    def linefeed: Int = PRINT_LINEFEED.intValue

    def null2Empty: Boolean = PRINT_NULL2EMPTY.booleanValue

    def padding: Boolean = PRINT_PADDING.booleanValue

    def pageSize: Int = PRINT_PAGE_SIZE.intValue

    def pretty: Boolean = PRINT_JSON_PRETTY.booleanValue

    def render: String = PRINT_RENDER.stringValue

    def truncate: Boolean = PRINT_TRUNCATE.booleanValue

    def truncateLength: Int = PRINT_TRUNCATE_LENGTH.intValue

    def transverse: Boolean = PRINT_BORDER_TRANSVERSE.booleanValue

    def vertical: Boolean = PRINT_BORDER_VERTICAL.booleanValue

}
