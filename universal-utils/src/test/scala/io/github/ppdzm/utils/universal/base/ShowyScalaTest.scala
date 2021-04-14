package io.github.ppdzm.utils.universal.base

import org.scalatest.{FlatSpec, GivenWhenThen}

import scala.collection.mutable

/**
 * Created by Stuart Alex on 2017/4/5.
 */
class ShowyScalaTest extends FlatSpec with GivenWhenThen {

    "A mutable Set" should "allow an element to be added" in {
        Given("an empty mutable Set")
        val set = mutable.Set.empty[String]

        When("an element is added")
        set += "clarity"

        Then("the Set should have size 1")
        assert(set.size === 1)

        And("the Set should contain the added element")
        assert(set.contains("clarity"))

        info("That's all folks!")
    }

    ignore should "produce NoSuchElementException when head is invoked" in {
        assertThrows[NoSuchElementException] {
            Set.empty.head
        }
    }

    markup {
        """

Mutable Set
———--

A set is a collection that contains no duplicate elements.

To implement a concrete mutable set, you need to provide implementations
of the following methods:

    def contains(elem: A): Boolean
    def iterator: Iterator[A]
    def += (elem: A): this.type
    def -= (elem: A): this.type

If you wish that methods like `take`,
`drop`, `filter` return the same kind of set,
you should also override:

    def empty: This

It is also good idea to override methods `foreach` and
`size` for efficiency.

    """
    }

    "A mutable Set" should "allow an element to be added" in {
        Given("an empty mutable Set")
        val set = mutable.Set.empty[String]

        When("an element is added")
        set += "clarity"

        Then("the Set should have size 1")
        assert(set.size === 1)

        And("the Set should contain the added element")
        assert(set.contains("clarity"))

        markup("This test finished with a **bold** statement!")
    }

}