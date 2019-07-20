# compute-graph

A Clojure 'kata': implementing a library for expressing computations as a dependency graph.

## Objective

See the tests in `[compute-graph.core-test](./test/compute_graph/core_test.clj)` for examples of the desired behaviour.

## Assignment

Implement the [`compute-graph.core/compute`](./src/compute_graph/core.clj) function. You may of course define other functions outside of this one, to help with the implementation!

We suggest proceeding with the following steps:

1. Write a 'naive' recursive implementation which may recompute some steps several times.
2. Add caching with a mutable data structure (e.g a Clojure atom or a `java.util.HashMap`) so that intermediary steps only get computed once.
3. Re-implement caching in a more functional way, by using an 'accumulator' argument instead of a mutable data structure.
4. Add cycles detection, throwing an error when a cycle is detected.
