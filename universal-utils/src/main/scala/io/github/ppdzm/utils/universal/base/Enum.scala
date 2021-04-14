package io.github.ppdzm.utils.universal.base

trait Enum extends Enumeration {

    def apply(value: String): Value = {
        this.values.find(_.toString == value)
            .getOrElse(throw new IllegalArgumentException(s"unsupported $value in ${this.getClass.getSimpleName}"))
    }

}
