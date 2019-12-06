/*
 * Copyright (c) 2018 ThanksMister LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.aikosoft.smarthouse.utility;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

/**
 * A SingleLiveEvent used for Alert dialog messages. Like a {@link SingleLiveEvent} but also prevents
 * null messages and uses a custom observer.
 * <p>
 * Note that only one observer is going to be notified of changes.
 * https://github.com/googlesamples/android-architecture/blob/dev-todo-mvvm-live/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/SnackbarMessage.java
 */
public class ToastMessage extends SingleLiveEvent<String> {
    public void observe(LifecycleOwner owner, final AlertObserver observer) {
        super.observe(owner, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String t) {
                if (TextUtils.isEmpty(t)) {
                    return;
                }
                observer.onNewMessage(t);
            }
        });
    }

    public interface AlertObserver {
        /**
         * Called when there is a new message to be shown.
         * @param alertMessage The new message, non-null.
         */
        void onNewMessage(String alertMessage);
    }
}