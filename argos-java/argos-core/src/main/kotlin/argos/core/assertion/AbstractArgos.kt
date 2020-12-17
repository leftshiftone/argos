package argos.core.assertion

import argos.api.ArgosOptions
import argos.api.AssertionGroup
import argos.api.Error
import argos.api.IAssertionResult
import gaia.sdk.api.extension.flatMap
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import reactor.util.function.Tuple3

abstract class AbstractArgos {
    protected fun argos(name: String, options: ArgosOptions, assertionGroups: List<AssertionGroup>):
            Publisher<IAssertionResult> {
        return Flowable.defer {
                options.getListeners().forEach { it.onBeforeAssertions(name) }
                Flowable.fromIterable(assertionGroups)
            }
            .doOnNext { assertionGroup ->
                options.getListeners().forEach { it.onBeforeAssertionGroup(assertionGroup) }
            }
            .flatMap { assertionGroup ->
                Flowable.fromIterable(assertionGroup.assertions)
                    .doOnNext { assertion ->
                        options.getListeners().forEach { it.onBeforeAssertion(assertion) }
                    }
                    .flatMap { assertion ->
                        Flowable.fromPublisher(assertion.assert(options))
                            .map { Pair(assertion, it) }
                            .onErrorReturn { Pair(assertion, Error(it)) }
                    }
                    .doOnNext { result ->
                        options.getListeners().forEach { it.onAfterAssertion(result.first, result.second) }
                    }
                    .map { Pair(assertionGroup, it.second) }
            }
            .doOnNext { assertionGroupResult ->
                options.getListeners().forEach {
                    it.onAfterAssertionGroup(assertionGroupResult.first)
                }
            }
            .doFinally {
                options.getListeners().forEach { it.onAfterAssertions() }
            }
            .map { assertionGroupResult -> assertionGroupResult.second }
    }
}