bloop:
	./mill bloop.install

fmt:
	./mill format

check:
	./mill 'scala-native-http[].fix' --check

test:
	./mill 'scala-native-http[].test'

debug-native:
	./mill show scala-native-http[].runClasspath
	./mill show scala-native-http[].test.runClasspath
	./mill show scala-native-http[].test.nativeLink
	./mill show scala-native-http[].test.nativeBuildTarget
	./mill show scala-native-http[].test.nativeClang
	./mill show scala-native-http[].test.nativeClangPP
	./mill show scala-native-http[].test.nativeCompileOptions
	./mill show scala-native-http[].test.nativeDump
	./mill show scala-native-http[].test.nativeEmbedResources
	./mill show scala-native-http[].test.nativeGC
	./mill show scala-native-http[].test.nativeGCInput
	./mill show scala-native-http[].test.nativeIncrementalCompilation
	./mill show scala-native-http[].test.nativeIvyDeps
	./mill show scala-native-http[].test.nativeLTO
	./mill show scala-native-http[].test.nativeLTOInput
	./mill show scala-native-http[].test.nativeLinkStubs
	./mill show scala-native-http[].test.nativeLinkingOptions
	./mill show scala-native-http[].test.nativeMultithreading
	./mill show scala-native-http[].test.nativeOptimize
	./mill show scala-native-http[].test.nativeOptimizeInput
	./mill show scala-native-http[].test.nativeTarget
	./mill show scala-native-http[].test.nativeWorkdir

