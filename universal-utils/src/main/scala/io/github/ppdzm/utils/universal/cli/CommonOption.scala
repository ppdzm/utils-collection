package io.github.ppdzm.utils.universal.cli

import org.apache.commons.cli.{CommandLine, Option}

/**
 * Created by Stuart Alex on 2017/9/6.
 */
case class CommonOption(name: String, longName: String) {

    def createOption(hasArg: Boolean, description: String) = new Option(this.name, this.longName, hasArg, description)

    def getOptionValue(commandLine: CommandLine, defaultValue: String = null): String = {
        if (this.name != null && commandLine.hasOption(this.name))
            commandLine.getOptionValue(this.name, defaultValue)
        else if (this.longName != null && commandLine.hasOption(this.longName))
            commandLine.getOptionValue(this.longName, defaultValue)
        else
            defaultValue
    }

    def getOptionValues(commandLine: CommandLine): Array[String] = {
        if (this.name != null && commandLine.hasOption(this.name))
            commandLine.getOptionValues(this.name)
        else if (this.longName != null && commandLine.hasOption(this.longName))
            commandLine.getOptionValues(this.longName)
        else
            null
    }

    override def toString: String = s"-${this.name}/--${this.longName}"

    implicit class OptionBuilder(option: Option) {

        def argName(argName: String): Option = {
            this.option.setArgName(argName)
            this.option
        }

        def valueSeparator(separator: Char): Option = {
            this.option.setValueSeparator(separator)
            this.option
        }

        def args(number: Int): Option = {
            this.option.setArgs(number)
            this.option
        }

    }

}