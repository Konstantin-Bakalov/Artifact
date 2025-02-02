object Artifact {
  case class State[T](id: String, timestamp: Long, value: T)

  class LWWReg[T](id: String, state: State[T]) {
    def getValue: T = state.value

    def getState: State[T] = state

    def set(value: T) = new LWWReg(this.id, state = State(id, state.timestamp + 1, value))

    def merge(foreignState: State[T]): LWWReg[T] = {
      val foreignId = foreignState.id;
      val foreignTimestamp = foreignState.timestamp
      val localId = state.id
      val localTimestamp = state.timestamp

      if (localTimestamp > foreignTimestamp) return this
      if (localTimestamp == foreignTimestamp && localId > foreignId) return this
      new LWWReg(id, foreignState)
    }
  }

  def main(args: Array[String]): Unit = {
    var alice = new LWWReg("alice", State("alice", 1, "foo"))
    var bob = new LWWReg("bob", State("bob", 1, "bar"))

    println(s"Alice initial state: ${alice.getState}")
    println(s"Bob initial state: ${bob.getState}")

    bob = bob.set(bob.getValue + "!")
    bob = bob.set(bob.getValue + "?") // State("bob", 3, "bar!?")
    assert(bob.getValue.equals("bar!?"))
    assert(bob.getState.timestamp == 3)
    println(s"Bob updated his state: ${bob.getState}")

    alice = alice.merge(bob.getState) // State("bob", 3, "bar!?")
    assert(alice.getValue.equals("bar!?"))
    assert(alice.getState.timestamp == 3)
    assert(alice.getState.id == "bob")
    println(s"Alice merged Bob's state: ${alice.getState}")

    alice = alice.set("milk")
    alice = alice.set(alice.getValue + " " + "and eggs") // State("alice", 5, "milk and eggs")
    assert(alice.getValue == "milk and eggs")
    assert(alice.getState.timestamp == 5)
    assert(alice.getState.id == "alice")
    println(s"Alice updated her state: ${alice.getState}")

    bob = bob.set("peppers") // State("bob", 4, "peppers")
    assert(bob.getValue == "peppers")
    assert(bob.getState.timestamp == 4)
    assert(bob.getState.id == "bob")
    println(s"Bob updated his state: ${bob.getState}")

    alice = alice.merge(bob.getState) // State("alice", 5, "milk and eggs")
    assert(alice.getValue.equals("milk and eggs"))
    assert(alice.getState.timestamp == 5)
    assert(alice.getState.id == "alice")
    println(s"Alice merged Bob's state: ${alice.getState}")
  }
}
