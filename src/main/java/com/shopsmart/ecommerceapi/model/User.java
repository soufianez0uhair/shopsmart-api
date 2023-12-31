package com.shopsmart.ecommerceapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "first_name"
    )
    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[a-zA-Z]{0,45}$", message = "Please use a valid first name")
    private String firstName;

    @Column(
            name = "last_name"
    )
    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[a-zA-Z]{0,45}$", message = "Please use a valid last name")
    private String lastName;

    @Column(
            length = 256
    )
    @Size(max = 256, message = "Please use a valid email")
    @NotBlank(message = "Email is required")
    @Email(message = "Please use a valid email")
    private String email;

    @Column(
            name = "phone_number"
    )
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @Column(
            length = 20
    )
    @Size(max = 20, message = "Password must not exceed 20 characters length")
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long", groups = SecondValidation.class)
    private String password;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "created_at"
    )
    private Date createdAt;

    @ManyToMany(
            fetch = FetchType.EAGER
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "users_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id")
    )
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        if(roles == null) {
            roles = new HashSet<>();
        }
        roles.add(role);
    }
}
