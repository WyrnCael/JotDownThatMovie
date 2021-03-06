/*
 * Copyright (C) 2017 Samuel Wall
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wyrnlab.jotdownthatmovie.ExternalLibraries.materialtaptagetprompt;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.annotation.StyleableRes;
import android.view.View;
import android.view.ViewGroup;

/**
 * Interface used to find resources required by {@link MaterialTapTargetPrompt}.
 */
public interface ResourceFinder
{
    /**
     * Finds a child view with the given identifier. Returns null if the
     * specified child view does not exist.
     *
     * @param resId the identifier of the view to find
     * @return The view with the given id or null.
     */
    @Nullable
    View findViewById(@IdRes int resId);

    /**
     * Get the view to add the prompt to.
     *
     * @return The view to add the prompt to.
     */
    @NonNull
    ViewGroup getPromptParentView();

    /**
     * Retrieve the Context that the prompt is running in.
     *
     * @return Context The Context used by the prompt.
     */
    @NonNull
    Context getContext();

    /**
     * Returns a Resources instance for the application's package.
     *
     * @return a Resources instance for the application's package
     */
    @NonNull
    Resources getResources();

    /**
     * Return the Theme object associated with {@link #getContext()}.
     */
    @NonNull
    Resources.Theme getTheme();

    /**
     * Returns a localized string from the application's package's
     * default string table.
     *
     * @param resId Resource id for the string
     * @return The string com.wyrnlab.jotdownthatmovie.Model.data associated with the resource, stripped of styled
     *         text information.
     */
    @NonNull
    String getString(@StringRes int resId);

    /**
     * Retrieve styled attribute information in {@link #getContext()} theme.  See
     * {@link android.content.res.Resources.Theme#obtainStyledAttributes(int, int[])}
     * for more information.
     *
     * @see android.content.res.Resources.Theme#obtainStyledAttributes(int, int[])
     */
    @NonNull
    TypedArray obtainStyledAttributes(@StyleRes int resId, @StyleableRes int[] attrs);

    /**
     * Returns a drawable object associated with a particular resource ID and
     * styled for the current theme.
     *
     * @param resId The desired resource identifier, as generated by the aapt
     *           tool. This integer encodes the package, type, and resource
     *           entry. The value 0 is an invalid identifier.
     * @return An object that can be used to draw this resource, or
     *         {@code null} if the resource could not be resolved.
     */
    @Nullable
    Drawable getDrawable(@DrawableRes int resId);
}
