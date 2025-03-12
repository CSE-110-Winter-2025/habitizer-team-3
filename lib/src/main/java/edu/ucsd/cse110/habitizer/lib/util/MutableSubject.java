package edu.ucsd.cse110.habitizer.lib.util;

public interface MutableSubject<T> extends Subject<T> {

    void setValue(T value);

}
