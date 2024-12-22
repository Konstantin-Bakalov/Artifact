package artefact

object Demo {
  trait CRDT[T, S]:
    def value: T
    def state: S
    def merge(State: S): LWWReg[T]

  case class State[T](peer: String, timestamp: Long, value: T)

  class LWWReg[T](val id: String, val state: State[T]) extends CRDT[T, State[T]]:
    def value: T = state.value

    def set(value: T) = new LWWReg(id = this.id, state = State(peer = id, timestamp = state.timestamp + 1, value = value))

    def merge(foreignState: State[T]): LWWReg[T] =
      val foreignPeer = foreignState.peer;
      val foreignTimestamp = foreignState.timestamp
      val localPeer = state.peer
      val localTimestamp = state.timestamp

      if localTimestamp <= foreignTimestamp || localPeer > foreignPeer then new LWWReg(id, foreignState) else this

  def main(args: Array[String]): Unit = {
    var alice = new LWWReg("alice", State("alice", 1, "foo"))
    var bob = new LWWReg("bob", State("bob", 1, "bar"))
    
    bob = bob.set(bob.value + "!")
    bob = bob.set(bob.value + "?") // State("bob", 3, "bar!?")
    assert(bob.state.value.equals("bar!?"))
    alice = alice.merge(bob.state) // State("bob", 3, "bar!?")
    assert(alice.state.value.equals("bar!?"))
    alice = alice.set("milk")
    alice = alice.set(alice.value + " " + "and eggs") // State("alice", 5, "milk and eggs")
    bob = bob.set("peppers") // State("bob", 4, "peppers")
    alice = alice.merge(bob.state) // State("alice", 5, "milk and eggs")
    assert(alice.state.value.equals("milk and eggs"))
  }
}
