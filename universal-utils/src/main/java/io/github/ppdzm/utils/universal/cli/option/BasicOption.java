package io.github.ppdzm.utils.universal.cli.option;

import org.apache.commons.cli.Option;

public interface BasicOption {

    Option option();

    String argument();

    String description();

}
