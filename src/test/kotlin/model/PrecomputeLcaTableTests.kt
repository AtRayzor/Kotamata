package model

import dev.timray.kotomata.model.*
import dev.timray.kotomata.utils.generateRandomId
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class PrecomputeLcaTableTests : ShouldSpec({
    should("have incoming edges and outgoing edges") {
        val graph = createLargeGraph()
        val cState1Node = graph.getOrNode(ContainerState1)
        val cState2Node = graph.getOrNode(ContainerState2)
        val cState3Node = graph.getOrNode(ContainerState3)
        val cState4Node = graph.getOrNode(ContainerState4)
        val cState5Node = graph.getOrNode(ContainerState5)
        val state1Node = graph.getOrNode(State1)
        val state2Node = graph.getOrNode(State1)
        val state3Node = graph.getOrNode(State1)
        val state4Node = graph.getOrNode(State1)

        fun OrNode.checkContainer() {
            val incomingEdges = graph.getIncomingEdges(this)
            val outgoingEdges = graph.getOutgoingEdges(this)

            incomingEdges shouldHaveSize 1
            outgoingEdges shouldHaveSize 1
        }

        fun OrNode.checkAtomicState() {
            val incomingEdges = graph.getIncomingEdges(this)
            val outgoingEdges = graph.getOutgoingEdges(this)

            incomingEdges shouldHaveSize 1
            outgoingEdges shouldHaveSize 0
        }

        cState1Node shouldNotBeNull { checkContainer() }
        cState2Node shouldNotBeNull { checkContainer() }
        cState3Node shouldNotBeNull { checkContainer() }
        cState4Node shouldNotBeNull { checkContainer() }
        cState5Node shouldNotBeNull { checkContainer() }
        state1Node shouldNotBeNull { checkAtomicState() }
        state2Node shouldNotBeNull { checkAtomicState() }
        state3Node shouldNotBeNull { checkAtomicState() }
        state4Node shouldNotBeNull { checkAtomicState() }
    }

    should("have correct predecessors and successors.") {
        val graph = createLargeGraph()
        val rootNode = graph.getRoot()
        val cState1Node = graph.getOrNode(ContainerState1)
        val cState2Node = graph.getOrNode(ContainerState2)
        val cState3Node = graph.getOrNode(ContainerState3)
        val cState4Node = graph.getOrNode(ContainerState4)
        val cState5Node = graph.getOrNode(ContainerState5)
        val state1Node = graph.getOrNode(State1)
        val state2Node = graph.getOrNode(State2)
        val state3Node = graph.getOrNode(State3)
        val state4Node = graph.getOrNode(State4)

        rootNode shouldNotBeNull {
            val predecessor = graph.orNodePredecessor(this)
            val successors = graph.orNodeSuccessors(this)

            predecessor.shouldBeNull()
            successors shouldHaveSize 2
        }

        cState1Node shouldNotBeNull {
            val predecessor = graph.orNodePredecessor(this)
            val successors = graph.orNodeSuccessors(this)

            predecessor shouldBe rootNode
            successors shouldHaveSize 2
        }

        cState2Node shouldNotBeNull {
            val predecessor = graph.orNodePredecessor(this)
            val successors = graph.orNodeSuccessors(this)

            predecessor shouldBe rootNode
            successors shouldHaveSize 1
        }

        cState3Node shouldNotBeNull {
            val predecessor = graph.orNodePredecessor(this)
            val successors = graph.orNodeSuccessors(this)

            predecessor shouldBe cState1Node
            successors shouldHaveSize 1
        }

        cState4Node shouldNotBeNull {
            val predecessor = graph.orNodePredecessor(this)
            val successors = graph.orNodeSuccessors(this)

            predecessor shouldBe cState1Node
            successors shouldHaveSize 2
        }


        cState5Node shouldNotBeNull {
            val predecessor = graph.orNodePredecessor(this)
            val successors = graph.orNodeSuccessors(this)

            predecessor shouldBe cState3Node
            successors shouldHaveSize 1
        }

        state1Node shouldNotBeNull {
            val predecessor = graph.orNodePredecessor(this)
            val successors = graph.orNodeSuccessors(this)

            predecessor shouldBe cState5Node
            successors.shouldBeEmpty()
        }

        state2Node shouldNotBeNull {
            val predecessor = graph.orNodePredecessor(this)
            val successors = graph.orNodeSuccessors(this)

            predecessor shouldBe cState4Node
            successors.shouldBeEmpty()
        }

        state3Node shouldNotBeNull {
            val predecessor = graph.orNodePredecessor(this)
            val successors = graph.orNodeSuccessors(this)

            predecessor shouldBe cState4Node
            successors.shouldBeEmpty()
        }

        state4Node shouldNotBeNull {
            val predecessor = graph.orNodePredecessor(this)
            val successors = graph.orNodeSuccessors(this)

            predecessor shouldBe cState2Node
            successors.shouldBeEmpty()
        }
    }

    should("build correct lca table") {
        val graph = createSmallGraph()
        val lcaTable = with(graph) { precomputeLcaTable() }

        val nodes = graph.getAllOrNodes()
        val sets = nodes.flatMapIndexed { idx, node1 ->
            nodes.takeLast(nodes.size - idx).map { node2 ->
                setOf(node1, node2)
            }
        }.toSet()
        val rootNode = graph.getRoot()
        val cState1Node = graph.getOrNode(ContainerState1)!!
        val cState2Node = graph.getOrNode(ContainerState2)!!
        val state1Node = graph.getOrNode(State1)!!
        val state2Node = graph.getOrNode(State2)!!

        lcaTable shouldHaveSize 15
        lcaTable.keys shouldBe sets

        nodes.forEach { node ->
            lcaTable[setOf(rootNode, node)] shouldBe rootNode
            lcaTable[setOf(node)] shouldBe node
        }

        lcaTable[setOf(cState1Node, cState2Node)] shouldBe rootNode
        lcaTable[setOf(cState1Node, state1Node)] shouldBe cState1Node
        lcaTable[setOf(cState1Node, state2Node)] shouldBe cState1Node
        lcaTable[setOf(cState2Node, state1Node)] shouldBe rootNode
        lcaTable[setOf(cState2Node, state2Node)] shouldBe rootNode
        lcaTable[setOf(state1Node, state2Node)] shouldBe cState1Node
    }
}
) {
    private object DataContext
    private object State1
    private object State2
    private object State3
    private object State4
    private object ContainerState1
    private object ContainerState2
    private object ContainerState3
    private object ContainerState4
    private object ContainerState5


    private companion object {
        val rootState = CompositeStateVertex<String, DataContext>(generateRandomId())
        val stateObj1 = AtomicStateVertex<State1, DataContext>(State1)
        val stateObj2 = AtomicStateVertex<State2, DataContext>(State2)
        val stateObj3 = AtomicStateVertex<State3, DataContext>(State3)
        val stateObj4 = AtomicStateVertex<State4, DataContext>(State4)
        val containerStateObj1 =
            CompositeStateVertex<ContainerState1, DataContext>(ContainerState1)
        val containerStateObj2 =
            CompositeStateVertex<ContainerState2, DataContext>(ContainerState2)
        val containerStateObj3 =
            CompositeStateVertex<ContainerState3, DataContext>(ContainerState3)
        val containerStateObj4 =
            CompositeStateVertex<ContainerState4, DataContext>(ContainerState4)
        val containerStateObj5 =
            CompositeStateVertex<ContainerState5, DataContext>(ContainerState5)
        val region = RegionAtom.create()

        fun createSmallGraph(): HierarchicalGraph {
            val graph =
                MutableXHiGraph(rootState = (rootState as CompositeStateVertex<String, Any?>))
            val compositeState1Node = graph.addCompositeStateNode(
                containerStateObj1,
                rootState,
                region
            )

            val compositeState2Node = graph.addCompositeStateNode(
                containerStateObj2,
                rootState,
                region
            )

            graph.addOrNode(
                stateObj1,
                compositeState1Node.payload as CompositeStateVertex<Any, Any?>,
                region
            )
            graph.addOrNode(
                stateObj2,
                compositeState1Node.payload as CompositeStateVertex<Any, Any?>,
                region
            )

            return graph
        }

        fun createLargeGraph(): HierarchicalGraph {
            val graph =
                MutableXHiGraph(rootState = (rootState as CompositeStateVertex<String, Any?>))
            val compositeState1Node = graph.addCompositeStateNode(
                containerStateObj1,
                rootState,
                region
            )
            val compositeState2Node = graph.addCompositeStateNode(
                containerStateObj2,
                rootState,
                region
            )
            val compositeState3Node = graph.addCompositeStateNode(
                containerStateObj3,
                compositeState1Node.payload as CompositeStateVertex<Any, Any?>,
                region
            )
            val compositeState4Node = graph.addCompositeStateNode(
                containerStateObj4,
                compositeState1Node.payload as CompositeStateVertex<Any, Any?>,
                region
            )
            val compositeState5Node = graph.addCompositeStateNode(
                containerStateObj5,
                compositeState3Node.payload as CompositeStateVertex<Any, Any?>,
                region
            )
            graph.addOrNode(
                stateObj1,
                compositeState5Node.payload as CompositeStateVertex<Any, Any?>,
                region
            )

            graph.addOrNode(
                stateObj2,
                compositeState4Node.payload as CompositeStateVertex<Any, Any?>,
                region
            )
            graph.addOrNode(
                stateObj3,
                compositeState4Node.payload as CompositeStateVertex<Any, Any?>,
                region
            )

            graph.addOrNode(
                stateObj4,
                compositeState2Node.payload as CompositeStateVertex<Any, Any?>,
                region
            )

            return graph
        }
    }
}