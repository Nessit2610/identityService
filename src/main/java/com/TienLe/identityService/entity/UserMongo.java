package com.TienLe.identityService.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "users")  // Đặt tên collection trong MongoDB
@Data
@NoArgsConstructor
public class UserMongo {

    @Id  // Sửa lỗi: Dùng annotation của Spring Data MongoDB
    private String id;
    
    private String name;
    private String email;

    // Constructor có tham số
    public UserMongo(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
