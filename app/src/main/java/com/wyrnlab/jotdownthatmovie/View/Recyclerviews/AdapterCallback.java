package com.wyrnlab.jotdownthatmovie.View.Recyclerviews;

import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;

public interface AdapterCallback  {
    public void swipeCallback(AudiovisualInterface item);
    public void removeCallback(AudiovisualInterface item);
    public void undoCallback(AudiovisualInterface item);
}
