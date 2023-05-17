package io.github.ppdzm.utils.universal.cli.option;

import io.github.ppdzm.utils.universal.cli.MessageGenerator;
import org.apache.commons.cli.Option;

/**
 * @author Created by Stuart Alex on 2017/9/22.
 */
public class ParameterOption extends AbstractOption {

    public ParameterOption() {
        super("p", "parameter");
    }

    @Override
    public Option option() {
        int argumentNumber = 2;
        char valueSeparator = '=';
        return builder(true, description()).argName(argument()).valueSeparator(valueSeparator).args(argumentNumber).build();
    }

    @Override
    public String argument() {
        return MessageGenerator.generate("argument-parameter");
    }

    @Override
    public String description() {
        return MessageGenerator.generate("description-parameter");
    }

}
