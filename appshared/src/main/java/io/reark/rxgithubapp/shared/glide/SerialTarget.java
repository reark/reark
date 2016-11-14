/*
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.reark.rxgithubapp.shared.glide;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import static io.reark.reark.utils.Preconditions.checkNotNull;

public class SerialTarget<T> implements Target<T> {

    private volatile State<T> state = new State<>(NullTarget.empty());

    private static final AtomicReferenceFieldUpdater<SerialTarget, State> STATE_UPDATER
            = AtomicReferenceFieldUpdater.newUpdater(SerialTarget.class, State.class, "state");

    private static final class State<T> {

        final Target<T> target;

        State(Target<T> t) {
            target = t;
        }

        State<T> set(@NonNull final Target<T> t) {
            return new State<>(t);
        }
    }

    public void set(@NonNull final Target<T> s) {
        checkNotNull(s);

        State<T> oldState;
        State<T> newState;

        do {
            oldState = state;
            newState = oldState.set(s);
        } while (!STATE_UPDATER.compareAndSet(this, oldState, newState));

        oldState.target.onDestroy();
    }

    public Target<T> get() {
        return state.target;
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {
        state.target.onLoadStarted(placeholder);
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        state.target.onLoadFailed(e, errorDrawable);
    }

    @Override
    public void onResourceReady(T resource, GlideAnimation<? super T> glideAnimation) {
        state.target.onResourceReady(resource, glideAnimation);
    }

    @Override
    public void onLoadCleared(Drawable placeholder) {
        state.target.onLoadCleared(placeholder);
    }

    @Override
    public void getSize(SizeReadyCallback cb) {
        state.target.getSize(cb);
    }

    @Override
    public void setRequest(Request request) {
        state.target.setRequest(request);
    }

    @Override
    public Request getRequest() {
        return state.target.getRequest();
    }

    @Override
    public void onStart() {
        state.target.onStart();
    }

    @Override
    public void onStop() {
        state.target.onStop();
    }

    @Override
    public void onDestroy() {
        state.target.onDestroy();
    }
}
