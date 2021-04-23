package io.github.ppdzm.utils.universal.cli

import org.apache.commons.cli

/**
 * Created by Stuart Alex on 2017/9/6.
 */
object ParameterOption extends CommonOption("p", "parameter") {
    private val hasArg = true
    private val valueSeparator = '='
    private val argumentNumber = 2

    def option: cli.Option = this.createOption(this.hasArg, this.description).argName(this.argument).valueSeparator(this.valueSeparator).args(this.argumentNumber)

    private def argument = MessageGenerator.generate("argument-parameter")

    private def description = MessageGenerator.generate("description-parameter")
}