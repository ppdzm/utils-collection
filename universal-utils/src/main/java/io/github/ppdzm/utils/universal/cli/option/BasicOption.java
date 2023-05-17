package io.github.ppdzm.utils.universal.cli.option;

import org.apache.commons.cli.Option;

/**
 * @author  Created by Stuart Alex on 2017/3/29.
 */
public interface BasicOption {

    /**
     * 得到Option
     *
     * @return Option
     */
    Option option();

    /**
     * 得到参数
     *
     * @return 参数
     */
    String argument();

    /**
     * 得到描述
     *
     * @return 描述
     */
    String description();

}
