/// Copyright (c) 2012 Ecma International.  All rights reserved. 
/// Ecma International makes this code available under the terms and conditions set
/// forth on http://hg.ecmascript.org/tests/test262/raw-file/tip/LICENSE (the 
/// "Use Terms").   Any redistribution of this code must retain the above 
/// copyright and this notice and otherwise comply with the Use Terms.
/**
 * @path ch15/15.4/15.4.4/15.4.4.21/15.4.4.21-8-b-ii-2.js
 * @description Array.prototype.reduce - deleted properties in step 2 are visible here
 */


function testcase() {

        var obj = { 1: "accumulator", 2: "another" };

        Object.defineProperty(obj, "length", {
            get: function () {
                delete obj[1];
                return 3;
            },
            configurable: true
        });

        return "accumulator" !== Array.prototype.reduce.call(obj, function () { });
    }
runTestCase(testcase);
