package com.wyrnlab.jotdownthatmovie.Model;

import java.util.Comparator;

public class AudioVisualInterfacePopularityComparator implements Comparator<AudiovisualInterface>
{
    public int compare(AudiovisualInterface left, AudiovisualInterface right) {
        return right.popularity.compareTo(left.popularity);
    }
}
