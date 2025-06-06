TASK_TARGET := scala-native-http[]

.PHONY: bloop
bloop:
	./mill bloop.install

.PHONY: fmt
fmt:
	./mill format

fix-check:
	./mill ${TASK_TARGET}.fix --check

.PHONY: compile
compile:
	./mill ${TASK_TARGET}.compile

.PHONY: test
test:
	./mill ${TASK_TARGET}.test

.PHONY: clean
clean:
	./mill clean

debug-native:
	./mill show ${TASK_TARGET}.bridgeFullClassPath
	./mill show ${TASK_TARGET}.runClasspath
	./mill show ${TASK_TARGET}.nativeLink
	./mill show ${TASK_TARGET}.nativeBuildTarget
	./mill show ${TASK_TARGET}.nativeClang
	./mill show ${TASK_TARGET}.nativeClangPP
	./mill show ${TASK_TARGET}.nativeCompileOptions
	./mill show ${TASK_TARGET}.nativeDump
	./mill show ${TASK_TARGET}.nativeEmbedResources
	./mill show ${TASK_TARGET}.nativeGC
	./mill show ${TASK_TARGET}.nativeGCInput
	./mill show ${TASK_TARGET}.nativeIncrementalCompilation
	./mill show ${TASK_TARGET}.nativeIvyDeps
	./mill show ${TASK_TARGET}.nativeLTO
	./mill show ${TASK_TARGET}.nativeLTOInput
	./mill show ${TASK_TARGET}.nativeLinkStubs
	./mill show ${TASK_TARGET}.nativeLinkingOptions
	./mill show ${TASK_TARGET}.nativeMultithreading
	./mill show ${TASK_TARGET}.nativeOptimize
	./mill show ${TASK_TARGET}.nativeOptimizeInput
	./mill show ${TASK_TARGET}.nativeTarget
	./mill show ${TASK_TARGET}.nativeWorkdir

debug-test-native:
	./mill show ${TASK_TARGET}.test.bridgeFullClassPath
	./mill show ${TASK_TARGET}.test.runClasspath
	./mill show ${TASK_TARGET}.test.nativeLink
	./mill show ${TASK_TARGET}.test.nativeBuildTarget
	./mill show ${TASK_TARGET}.test.nativeClang
	./mill show ${TASK_TARGET}.test.nativeClangPP
	./mill show ${TASK_TARGET}.test.nativeCompileOptions
	./mill show ${TASK_TARGET}.test.nativeDump
	./mill show ${TASK_TARGET}.test.nativeEmbedResources
	./mill show ${TASK_TARGET}.test.nativeGC
	./mill show ${TASK_TARGET}.test.nativeGCInput
	./mill show ${TASK_TARGET}.test.nativeIncrementalCompilation
	./mill show ${TASK_TARGET}.test.nativeIvyDeps
	./mill show ${TASK_TARGET}.test.nativeLTO
	./mill show ${TASK_TARGET}.test.nativeLTOInput
	./mill show ${TASK_TARGET}.test.nativeLinkStubs
	./mill show ${TASK_TARGET}.test.nativeLinkingOptions
	./mill show ${TASK_TARGET}.test.nativeMultithreading
	./mill show ${TASK_TARGET}.test.nativeOptimize
	./mill show ${TASK_TARGET}.test.nativeOptimizeInput
	./mill show ${TASK_TARGET}.test.nativeTarget
	./mill show ${TASK_TARGET}.test.nativeWorkdir


cloc:
	cloc --vcs=git
