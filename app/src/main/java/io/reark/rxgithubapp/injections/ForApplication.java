package io.reark.rxgithubapp.injections;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Pawel Polanski on 5/16/15.
 */
@Qualifier
@Retention(RUNTIME)
public @interface ForApplication {

}
