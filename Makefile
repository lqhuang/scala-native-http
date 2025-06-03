bloop:
	./mill bloop.install

fmt:
	./mill format

check:
	./mill 'scala-native-http[].fix' --check
