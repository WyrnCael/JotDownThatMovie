package com.wyrnlab.jotdownthatmovie.Model;

import java.util.Comparator;

public class AudioVisualInterfaceVoreAverageComparator implements Comparator<AudiovisualInterface>
{
    public int compare(AudiovisualInterface left, AudiovisualInterface right) {
        return right.Rating.compareTo(left.Rating);
    }
}
