import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import kotlin.concurrent.thread

class ViewModel {
    val state = mutableStateOf("init", object : SnapshotMutationPolicy<String> {
        override fun equivalent(a: String, b: String): Boolean {
            return a == b
        }

        override fun merge(previous: String, current: String, applied: String): String {
            return applied
        }
    })
}

fun main() {
    val viewModel = ViewModel()

    viewModel.state.value = "before snapshot"

    val snapshot = Snapshot.takeMutableSnapshot()
    // change from other thread
    thread {
        viewModel.state.value = "changes from other thread"
    }
    snapshot.enter {
        // wait change from other thread
        Thread.sleep(100)
        println("in snapshot before change:" + viewModel.state)
        viewModel.state.value = "change in snapshot"
        println("in snapshot after change:" + viewModel.state)
    }
    snapshot.apply()
    println("after apply:" + viewModel.state)

}
