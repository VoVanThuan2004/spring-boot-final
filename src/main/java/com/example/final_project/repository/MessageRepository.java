package com.example.final_project.repository;

import com.example.final_project.entity.Message;
import com.example.final_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
            "(m.sender.id = :userId2 AND m.receiver.id = :userId1) " +
            "ORDER BY m.dateTime ASC")
    List<Message> findConversation(@Param("userId1") int userId1, @Param("userId2") int userId2);

//    @Query("SELECT DISTINCT " +
//            "CASE WHEN m.sender.id = :adminId THEN m.receiver ELSE m.sender END " +
//            "FROM Message m " +
//            "WHERE m.sender.id = :adminId OR m.receiver.id = :adminId")
//    List<User> findUsersChattedWithAdmin(@Param("adminId") int adminId);


    @Query("SELECT DISTINCT m.sender FROM Message m WHERE m.receiver.id = :adminId")
    List<User> findSendersToAdmin(@Param("adminId") int adminId);

    @Query("SELECT DISTINCT m.receiver FROM Message m WHERE m.sender.id = :adminId")
    List<User> findReceiversFromAdmin(@Param("adminId") int adminId);
}
