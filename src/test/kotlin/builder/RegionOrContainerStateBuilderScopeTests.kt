package builder

import dev.timray.kotomata.builder.RegionOrCompositeStateBuilderScopeImpl
import dev.timray.kotomata.builder.vertices.AtomicStateDeclaration
import dev.timray.kotomata.builder.vertices.ChoiceDeclaration
import dev.timray.kotomata.builder.vertices.ForkDeclaration
import dev.timray.kotomata.builder.vertices.StateDeclaration
import dev.timray.kotomata.model.Event
import dev.timray.kotomata.model.RegionAtom
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async

class RegionOrContainerStateBuilderScopeTests : ShouldSpec({
    should("add state declaration to list") {
        val scope = scopeFactory()

        with(scope) {
            state(TestState)
        }

        val declaration = scope
            .vertexDeclarations
            .filterIsInstance<StateDeclaration<TestState, TestContext>>()
            .singleOrNull()

        declaration shouldNotBeNull {
            id shouldBe TestState
            containingStateId shouldBe TestContainingState
        }
    }

    should("add state state declaration with actions") {
        val scope = scopeFactory()

        with(scope) {
            state<TestState, TestContext>(TestState) {
                entry { ctx -> ctx }
                work { ctx -> async { ctx } }
                exit { ctx -> ctx }
            }
        }

        val declaration = scope
            .vertexDeclarations
            .filterIsInstance<StateDeclaration<TestState, TestContext>>()
            .singleOrNull()

        declaration shouldNotBeNull {
            id shouldBe TestState
            containingStateId shouldBe TestContainingState
            entryAction.shouldNotBeNull()
            doAction.shouldNotBeNull()
            exitAction.shouldNotBeNull()
        }
    }

    should("add state with transitions") {
        val scope = scopeFactory()

        with(scope) {
            state<TestState, TestContext>(TestState) {
                on(TestEvent::class)
                    .target(TestTargetState)
            }
        }

        val declaration = scope
            .vertexDeclarations
            .filterIsInstance<StateDeclaration<TestState, TestContext>>()
            .singleOrNull()

        declaration shouldNotBeNull {
            id shouldBe TestState
            containingStateId shouldBe TestContainingState
            transitions.shouldNotBeEmpty()
        }
    }

    should("add choice pseudostate declaration") {
        val scope = scopeFactory()

        with(scope) {
            choice(TestChoice) {
                select(TestChoiceSelection)
            }
        }
        val declaration = scope
            .vertexDeclarations
            .filterIsInstance<ChoiceDeclaration<TestChoice, TestContext>>()
            .singleOrNull()

        declaration shouldNotBeNull {
            id shouldBe TestChoice
            containingStateId shouldBe TestContainingState
            selector.shouldNotBeNull()
        }
    }


    should("add fork transition") {
        val scope = scopeFactory()
        val region1 = RegionAtom.create()
        val region2 = RegionAtom.create()
        val region3 = RegionAtom.create()

        with(scope) {
            fork(TestFork) {
                target(region = region1, TestForkTarget1)
                target(region = region2, TestForkTarget2)
                target(
                    region = region3,
                    TestForkTarget3
                ) withAction { ctx -> ctx }
            }
        }

        val declaration = scope
            .vertexDeclarations
            .filterIsInstance<ForkDeclaration<TestFork, TestContext>>()
            .singleOrNull()

        declaration shouldNotBeNull {
            id shouldBe TestFork
            containingStateId shouldBe TestContainingState
            targetIds shouldHaveSize 3
            targetIds[region1] shouldBe TestForkTarget1
            targetIds[region2] shouldBe TestForkTarget2
            targetIds[region3] shouldBe TestForkTarget3
        }
    }
}) {

    private object TestState
    private object TestContainingState
    private object TestContext
    private object TestEvent : Event
    private object TestTargetState
    private object TestChoice
    private object TestChoiceSelection
    private object TestFork
    private object TestForkTarget1
    private object TestForkTarget2
    private object TestForkTarget3

    private companion object {
        val region = RegionAtom.create()

        val testState =
            AtomicStateDeclaration<TestState, TestContext>(
                id = TestState,
                containingStateId = TestContainingState,
                region = region
            )

        fun scopeFactory() =
            RegionOrCompositeStateBuilderScopeImpl<TestState, TestContext>(
                containingStateId = TestContainingState,
                region = RegionAtom.create(),
            )
    }
}