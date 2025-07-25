MILL := ./mill
TASK_TARGET := scala-native-http[]


##############
# Toolcahins #
##############

.PHONY: bloop
bloop:
	${MILL} bloop.install

.PHONY: bloop
bsp:
	${MILL} --bsp-install

.PHONY: fmt
fmt:
	${MILL} format

install-mill-autocomp:
	${MILL} mill.tabcomplete/install
	source ~/.cache/mill/download/mill-completion.sh # MILL_SOURCE_COMPLETION_LINE

#########
# Build #
#########


fix-check:
	${MILL} ${TASK_TARGET}.fix --check

.PHONY: compile
compile:
	${MILL} ${TASK_TARGET}.compile

.PHONY: test
test:
	${MILL} ${TASK_TARGET}.test

.PHONY: clean
clean:
	${MILL} clean

debug-native:
	${MILL} show ${TASK_TARGET}.bridgeFullClassPath
	${MILL} show ${TASK_TARGET}.runClasspath
	${MILL} show ${TASK_TARGET}.nativeLink
	${MILL} show ${TASK_TARGET}.nativeBuildTarget
	${MILL} show ${TASK_TARGET}.nativeClang
	${MILL} show ${TASK_TARGET}.nativeClangPP
	${MILL} show ${TASK_TARGET}.nativeCompileOptions
	${MILL} show ${TASK_TARGET}.nativeDump
	${MILL} show ${TASK_TARGET}.nativeEmbedResources
	${MILL} show ${TASK_TARGET}.nativeGC
	${MILL} show ${TASK_TARGET}.nativeGCInput
	${MILL} show ${TASK_TARGET}.nativeIncrementalCompilation
	${MILL} show ${TASK_TARGET}.nativeIvyDeps
	${MILL} show ${TASK_TARGET}.nativeLTO
	${MILL} show ${TASK_TARGET}.nativeLTOInput
	${MILL} show ${TASK_TARGET}.nativeLinkStubs
	${MILL} show ${TASK_TARGET}.nativeLinkingOptions
	${MILL} show ${TASK_TARGET}.nativeMultithreading
	${MILL} show ${TASK_TARGET}.nativeOptimize
	${MILL} show ${TASK_TARGET}.nativeOptimizeInput
	${MILL} show ${TASK_TARGET}.nativeTarget
	${MILL} show ${TASK_TARGET}.nativeWorkdir

debug-test-native:
	${MILL} show ${TASK_TARGET}.test.bridgeFullClassPath
	${MILL} show ${TASK_TARGET}.test.runClasspath
	${MILL} show ${TASK_TARGET}.test.nativeLink
	${MILL} show ${TASK_TARGET}.test.nativeBuildTarget
	${MILL} show ${TASK_TARGET}.test.nativeClang
	${MILL} show ${TASK_TARGET}.test.nativeClangPP
	${MILL} show ${TASK_TARGET}.test.nativeCompileOptions
	${MILL} show ${TASK_TARGET}.test.nativeDump
	${MILL} show ${TASK_TARGET}.test.nativeEmbedResources
	${MILL} show ${TASK_TARGET}.test.nativeGC
	${MILL} show ${TASK_TARGET}.test.nativeGCInput
	${MILL} show ${TASK_TARGET}.test.nativeIncrementalCompilation
	${MILL} show ${TASK_TARGET}.test.nativeIvyDeps
	${MILL} show ${TASK_TARGET}.test.nativeLTO
	${MILL} show ${TASK_TARGET}.test.nativeLTOInput
	${MILL} show ${TASK_TARGET}.test.nativeLinkStubs
	${MILL} show ${TASK_TARGET}.test.nativeLinkingOptions
	${MILL} show ${TASK_TARGET}.test.nativeMultithreading
	${MILL} show ${TASK_TARGET}.test.nativeOptimize
	${MILL} show ${TASK_TARGET}.test.nativeOptimizeInput
	${MILL} show ${TASK_TARGET}.test.nativeTarget
	${MILL} show ${TASK_TARGET}.test.nativeWorkdir

#########
# Utils #
#########

cloc:
	cloc --vcs=git
