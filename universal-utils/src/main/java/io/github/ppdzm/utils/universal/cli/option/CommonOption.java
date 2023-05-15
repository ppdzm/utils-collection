package io.github.ppdzm.utils.universal.cli.option;

import lombok.Data;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

@Data
public abstract class CommonOption implements BasicOption {
    protected final String name;
    protected final String longName;

    public CommonOption(String name, String longName) {
        this.name = name;
        this.longName = longName;
    }

    public OptionBuilder builder(boolean hasArg, String description) {
        return new OptionBuilder(new Option(this.name, this.longName, hasArg, description));
    }

    public String getOptionValue(CommandLine commandLine, String defaultValue) {
        if (this.name != null && commandLine.hasOption(this.name)) {
            return commandLine.getOptionValue(this.name, defaultValue);
        } else if (this.longName != null && commandLine.hasOption(this.longName)) {
            return commandLine.getOptionValue(this.longName, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public String[] getOptionValues(CommandLine commandLine) {
        if (this.name != null && commandLine.hasOption(this.name)) {
            return commandLine.getOptionValues(this.name);
        } else if (this.longName != null && commandLine.hasOption(this.longName)) {
            return commandLine.getOptionValues(this.longName);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "-" + this.name + "/--" + this.longName;
    }

    public static class OptionBuilder {
        private final Option option;

        public OptionBuilder(Option option) {
            this.option = option;
        }

        public OptionBuilder argName(String argName) {
            this.option.setArgName(argName);
            return this;
        }

        public OptionBuilder valueSeparator(char separator) {
            this.option.setValueSeparator(separator);
            return this;
        }

        public OptionBuilder args(int number) {
            this.option.setArgs(number);
            return this;
        }

        public Option build() {
            return this.option;
        }

    }


}