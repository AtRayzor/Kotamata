package builder

import dev.timray.kotomata.builder.RegionOrCompositeStateBuilderScopeImpl
import dev.timray.kotomata.builder.vertices.AtomicStateDeclaration
import dev.timray.kotomata.model.AtomicStateVertex
import dev.timray.kotomata.model.Event
import dev.timray.kotomata.model.RegionAtom
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async

class RegionOrCompositeStateBuilderScopeDslTests : ShouldSpec({
    should("return state vertex instance") {
        val scope =
            RegionOrCompositeStateBuilderScopeImpl<TestState, TestContext>(
                containingStateId = ContainingState,
                region = region,
            )

        val state = scope.state(TestState)

        state shouldBe testState
    }

    should("return state with actions") {
        val scope =
            RegionOrCompositeStateBuilderScopeImpl<TestState, TestContext>(
                containingStateId = ContainingState,
                region = RegionAtom.create(),
            )

        val state = with(scope) {
            state<TestState, TestContext>(TestState) {
                entry { TestContext }
                work { async { TestContext } }
                exit { TestContext }
            }
        } as? AtomicStateDeclaration<TestState, TestContext>

        state shouldNotBeNull {
            id shouldBe TestState
            entryAction.shouldNotBeNull()
            doAction.shouldNotBeNull()
            exitAction.shouldNotBeNull()
        }
    }
}) {
    private object TestState
    private object ContainingState
    private object TestContext
    private object TestEvent : Event

    companion object {
        private val region = RegionAtom.create()

        private val testState =
            AtomicStateDeclaration<TestState, TestContext>(
                id = TestState,
                containingStateId = ContainingState,
                region = region
            )
    }
}