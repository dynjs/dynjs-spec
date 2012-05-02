// Copyright 2012 Google Inc.  All rights reserved.
// This code is governed by the BSD license found in the LICENSE file.

/**
 * @path intl402/ch11/11.2/11.2.1.js
 * @description Tests that the Intl.Collator prototype object exists and
 * is not writable, enumerable, or configurable.
 */

var testcase = function() {
  "use strict";

  var desc;

  if (!Intl.Collator.hasOwnProperty('prototype')) {
    $ERROR('Intl.Collator has no prototype property');
  }

  desc = Object.getOwnPropertyDescriptor(Intl.Collator, 'prototype');
  if (desc.writable === true) {
    $ERROR('Intl.Collator.prototype is writable.');
  }
  if (desc.enumerable === true) {
    $ERROR('Intl.Collator.prototype is enumerable.');
  }
  if (desc.configurable === true) {
    $ERROR('Intl.Collator.prototype is configurable.');
  }

  return true;
}
runTestCase(testcase);
