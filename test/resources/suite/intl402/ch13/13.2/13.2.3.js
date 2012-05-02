// Copyright 2012 Google Inc.  All rights reserved.
// This code is governed by the BSD license found in the LICENSE file.

/**
 * @path intl402/ch13/13.2/13.2.3.js
 * @description Tests the internal properties of Intl.DateTimeFormat
 * @author: Roozbeh Pournader
 */

var testcase = function() {
  "use strict";

  var defaultLocale = (new Intl.LocaleList())[0];
  var supportedLocales = Intl.DateTimeFormat.supportedLocalesOf([defaultLocale]);
  
  if (supportedLocales.length < 1 || supportedLocales[0] != defaultLocale) {
    $ERROR('The default Locale is not supported by Intl.DateTimeFormat');
  }

  // FIXME: Find a way to check that [[relevantExtensionKeys]] === ['ca', 'nu']
  
  // FIXME: Find a way to check specified properties of [[localeData]]  
  
  return true;
}
runTestCase(testcase);
