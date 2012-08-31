function testFailed(message) {
    $$$fail(message);
}

//function $PRINT(message) {
//
//}
//
//function $INCLUDE(message) { }
function $ERROR(message) {
    testFailed(message);
}
//
//function $FAIL(message) {
//    testFailed(message);
//}
//
function runTestCase(testcase) {
    var x = testcase();
    print(x);
}

var __globalObject = Function("return this;")();
function fnGlobalObject() {
     return __globalObject;
}
