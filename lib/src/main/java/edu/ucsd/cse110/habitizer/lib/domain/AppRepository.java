package edu.ucsd.cse110.habitizer.lib.domain;

import edu.ucsd.cse110.habitizer.lib.util.Subject;

public interface AppRepository {
    Subject<App> find();
    void save(App app);
}
