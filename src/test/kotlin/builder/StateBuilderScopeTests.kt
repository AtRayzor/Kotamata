package builder

import dev.timray.kotomata.builder.StateBuilderScopeImpl
import dev.timray.kotomata.builder.vertices.CompletionTransitionDeclaration
import dev.timray.kotomata.builder.vertices.SelfTransitionDeclaration
import dev.timray.kotomata.builder.vertices.TriggeredTransitionDeclaration
import dev.timray.kotomata.model.Event
import dev.timray.kotomata.model.Region
import dev.timray.kotomata.model.RegionAtom
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async

class StateBuilderScopeTests : ShouldSpec({

    should("build valid test state with actions") {
        val scope = createScope()
        with(scope) {
            entry { TestContext }
            work { async { TestContext } }
            exit { TestContext }
        }
        val declaration = scope.declaration

        declaration.id shouldBe TestState
        declaration.containingStateId shouldBe TestContainingState
        declaration.region shouldBe region
        declaration.entryAction.shouldNotBeNull()
        declaration.doAction.shouldNotBeNull()
        declaration.exitAction.shouldNotBeNull()
    }

    should("add targeted transition with guards") {
        val scope = createScope()

        with(scope) {
            on(TestEvent::class)
                .target(TestTargetState)
                .withAction { ctx, _ -> ctx }
                .withGuard { true }
        }
        val transition = scope.declaration.transitions.singleOrNull()
                as? TriggeredTransitionDeclaration

        transition shouldNotBeNull {
            target shouldBe TestTargetState
            action.shouldNotBeNull()
            guard.shouldNotBeNull()
        }
    }


    should("add self transition with guard and action") {
        val scope = createScope()

        with(scope) {
            on(TestEvent::class)
                .targetSelf()
                .withAction { ctx, _ -> ctx }
                .withGuard { true }
        }
        val transition = scope.declaration.transitions.singleOrNull()
                as? SelfTransitionDeclaration

        transition shouldNotBeNull {
            action.shouldNotBeNull()
            guard.shouldNotBeNull()
        }
    }


    should("add completion transition with action") {
        val scope = createScope()

        with(scope) {
            onCompletion()
                .target(TestTargetState)
                .withAction { ctx -> ctx }
        }
        val transition = scope.declaration.transitions.singleOrNull()
                as? CompletionTransitionDeclaration

        transition shouldNotBeNull {
            target shouldBe TestTargetState
            action.shouldNotBeNull()
        }
    }
}) {
    private object TestState
    private object TestTargetState
    private object TestContext
    private object TestContainingState
    private object TestEvent : Event

    private companion object {
        val region = RegionAtom.create()

        fun createScope() =
            StateBuilderScopeImpl<TestState, TestContext>(
                id = TestState,
                containingStateId = TestContainingState,
                region = region
            )
    }
}