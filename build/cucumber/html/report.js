$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("bdd/features/bddShowFullProfile.feature");
formatter.feature({
  "line": 1,
  "name": "API BDDs",
  "description": "",
  "id": "api-bdds",
  "keyword": "Feature"
});
formatter.scenarioOutline({
  "line": 4,
  "name": "Receive prameter values",
  "description": "",
  "id": "api-bdds;receive-prameter-values",
  "type": "scenario_outline",
  "keyword": "Scenario Outline",
  "tags": [
    {
      "line": 3,
      "name": "@Scenario-ProfileVisible"
    }
  ]
});
formatter.step({
  "line": 5,
  "name": "I use the pid \u003cpid\u003e , ids \"\u003cids\u003e\", vt \"\u003cvt\u003e\"",
  "keyword": "Given "
});
formatter.step({
  "line": 7,
  "name": "I request the api",
  "keyword": "When "
});
formatter.step({
  "line": 8,
  "name": "I should get a response with HTTP status code \u003cstatus\u003e",
  "keyword": "Then "
});
formatter.step({
  "line": 10,
  "name": "response includes the following in any order",
  "rows": [
    {
      "cells": [
        "data.items.bi.ag",
        "23"
      ],
      "line": 11
    }
  ],
  "keyword": "And "
});
formatter.examples({
  "line": 14,
  "name": "",
  "description": "",
  "id": "api-bdds;receive-prameter-values;",
  "rows": [
    {
      "cells": [
        "pid",
        "ids",
        "vt",
        "status"
      ],
      "line": 15,
      "id": "api-bdds;receive-prameter-values;;1"
    },
    {
      "cells": [
        "9061321",
        "4269425",
        "pd",
        "200"
      ],
      "line": 16,
      "id": "api-bdds;receive-prameter-values;;2"
    }
  ],
  "keyword": "Examples"
});
formatter.scenario({
  "line": 16,
  "name": "Receive prameter values",
  "description": "",
  "id": "api-bdds;receive-prameter-values;;2",
  "type": "scenario",
  "keyword": "Scenario Outline",
  "tags": [
    {
      "line": 3,
      "name": "@Scenario-ProfileVisible"
    }
  ]
});
formatter.step({
  "line": 5,
  "name": "I use the pid 9061321 , ids \"4269425\", vt \"pd\"",
  "matchedColumns": [
    0,
    1,
    2
  ],
  "keyword": "Given "
});
formatter.step({
  "line": 7,
  "name": "I request the api",
  "keyword": "When "
});
formatter.step({
  "line": 8,
  "name": "I should get a response with HTTP status code 200",
  "matchedColumns": [
    3
  ],
  "keyword": "Then "
});
formatter.step({
  "line": 10,
  "name": "response includes the following in any order",
  "rows": [
    {
      "cells": [
        "data.items.bi.ag",
        "23"
      ],
      "line": 11
    }
  ],
  "keyword": "And "
});
formatter.match({
  "arguments": [
    {
      "val": "9061321",
      "offset": 14
    },
    {
      "val": "4269425",
      "offset": 29
    },
    {
      "val": "pd",
      "offset": 43
    }
  ],
  "location": "APISteps.i_use_the_pid_ids_vt(int,String,String)"
});
formatter.result({
  "duration": 1058143190,
  "status": "passed"
});
formatter.match({
  "location": "APISteps.i_request_the_api()"
});
formatter.result({
  "duration": 1295652528,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "200",
      "offset": 46
    }
  ],
  "location": "APISteps.i_should_get_a_response_with_HTTP_status_code(int)"
});
formatter.result({
  "duration": 82148166,
  "error_message": "java.lang.AssertionError: 1 expectation failed.\nExpected status code \u003c200\u003e but was \u003c404\u003e.\n\n\tat sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)\n\tat sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)\n\tat sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)\n\tat java.lang.reflect.Constructor.newInstance(Constructor.java:423)\n\tat org.codehaus.groovy.reflection.CachedConstructor.invoke(CachedConstructor.java:83)\n\tat org.codehaus.groovy.reflection.CachedConstructor.doConstructorInvoke(CachedConstructor.java:77)\n\tat org.codehaus.groovy.runtime.callsite.ConstructorSite$ConstructorSiteNoUnwrap.callConstructor(ConstructorSite.java:84)\n\tat org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCallConstructor(CallSiteArray.java:60)\n\tat org.codehaus.groovy.runtime.callsite.AbstractCallSite.callConstructor(AbstractCallSite.java:235)\n\tat org.codehaus.groovy.runtime.callsite.AbstractCallSite.callConstructor(AbstractCallSite.java:247)\n\tat io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure.validate(ResponseSpecificationImpl.groovy:471)\n\tat io.restassured.internal.ResponseSpecificationImpl$HamcrestAssertionClosure$validate$1.call(Unknown Source)\n\tat org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCall(CallSiteArray.java:48)\n\tat org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:113)\n\tat org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:125)\n\tat io.restassured.internal.ResponseSpecificationImpl.validateResponseIfRequired(ResponseSpecificationImpl.groovy:643)\n\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.lang.reflect.Method.invoke(Method.java:498)\n\tat org.codehaus.groovy.runtime.callsite.PogoMetaMethodSite$PogoCachedMethodSiteNoUnwrapNoCoerce.invoke(PogoMetaMethodSite.java:210)\n\tat org.codehaus.groovy.runtime.callsite.PogoMetaMethodSite.callCurrent(PogoMetaMethodSite.java:59)\n\tat org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCallCurrent(CallSiteArray.java:52)\n\tat org.codehaus.groovy.runtime.callsite.AbstractCallSite.callCurrent(AbstractCallSite.java:154)\n\tat org.codehaus.groovy.runtime.callsite.AbstractCallSite.callCurrent(AbstractCallSite.java:166)\n\tat io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:122)\n\tat io.restassured.specification.ResponseSpecification$statusCode$0.callCurrent(Unknown Source)\n\tat org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCallCurrent(CallSiteArray.java:52)\n\tat org.codehaus.groovy.runtime.callsite.AbstractCallSite.callCurrent(AbstractCallSite.java:154)\n\tat org.codehaus.groovy.runtime.callsite.AbstractCallSite.callCurrent(AbstractCallSite.java:166)\n\tat io.restassured.internal.ResponseSpecificationImpl.statusCode(ResponseSpecificationImpl.groovy:130)\n\tat io.restassured.internal.ValidatableResponseOptionsImpl.statusCode(ValidatableResponseOptionsImpl.java:117)\n\tat bdd.steps.APISteps.i_should_get_a_response_with_HTTP_status_code(APISteps.java:48)\n\tat ✽.Then I should get a response with HTTP status code 200(bdd/features/bddShowFullProfile.feature:8)\n",
  "status": "failed"
});
formatter.match({
  "location": "APISteps.response_includes_the_following_in_any_order(DataTable)"
});
formatter.result({
  "status": "skipped"
});
});