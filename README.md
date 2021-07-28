JaCoCo Java Code Coverage Library
=================================

This JaCoCo fork introduces functionality to keep track of line execution counts. We like to dub it, Java Code Coverage w/ Counts Library (JaCoCoCo). The execution counts are reported in XML and HTML reporting on source code lines. Essentially, we have changed the underlying datastructure from a `boolean[]` to a number-based array and increment values on every execution. 

This version has been developed to be used in a master thesis project for dynamic analysis in production. Therefore, some of its implementation details might be specifically tailored to this project. The changes have been applied on top of `v0.8.7` of JaCoCo. Please find the various implementations and their descriptions at the following locations:

- Using overflowing integers: [branch](https://github.com/Badbond/jacoco/tree/track-line-execution-count-integer-overflow), [tag](https://github.com/Badbond/jacoco/releases/tag/integer-overflow).
- Using overflowing longs: [branch](https://github.com/Badbond/jacoco/tree/track-line-execution-count-long-overflow), [tag](https://github.com/Badbond/jacoco/releases/tag/long-overflow).
- Using `Math.min` to cap at `Integer.MAX_VALUE - 1`: [branch](https://github.com/Badbond/jacoco/tree/track-line-execution-count-math-min), [tag](https://github.com/Badbond/jacoco/releases/tag/math-min).
- Using double casting to cap at `Integer.MAX_VALUE`: [branch](https://github.com/Badbond/jacoco/tree/track-line-execution-count-double-cast), [tag](https://github.com/Badbond/jacoco/releases/tag/double-cast).
- Using BigInteger (WIP -- abandoned): [branch](https://github.com/Badbond/jacoco/tree/track-line-execution-count-big-int-wip).

---

[![Build Status](https://dev.azure.com/jacoco-org/JaCoCo/_apis/build/status/JaCoCo?branchName=master)](https://dev.azure.com/jacoco-org/JaCoCo/_build/latest?definitionId=1&branchName=master)
[![Build status](https://ci.appveyor.com/api/projects/status/g28egytv4tb898d7/branch/master?svg=true)](https://ci.appveyor.com/project/JaCoCo/jacoco/branch/master)
[![Maven Central](https://img.shields.io/maven-central/v/org.jacoco/jacoco.svg)](http://search.maven.org/#search|ga|1|g%3Aorg.jacoco)

JaCoCo is a free Java code coverage library distributed under the Eclipse Public
License. Check the [project homepage](http://www.jacoco.org/jacoco)
for downloads, documentation and feedback.

Please use our [mailing list](https://groups.google.com/forum/?fromgroups=#!forum/jacoco)
for questions regarding JaCoCo which are not already covered by the
[extensive documentation](http://www.jacoco.org/jacoco/trunk/doc/).

Note: We do not answer general questions in the project's issue tracker. Please use our [mailing list](https://groups.google.com/forum/?fromgroups=#!forum/jacoco) for this.
-------------------------------------------------------------------------
