package com.exarlabs.timescheduler.business.dagger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

@Scope
@Retention (RetentionPolicy.RUNTIME)
public @interface AppScope {}
