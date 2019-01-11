package org.etudinsa.clavardage.users;

public interface UserObserver {
    void newUser(User newUser);

    void userLeaving(User userLeaving);
    
    void updatedUserList();
}
